package uasb.c14220127.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore

class MainActivityLogin : AppCompatActivity() {
    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var registerText: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.login)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Link UI elements
        usernameField = findViewById(R.id.username)
        passwordField = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        registerText = findViewById(R.id.signupText)

        // Login button listener
        loginButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()


            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this@MainActivityLogin,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Query Firestore for the user with matching username
            firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(
                            this@MainActivityLogin,
                            "Username not found",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@addOnSuccessListener
                    }

                    // Get user document
                    val userDoc = documents.documents[0]
                    val email = userDoc.getString("email") ?: ""
                    if (email.isEmpty()) {
                        Toast.makeText(this, "Email not found for this username", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    // Attempt sign in with email and password
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            val intent = Intent(this@MainActivityLogin, HomePageActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@MainActivityLogin,
                                "Invalid password",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("Login", "Error: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@MainActivityLogin,
                        "Error during login: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        registerText.setOnClickListener {
            val intent = Intent(this@MainActivityLogin, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
