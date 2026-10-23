package com.example.trandify1.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.trandify1.MainActivity
import com.example.trandify1.data.db.TrendifyyDatabaseHelper
import com.example.trandify1.databinding.ActivityAuthBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var dbHelper: TrendifyyDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = TrendifyyDatabaseHelper(this)

        binding.tabLayoutAuthActivity.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    binding.layoutSigninAct.visibility = View.VISIBLE
                    binding.layoutSignupAct.visibility = View.GONE
                } else {
                    binding.layoutSigninAct.visibility = View.GONE
                    binding.layoutSignupAct.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.btnSigninActSubmit.setOnClickListener {
            val email = binding.etSigninActEmail.text.toString().trim()
            val pass = binding.etSigninActPassword.text.toString().trim()
            if (email.isEmpty() || pass.isEmpty()) {
                Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (dbHelper.authenticateUser(email, pass)) {
                saveLoggedInEmail(email)
                navigateToMain()
            } else {
                Snackbar.make(binding.root, "Invalid email or password", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnFingerprintSignin.setOnClickListener {
            showBiometricPrompt()
        }

        binding.btnSignupActSubmit.setOnClickListener {
            val name = binding.etSignupActName.text.toString().trim()
            val email = binding.etSignupActEmail.text.toString().trim()
            val phone = binding.etSignupActPhone.text.toString().trim()
            val pass = binding.etSignupActPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
                Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = dbHelper.registerUser(
                email = email,
                pass = pass,
                name = name,
                phone = phone,
                address = "742 Evergreen Terrace, NY",
                dob = "2000-01-01"
            )

            if (success) {
                saveLoggedInEmail(email)
                navigateToMain()
            } else {
                Snackbar.make(binding.root, "Registration failed or email already exists", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnSkipGuest.setOnClickListener {
            navigateToMain()
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext, "Fingerprint Authentication succeeded!", Toast.LENGTH_SHORT).show()
                    saveLoggedInEmail("alex.morgan@trendifyy.com")
                    navigateToMain()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed. Try again.", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for Trendifyy")
            .setSubtitle("Log in using your fingerprint or biometric credential")
            .setNegativeButtonText("Use Account Password")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun saveLoggedInEmail(email: String) {
        getSharedPreferences("trendifyy_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("logged_in_email", email)
            .apply()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}