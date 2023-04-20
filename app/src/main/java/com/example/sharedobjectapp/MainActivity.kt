package com.example.sharedobjectapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.sharedobjectapp.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var imageUri: Uri? = null
    private var textToShare = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageSharing.setOnClickListener {
            pickImage()

        }

        binding.shareText.setOnClickListener {
            textToShare = binding.textSharing.text.toString()
            sharedText()
//            val intent = Intent()
//            intent.action = Intent.ACTION_SEND
//            intent.type = "text/plain"
//            intent.putExtra(Intent.EXTRA_TEXT, textToShare)
//            intent.putExtra(Intent.EXTRA_SUBJECT, "subject here")
//            startActivity(Intent.createChooser(intent, "Share text via"))
        }

        binding.shareImg.setOnClickListener {
            // Получаем картинку как bitmap
            if (imageUri == null) {
                showToast("Pick Image")
            } else {
                sharedImage()
            }


//
//            val myDrawable = binding.imageSharing.drawable
//            val bitmap = (myDrawable as BitmapDrawable).bitmap
//
//            val file = File(externalCacheDir, "myImage.png")
//            val fOut = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fOut)
//            fOut.flush()
//            fOut.close()
//            file.setReadable(true, false)
//            val intent = Intent(Intent.ACTION_SEND)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
//            intent.type = "image/png"
//            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject here")
//            startActivity(Intent.createChooser(intent, "Share Image via"))
        }

        binding.sharedAll.setOnClickListener {
            textToShare = binding.textSharing.text.toString()
            if (imageUri == null) {
                showToast("Pick Image")
            } else {
                sharedAll()
            }

        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private var galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(), { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                imageUri = intent!!.data
                binding.imageSharing.setImageURI(imageUri)
            } else {
                showToast("Canceled")
            }
        }
    )

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun sharedText() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, textToShare)
        intent.putExtra(Intent.EXTRA_SUBJECT, "subject here")
        startActivity(Intent.createChooser(intent, "Share text via"))
    }

    private fun sharedImage() {
        val contentUri = getContentUri()
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, contentUri)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject here")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(Intent.createChooser(intent, "Share Image via"))

    }

    private fun sharedAll() {
        val contentUri = getContentUri()
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject here")
        intent.putExtra(Intent.EXTRA_TEXT, textToShare)
        intent.putExtra(Intent.EXTRA_STREAM, contentUri)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(Intent.createChooser(intent, "Share via"))

    }

    private fun getContentUri(): Uri {
        val bitmap: Bitmap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, imageUri!!)
            bitmap = ImageDecoder.decodeBitmap(source)
        } else {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        }

//        val bitmapDrawable = binding.imageSharing.drawable as BitmapDrawable
//        bitmap = bitmapDrawable.bitmap

        val imagesFolder = File(cacheDir, "images")
        var contentUri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "shared_image.png")
            val stream =  FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            contentUri= FileProvider.getUriForFile(this, "com.example.sharedobjectapp.fileprovaider", file)


        } catch (e: java.lang.Exception){
            showToast("${e.message}")
        }

        return contentUri!!


    }
}