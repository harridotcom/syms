package com.example.sym.others

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.sym.objs.Product
import com.example.sym.others.GrpData.extractGroupedProductDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class ProductRepository {

    val database = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val ProductList = mutableListOf<Product>()

    suspend fun getProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            val documents = database.collection("Kirana")
                .get()
                .await()

            return@withContext documents.mapNotNull { document ->
                val name = document.getString("name")
                val price = document.getString("price")
                if (name != null && price != null) {
                    Product(name = name, price = price.toDouble())
                } else null
            }
        } catch (e: Exception) {
            // Handle error
            return@withContext emptyList()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun checkout(list: List<Product>){

        val currentDate = LocalDate.now().toString()
        val currentDateTime = "${LocalDateTime.now()}_${UUID.randomUUID()}"

        val data = hashMapOf(
            "items $currentDateTime" to list
        )

        currentUser?.email?.let {
            database.collection("User")
                .document(it).collection("Transactions")
                .document(currentDate)
                .set(data, SetOptions.merge())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodaysCollection(onSuccess: (List<List<Pair<String, Double>>>) -> Unit, onFailure: (Exception) -> Unit) {
        Log.d("Today", "Pressed")

        val currentDate = LocalDate.now().toString()

        currentUser?.email?.let { email ->
            database.collection("User")
                .document(email)
                .collection("Transactions")
                .document(currentDate)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Call extractGroupedProductDetails with the document data and pass the result to onSuccess
                        val groupedProducts = extractGroupedProductDetails(document.data.toString())
                        onSuccess(groupedProducts)
                    } else {
                        onSuccess(emptyList()) // Pass an empty list if no document is found
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Failed", "Failed")
                    onFailure(exception) // Pass the exception to the onFailure callback
                }
        }
    }
}