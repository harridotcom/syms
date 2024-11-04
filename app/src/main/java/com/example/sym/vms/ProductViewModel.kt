package com.example.sym.vms

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sym.objs.Product
import com.example.sym.others.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(private val productRepository: ProductRepository): ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _productsCart = MutableLiveData<List<Product>>(emptyList())
    val productsCart: LiveData<List<Product>> = _productsCart

    private val _todayProducts = MutableLiveData<List<List<Pair<String, Double>>>>()
    val todayProducts: LiveData<List<List<Pair<String, Double>>>> = _todayProducts

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val productList = productRepository.getProducts()
            _products.value = productList
        }
    }

    fun addToCart(product: Product){
        val updatedCart = _productsCart.value.orEmpty().toMutableList()
        updatedCart.add(product)
        _productsCart.value = updatedCart
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkOut(list: List<Product>){
        productRepository.checkout(list)
    }

    fun clearCart(){
        _productsCart.value = emptyList()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodaysCollection() {
        productRepository.getTodaysCollection(
            onSuccess = { groupedProducts ->
                _todayProducts.value = groupedProducts // Update LiveData with the result
            },
            onFailure = { exception ->
                _error.value = exception.message ?: "Unknown error occurred" // Update LiveData with the error message
            }
        )
    }
}