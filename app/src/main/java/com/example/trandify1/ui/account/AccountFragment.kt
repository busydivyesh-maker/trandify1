package com.example.trandify1.ui.account

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.example.trandify1.R
import com.example.trandify1.data.model.UserProfile
import com.example.trandify1.databinding.FragmentAccountBinding
import com.example.trandify1.utils.ExifUtils
import com.example.trandify1.viewmodel.MainViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private var photoUri: Uri? = null
    private var currentPhotoPath: String? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && photoUri != null) {
            currentPhotoPath?.let { path ->
                val bitmap = android.graphics.BitmapFactory.decodeFile(path)
                val rotated = ExifUtils.rotateBitmapIfNeeded(bitmap, path)
                binding.ivProfilePicture.setImageBitmap(rotated)

                // Save profile with new photo path
                val current = viewModel.userProfile.value
                viewModel.saveUserProfile(current.copy(imageUriPath = path))
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // User checked "Don't ask again", guide to Settings
                showSettingsRationaleDialog()
            } else {
                Snackbar.make(binding.root, R.string.camera_permission_rationale, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProfileData()
        setupListeners()
        setupSettingsItems()
    }

    private fun setupProfileData() {
        val profile = viewModel.userProfile.value
        binding.etName.setText(profile.name)
        binding.etAddress.setText(profile.address)
        binding.etEmail.setText(profile.email)
        binding.etPhone.setText(profile.phone)
        binding.etDob.setText(profile.dob)

        if (!profile.imageUriPath.isNullOrEmpty()) {
            currentPhotoPath = profile.imageUriPath
            binding.ivProfilePicture.load(File(profile.imageUriPath)) {
                crossfade(true)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }
        }
    }

    private fun setupListeners() {
        binding.etDob.setOnClickListener {
            showDatePicker()
        }

        binding.btnUserPicture.setOnClickListener {
            showCameraOptionsBottomSheet()
        }

        binding.fabCameraSmall.setOnClickListener {
            showCameraOptionsBottomSheet()
        }

        binding.btnSaveProfile.setOnClickListener {
            validateAndSaveProfile()
        }
    }

    private fun showDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date of Birth")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateStr = sdf.format(Date(selection))
            binding.etDob.setText(dateStr)
        }
        datePicker.show(parentFragmentManager, "DatePicker")
    }

    private fun showCameraOptionsBottomSheet() {
        val bottomSheet = CameraOptionsBottomSheetFragment(
            onTakeClick = { checkCameraPermissionAndLaunch() },
            onRemoveClick = { removeProfilePhoto() }
        )
        bottomSheet.show(parentFragmentManager, "CameraOptionsBottomSheet")
    }

    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Camera Permission Required")
                    .setMessage(R.string.camera_permission_rationale)
                    .setPositiveButton("Grant") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        try {
            val photoFile = createImageFile()
            currentPhotoPath = photoFile.absolutePath
            photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            photoUri?.let { uri ->
                try {
                    takePictureLauncher.launch(uri)
                } catch (e: Exception) {
                    Snackbar.make(binding.root, R.string.camera_not_found, Snackbar.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Snackbar.make(binding.root, R.string.camera_not_found, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().cacheDir
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun removeProfilePhoto() {
        currentPhotoPath = null
        binding.ivProfilePicture.setImageResource(R.drawable.ic_user)
        val current = viewModel.userProfile.value
        viewModel.saveUserProfile(current.copy(imageUriPath = null))
        Snackbar.make(binding.root, R.string.photo_removed, Snackbar.LENGTH_SHORT).show()
    }

    private fun showSettingsRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Denied")
            .setMessage("Camera permission is permanently denied. Please enable it in app settings to take profile pictures.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun validateAndSaveProfile() {
        val name = binding.etName.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val dob = binding.etDob.text.toString().trim()

        var isValid = true

        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.err_empty_name)
            isValid = false
        } else {
            binding.tilName.error = null
        }

        if (address.isEmpty()) {
            binding.tilAddress.error = getString(R.string.err_empty_address)
            isValid = false
        } else {
            binding.tilAddress.error = null
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.err_invalid_email)
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (phone.length != 10 || !phone.all { it.isDigit() }) {
            binding.tilPhone.error = getString(R.string.err_invalid_phone)
            isValid = false
        } else {
            binding.tilPhone.error = null
        }

        if (isValid) {
            val profile = UserProfile(
                name = name,
                address = address,
                email = email,
                phone = phone,
                dob = dob,
                imageUriPath = currentPhotoPath
            )
            viewModel.saveUserProfile(profile)
            Snackbar.make(binding.root, R.string.profile_saved, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupSettingsItems() {
        with(binding) {
            settingOrders.tvSettingTitle.text = getString(R.string.my_orders)
            settingOrders.ivSettingIcon.setImageResource(R.drawable.ic_orders)
            settingOrders.root.setOnClickListener {
                startActivity(android.content.Intent(requireContext(), com.example.trandify1.ui.orders.OrdersActivity::class.java))
            }

            settingAddresses.tvSettingTitle.text = getString(R.string.saved_addresses)
            settingAddresses.ivSettingIcon.setImageResource(R.drawable.ic_location)

            settingPayments.tvSettingTitle.text = getString(R.string.payment_methods)
            settingPayments.ivSettingIcon.setImageResource(R.drawable.ic_payment)

            settingNotifications.tvSettingTitle.text = getString(R.string.notifications)
            settingNotifications.ivSettingIcon.setImageResource(R.drawable.ic_notification)

            settingHelp.tvSettingTitle.text = getString(R.string.help_center)
            settingHelp.ivSettingIcon.setImageResource(R.drawable.ic_help)

            settingLogout.tvSettingTitle.text = "Logout"
            settingLogout.ivSettingIcon.setImageResource(R.drawable.ic_logout)
            settingLogout.root.setOnClickListener {
                requireContext().getSharedPreferences("trendifyy_prefs", android.content.Context.MODE_PRIVATE)
                    .edit()
                    .remove("logged_in_email")
                    .apply()

                val intent = android.content.Intent(requireContext(), com.example.trandify1.ui.auth.AuthActivity::class.java).apply {
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}