package com.duyvv.camera.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.duyvv.camera.databinding.FragmentScanQrBinding
import com.google.common.util.concurrent.ListenableFuture

class ScanQRFragment : Fragment() {

    private var _binding: FragmentScanQrBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val hasCameraPermission
        get() = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private val permissionsResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            setupCamera()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermission()

    }

    private fun setup() {

    }

    private fun requestPermission() {
        if (!hasCameraPermission) {
            permissionsResultLauncher.launch(Manifest.permission.CAMERA)
        } else {
            setupCamera()
        }
    }

    private fun setupCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext()).apply {
            addListener(
                {
                    val cameraProvider = get()
                    bindPreview(cameraProvider)
                },
                ContextCompat.getMainExecutor(requireContext())
            )
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.surfaceProvider = binding.previewView.surfaceProvider
        var camera = cameraProvider?.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview)
    }
}