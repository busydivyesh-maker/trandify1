package com.example.trandify1.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trandify1.data.db.TrendifyyDatabaseHelper
import com.example.trandify1.databinding.DialogAuthBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

class AuthBottomSheetDialogFragment(
    private val onAuthSuccess: (String) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogAuthBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: TrendifyyDatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = TrendifyyDatabaseHelper(requireContext())

        binding.tabLayoutAuth.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    binding.layoutSignin.visibility = View.VISIBLE
                    binding.layoutSignup.visibility = View.GONE
                } else {
                    binding.layoutSignin.visibility = View.GONE
                    binding.layoutSignup.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.btnSigninSubmit.setOnClickListener {
            val email = binding.etSigninEmail.text.toString().trim()
            val pass = binding.etSigninPassword.text.toString().trim()
            if (email.isEmpty() || pass.isEmpty()) {
                Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (dbHelper.authenticateUser(email, pass)) {
                Snackbar.make(binding.root, "Signed in successfully!", Snackbar.LENGTH_SHORT).show()
                onAuthSuccess(email)
                dismiss()
            } else {
                Snackbar.make(binding.root, "Invalid email or password", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnSignupSubmit.setOnClickListener {
            val name = binding.etSignupName.text.toString().trim()
            val email = binding.etSignupEmail.text.toString().trim()
            val phone = binding.etSignupPhone.text.toString().trim()
            val pass = binding.etSignupPassword.text.toString().trim()

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
                Snackbar.make(binding.root, "Account created successfully!", Snackbar.LENGTH_SHORT).show()
                onAuthSuccess(email)
                dismiss()
            } else {
                Snackbar.make(binding.root, "Registration failed or email already exists", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}