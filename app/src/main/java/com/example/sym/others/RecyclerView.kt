package com.example.sym.others

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.sym.objs.Product
import com.example.sym.vms.ProductViewModel
import kotlinx.coroutines.launch

@Composable
fun SwipeableCard(
    product: Product,
    modifier: Modifier = Modifier,
    onSwipe: () -> Unit = {}
) {
    var offsetX by remember { mutableStateOf(0f) }
    val swipeThreshold = with(LocalDensity.current) { 100.dp.toPx() }

    // Animation state
    val animatedOffset = remember { Animatable(0f) }

    // Add coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // Add state to track if swipe action has been triggered
    var hasTriggeredSwipe by remember { mutableStateOf(false) }

    LaunchedEffect(offsetX) {
        if (!hasTriggeredSwipe && kotlin.math.abs(offsetX) >= swipeThreshold) {
            hasTriggeredSwipe = true
            onSwipe()
            animatedOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            )
            offsetX = 0f
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .offset(x = animatedOffset.value.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        hasTriggeredSwipe = false // Reset the trigger state when starting a new drag
                    },
                    onDragEnd = {
                        offsetX = 0f
                        coroutineScope.launch {
                            animatedOffset.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    },
                    onDragCancel = {
                        offsetX = 0f
                        coroutineScope.launch {
                            animatedOffset.animateTo(0f)
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            offsetX += dragAmount
                            animatedOffset.snapTo(offsetX)
                        }
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = product.name)
            Text(text = "₹${product.price}")
        }
    }
}

@Composable
fun CardList(products: State<List<Product>?>, productViewModel: ProductViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        products.value?.forEach { product ->
            SwipeableCard(
                product = product,
                onSwipe = {
                    productViewModel.addToCart(product)
                }
            )
        }
    }
}