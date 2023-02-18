package com.sdnsoft.medmind

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.sdnsoft.medmind.databinding.FragmentSecondBinding
import com.budiyev.android.codescanner.*
import com.google.zxing.BarcodeFormat
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private var scannerView : CodeScannerView? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                isGranted: Boolean ->
            if(isGranted) {
                Log.i("Permission: ", "Granted")
            } else {
                Log.i("Permission: ", "Denied")
            }
        }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        scannerView = _binding!!.scannerView
        requestPermission()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext() /* Activity context */)
        val prefAutoFocus = sharedPreferences.getBoolean("auto_focus", true)

        val context = requireContext()
        codeScanner = CodeScanner(context, scannerView!!)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = listOf(BarcodeFormat.QR_CODE) // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = prefAutoFocus // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread() {
                Toast.makeText(context, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()

                var mainActivity = activity as MainActivity

                val uri = Uri.parse(it.text)
                val tag = uri.getQueryParameter("id")
                if (tag != null) {
                    Log.d("Test", tag)
                    mainActivity.model.add(ScanItem(tag, Date()))
                }

                findNavController().popBackStack()
                // findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            activity?.runOnUiThread() {
                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }

        scannerView?.setOnClickListener() {
            codeScanner.startPreview()
        }
    }

    override fun onStart() {
        super.onStart()

        val mainActivity = requireActivity() as MainActivity
        mainActivity.enableDisableFabButtons("none")
    }

    private fun requestPermission() {
        when {
            requireContext().checkSelfPermission(
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
            }

            requireActivity().shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA
            ) -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        codeScanner.releaseResources()
    }
}