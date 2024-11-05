package com.example.sym.others

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.sym.objs.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class ProductRepository {
    private val database = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Cache the current user's email to ensure consistency
    private val currentUserEmail: String?
        get() = auth.currentUser?.email

    suspend fun getProducts(): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            // Verify user is authenticated
            val email = currentUserEmail ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )

            val documents = database.collection("Kirana")
                .get()
                .await()

            val products = documents.mapNotNull { document ->
                val name = document.getString("name")
                val price = document.getString("price")
                if (name != null && price != null) {
                    Product(name = name, price = price.toDouble())
                } else null
            }
            Result.success(products)
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error fetching products", e)
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun checkout(list: List<Product>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val email = currentUserEmail ?: return@withContext Result.failure(
                IllegalStateException("User not authenticated")
            )

            val currentDate = LocalDate.now().toString()
            val currentDateTime = "${LocalDateTime.now()}_${UUID.randomUUID()}"

            // Structure the data with more metadata
            val data = hashMapOf(
                "items $currentDateTime" to list
            )

            database.collection("User")
                .document(email)
                .collection("Transactions")
                .document(currentDate)
                .set(hashMapOf(currentDateTime to data), SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error during checkout", e)
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTodaysCollection(): Result<List<List<Pair<String, Double>>>> =
        withContext(Dispatchers.IO) {
            try {
                val email = currentUserEmail ?: return@withContext Result.failure(
                    IllegalStateException("User not authenticated")
                )

                val currentDate = LocalDate.now().toString()
                val document = database.collection("User")
                    .document(email)
                    .collection("Transactions")
                    .document(currentDate)
                    .get()
                    .await()

                Log.d("Doc", "${document.data}")

                if (document.exists()) {
                    val groupedProducts = GrpData.extractGroupedProductDetails(document.data.toString())
                    Result.success(groupedProducts)
                } else {
                    Result.success(emptyList())
                }
            } catch (e: Exception) {
                Log.e("ProductRepository", "Error fetching today's collection", e)
                Result.failure(e)
            }
        }

    suspend fun allTransactions(): Result<List<List<List<Pair<String, Double>>>>> =
        withContext(Dispatchers.IO) {
            try {
                val email = currentUserEmail ?: return@withContext Result.failure(
                    IllegalStateException("User not authenticated")
                )

                val querySnapshot = database.collection("User")
                    .document(email)
                    .collection("Transactions")
                    .get()
                    .await()

                val transactionDetails = querySnapshot.documents.mapNotNull { document ->
                    val rawData = document.data.toString()
                    GrpData.extractGroupedProductDetails(rawData)
                }

                Result.success(transactionDetails)
            } catch (e: Exception) {
                Log.e("ProductRepository", "Error fetching all transactions", e)
                Result.failure(e)
            }
        }
}