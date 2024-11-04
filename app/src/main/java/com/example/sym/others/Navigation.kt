package com.example.sym.others

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sym.pages.HomePage
import com.example.sym.pages.LoginPage
import com.example.sym.pages.RegisterPage
import com.example.sym.pages.ShowCart
import com.example.sym.pages.TodayCollection
import com.example.sym.vms.AuthViewModel
import com.example.sym.vms.ProductViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel
) {
    var navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login"){
            LoginPage(navController=navController, authViewModel = authViewModel)
        }
        composable("register"){
            RegisterPage(navController=navController, authViewModel = authViewModel)
        }
        composable("home"){
            HomePage(navController=navController, authViewModel = authViewModel, productViewModel = productViewModel)
        }
        composable("showcart"){
            ShowCart(productViewModel = productViewModel, navController = navController)
        }
        composable("todayCollection"){
            TodayCollection(productViewModel = productViewModel)
        }
    }
}
