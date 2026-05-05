package com.example.makeupstoreapp.viewmodel
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow(auth.currentUser != null)
    val authState: StateFlow<Boolean> = _authState

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    fun getFriendlyErrorMessage(error: Exception): String {
        val message = error.message ?: ""

        return when {
            message.contains("credential is incorrect", true) ||
                    message.contains("malformed", true) ||
                    message.contains("expired", true) ||
                    message.contains("INVALID_LOGIN_CREDENTIALS", true) ->
                "Emailul sau parola sunt incorecte."

            message.contains("badly formatted", true) ->
                "Emailul nu este valid."

            message.contains("no user record", true) ->
                "Nu există cont cu acest email."

            message.contains("email address is already in use", true) ->
                "Există deja un cont cu acest email."

            message.contains("password should be at least", true) ->
                "Parola trebuie să aibă minimum 6 caractere."

            message.contains("network error", true) ->
                "Eroare de internet. Verifică conexiunea."

            else ->
                "A apărut o eroare. Încearcă din nou."
        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            _message.value = "Completează emailul și parola."
            return
        }

        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener {
                _authState.value = true
                _message.value = ""
                onSuccess()
            }
            .addOnFailureListener { error ->
                _message.value = getFriendlyErrorMessage(error)
            }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _message.value = "Completează numele, emailul și parola."
            return
        }

        if (password.length < 6) {
            _message.value = "Parola trebuie să aibă minimum 6 caractere."
            return
        }

        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val userId = result.user?.uid ?: return@addOnSuccessListener

                val userData = hashMapOf(
                    "name" to name.trim(),
                    "email" to email.trim(),
                    "role" to "client"
                )

                db.collection("users")
                    .document(userId)
                    .set(userData)
                    .addOnSuccessListener {
                        _authState.value = true
                        _message.value = ""
                        onSuccess()
                    }
                    .addOnFailureListener {
                        _message.value = "Contul a fost creat, dar datele profilului nu au fost salvate."
                    }
            }
            .addOnFailureListener { error ->
                _message.value = getFriendlyErrorMessage(error)
            }
    }

    fun logout() {
        auth.signOut()
        _authState.value = false
    }

    fun clearMessage() {
        _message.value = ""
    }
}

