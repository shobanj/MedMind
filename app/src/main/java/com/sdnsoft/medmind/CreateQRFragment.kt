package com.sdnsoft.medmind

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateQRFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateQRFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var editText : EditText
    private lateinit var imageView : ImageView
    private lateinit var imageButton : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_qr, container, false)

        editText = view.findViewById<EditText>(R.id.editTextQRName)
        imageView = view.findViewById<ImageView>(R.id.imageViewQR)
        imageButton = view.findViewById<ImageButton>(R.id.imageButtonGenerateQR)

        editText.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    imageButton.performClick()
                    true
                }
                else -> false
            }
        }

        imageButton.setOnClickListener(View.OnClickListener {
            var text = editText.text.toString()
            if(text.isEmpty())
                true

            Log.d("Test", "Text is: " + text)
            var bitmap = generateBitmap(getString(R.string.intent_base_path) + editText.text.toString())
            imageView.setImageBitmap(bitmap)

            // Save the bitmap someplace and share the Uri to activity
            var uri = saveImage(bitmap)
            (requireActivity() as MainActivity).qrUri = uri

            true
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView.setImageBitmap(generateBitmap(getString(R.string.intent_base_path) + editText.text.toString()))
    }

    override fun onStart() {
        super.onStart()

        val mainActivity = requireActivity() as MainActivity
        mainActivity.enableDisableFabButtons("fragment_create_qr")
    }

    private fun generateBitmap(qr: String) : Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(qr, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    private fun saveImage(image: Bitmap) : Uri {
        var imagesFolder = File(requireActivity().cacheDir, "images")
        var uri : Uri? = null

        imagesFolder.mkdirs()
        var file = File(imagesFolder, "shared_image.png")
        var stream = FileOutputStream(file)

        image.compress(Bitmap.CompressFormat.PNG, 90, stream)
        stream.flush()
        stream.close()

        uri = FileProvider.getUriForFile(requireContext(), "com.sdnsoft.medmind", file)
        return uri
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateQRFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateQRFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

