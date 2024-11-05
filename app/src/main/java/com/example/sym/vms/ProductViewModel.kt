package com.example.sym.vms

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sym.objs.Product
import com.example.sym.others.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _productsCart = MutableLiveData<List<Product>>(emptyList())
    val productsCart: LiveData<List<Product>> = _productsCart

    private val _todayProducts = MutableLiveData<List<List<Pair<String, Double>>>>()
    val todayProducts: LiveData<List<List<Pair<String, Double>>>> = _todayProducts

    private val _allProducts = MutableLiveData<List<List<List<Pair<String, Double>>>>>()
    val allProducts: LiveData<List<List<List<Pair<String, Double>>>>> = _allProducts

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    init {
        loadProducts()
        Log.d("pussi", "${products.value}")
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                productRepository.getProducts()
                    .onSuccess { productList ->
                        _products.value = productList
                    }
                    .onFailure { exception ->
                        _error.value = exception.message ?: "Failed to load products"
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart(product: Product) {
        val updatedCart = _productsCart.value.orEmpty().toMutableList()
        updatedCart.add(product)
        _productsCart.value = updatedCart
    }

    fun deleteProductFromCart(product: Product) {
        val updatedCart = _productsCart.value.orEmpty().toMutableList()
        updatedCart.remove(product)
        _productsCart.value = updatedCart
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkOut(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val cartItems = _productsCart.value ?: emptyList()
                if (cartItems.isEmpty()) {
                    _error.value = "Cart is empty"
                    return@launch
                }

                productRepository.checkout(cartItems)
                    .onSuccess {
                        clearCart()
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                    }
                    .onFailure { exception ->
                        _error.value = exception.message ?: "Checkout failed"
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun clearCart(){
        _productsCart.value = emptyList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodaysCollection() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                productRepository.getTodaysCollection()
                    .onSuccess { groupedProducts ->
                        _todayProducts.value = groupedProducts
                    }
                    .onFailure { exception ->
                        _error.value = exception.message ?: "Failed to fetch today's collection"
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAllTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                productRepository.allTransactions()
                    .onSuccess { transactions ->
                        _allProducts.value = transactions
                    }
                    .onFailure { exception ->
                        _error.value = exception.message ?: "Failed to fetch transactions"
                        Log.e("ProductViewModel", "Failed to retrieve transactions", exception)
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }
}