package com.example.sym

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.sym.others.Navigation
import com.example.sym.others.ProductFactory
import com.example.sym.others.ProductRepository
import com.example.sym.ui.theme.SymTheme
import com.example.sym.vms.AuthViewModel
import com.example.sym.vms.ProductViewModel
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var authViewModel: AuthViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize ViewModels
        authViewModel = AuthViewModel()
        val repository = ProductRepository()
        val productFactory = ProductFactory(repository)
        productViewModel = ViewModelProvider(this, productFactory)[ProductViewModel::class.java]


        // Set up the UI
        setContent {
            SymTheme {
                Navigation(
                    authViewModel = authViewModel,
                    productViewModel = productViewModel
                )
            }

//            productViewModel.getTodaysCollection()
        }
    }

}