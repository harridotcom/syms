package com.example.sym.vms

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

class AuthViewModel: ViewModel() {
    var auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    private var _authState = MutableLiveData<AuthState>()
    var authState: LiveData<AuthState> = _authState


    init {
        checkUserStatus()
    }

    fun checkUserStatus(){
        if (auth.currentUser != null){
            _authState.value = AuthState.Authenticated
        }else{
            _authState.value = AuthState.UnAuthenticated
        }
    }

    fun login(email: String, password: String){
        if (email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password cannot be empty")
        }
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                _authState.value = AuthState.Authenticated
            }else if (task.isCanceled){
                _authState.value = AuthState.Error("Try Later")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun register(email: String, password: String){
        if (email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password cannot be empty")
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                _authState.value = AuthState.Authenticated

                val userData = hashMapOf("email" to email, "created_at" to System.currentTimeMillis())

                val userDocRef = firestore.collection("User").document(email)

                userDocRef.set(userData)
                .addOnSuccessListener {
                    val initialTransaction = hashMapOf(
                        "transaction" to "Welcome On Board"
                    )
                    userDocRef.collection("Transactions")
                        .document(LocalDate.now().toString())
                        .set(initialTransaction)
                }


            }else if (task.isCanceled){
                _authState.value = AuthState.Error("Try Later")
            }
        }
    }

    fun signout(){
        _authState.value = AuthState.UnAuthenticated
        auth.signOut()
    }

}

sealed class AuthState{
    object Authenticated: AuthState()
    object UnAuthenticated: AuthState()
    data class Error(var message: String): AuthState()
}