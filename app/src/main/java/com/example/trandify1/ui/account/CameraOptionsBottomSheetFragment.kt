package com.example.trandify1.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trandify1.databinding.DialogCameraOptionsBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CameraOptionsBottomSheetFragment(
    private val onTakeClick: () -> Unit,
    private val onRemoveClick: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogCameraOptionsBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogCameraOptionsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOptionTakePhoto.setOnClickListener {
            dismiss()
            onTakeClick()
        }

        binding.btnOptionRemovePhoto.setOnClickListener {
            dismiss()
            onRemoveClick()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}