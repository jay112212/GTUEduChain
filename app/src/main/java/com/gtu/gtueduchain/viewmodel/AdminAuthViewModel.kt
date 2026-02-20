package com.gtu.gtueduchain.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminAuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var isLoggedIn by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    val currentUserEmail: String?
        get() = auth.currentUser?.email

    init {
        checkExistingLogin()
    }

    private fun checkExistingLogin() {
        val user = auth.currentUser

        if (user != null) {
            db.collection("admins")
                .document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    isLoggedIn = doc.exists()
                }
                .addOnFailureListener {
                    isLoggedIn = false
                }
        } else {
            isLoggedIn = false
        }
    }

    fun login(email: String, password: String) {
        error = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkExistingLogin()
            }
            .addOnFailureListener {
                error = it.message
            }
    }

    fun logout() {
        auth.signOut()
        isLoggedIn = false
    }
}
