package com.example.sym.others

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sym.vms.ProductViewModel

class ProductFactory(val productRepository: ProductRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductViewModel(productRepository) as T
    }
}