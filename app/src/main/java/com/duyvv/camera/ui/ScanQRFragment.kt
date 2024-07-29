package com.duyvv.camera.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_ORIGINAL
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.duyvv.camera.databinding.FragmentScanQrBinding
import com.duyvv.camera.utils.isValidUrl
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

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

    private fun requestPermission() {
        if (!hasCameraPermission) {
            permissionsResultLauncher.launch(Manifest.permission.CAMERA)
        } else {
            setupCamera()
        }
    }

    private fun setupCamera() {
        cameraProviderFuture = ProcessCameraProvider
            .getInstance(requireContext()).apply {
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

        // setup qr code scanner
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        val barcodeScanner = BarcodeScanning.getClient(options)

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
            .build()

        val mlKitAnalyzer = MlKitAnalyzer(
            listOf(barcodeScanner),
            COORDINATE_SYSTEM_ORIGINAL,
            ContextCompat.getMainExecutor(requireContext())
        ) { result: MlKitAnalyzer.Result? ->
            val data = result?.getValue(barcodeScanner)
            if (!data.isNullOrEmpty()) {
                data[0].rawValue?.let {
                    Log.d("TAG", it)
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    if (isValidUrl(it)) {
                        imageAnalysis.clearAnalyzer()
                        startActivity(it)
                    }
                }
            }
        }

        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(requireContext()),
            mlKitAnalyzer
        )

        // set up camera
        cameraProvider?.unbindAll()
        cameraProvider?.bindToLifecycle(
            viewLifecycleOwner,
            cameraSelector,
            imageAnalysis,
            preview
        )
    }

    private fun startActivity(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
        activity?.finish()
    }
}