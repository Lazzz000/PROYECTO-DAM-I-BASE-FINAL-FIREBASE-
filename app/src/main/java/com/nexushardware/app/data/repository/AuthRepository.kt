package com.nexushardware.app.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

import com.nexushardware.app.data.model.User
import kotlinx.coroutines.tasks.await


class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val auth = FirebaseAuth.getInstance()

    //Autentica al usuario en Firebase usando el idToken obtenido de Google Sign-In
    suspend fun firebaseAuthWithGoogle(idToken: String): User? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        // Realiza el login en Firebase de manera asíncrona
        val authResult = firebaseAuth.signInWithCredential(credential).await()
        val user = authResult.user

        // Mapeamos el FirebaseUser al modelo User
        return user?.let {
            User(
                id = it.uid,
                email = it.email,
                nombreCompleto = it.displayName,
                telefono = it.phoneNumber,
                photo = it.photoUrl?.toString(),
                esAdmin = false
            )
        }
    }

    //Cierra la sesión del usuario actualmente autenticado en Firebase.
    fun logout() {
        firebaseAuth.signOut()
    }

    fun login(email: String, password: String,onResult: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    //Obtiene el usuario actualmente autenticado en Firebase.
    fun getCurrentUser(): User? {
        val user = firebaseAuth.currentUser
        return user?.let {
            User(
                id = it.uid,
                email = it.email,
                nombreCompleto = it.displayName,
                telefono = it.phoneNumber,
                photo = it.photoUrl?.toString(),
                esAdmin = false
            )
        }
    }
    fun register(
        nombre: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser

                    val profileUpdates = userProfileChangeRequest {
                        displayName = nombre
                    }

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->

                            if (updateTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                onResult(false, updateTask.exception?.message)
                            }
                        }

                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }
}