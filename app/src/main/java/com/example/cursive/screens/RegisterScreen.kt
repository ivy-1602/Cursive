package com.example.cursive.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cursive.databinding.ActivityRegisterBinding
import com.example.cursive.models.User  // ← ADD THIS LINE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterScreen : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInputs(name, email, password, confirmPassword)) {
                registerUser(name, email, password)
            }
        }

        binding.tvLogin.setOnClickListener {
            finish() // Go back to login screen
        }
    }

    private fun validateInputs(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (name.isEmpty()) {
            binding.tilName.error = "Name is required"
            return false
        }
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            return false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            return false
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            return false
        }
        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            return false
        }
        return true
    }

    private fun registerUser(name: String, email: String, password: String) {
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.btnRegister.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User created successfully, now save to Firestore
                    val userId = auth.currentUser?.uid ?: ""
                    val user = User(
                        uid = userId,
                        name = name,
                        email = email,
                        currentStreak = 0,
                        longestStreak = 0,
                        totalEntries = 0,
                        createdAt = System.currentTimeMillis()
                    )

                    saveUserToFirestore(user)
                } else {
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun saveUserToFirestore(user: User) {
        firestore.collection("users")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                binding.progressBar.visibility = android.view.View.GONE
                Toast.makeText(this, "Welcome to Cursive, ${user.name}!", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = android.view.View.GONE
                binding.btnRegister.isEnabled = true
                Toast.makeText(
                    this,
                    "Failed to save user data: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeScreen::class.java))
        finish()
    }
}