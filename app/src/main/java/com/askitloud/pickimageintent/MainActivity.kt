package com.askitloud.pickimageintent

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {
    val GALLERY_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!checkPermissionForReadExtertalStorage()) {
            requestPermissionForReadExtertalStorage()
        }
    }

    private fun pickFromGallery() { //Create an Intent with action as ACTION_PICK
        val intent = Intent(Intent.ACTION_PICK)
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.type = "image/*"
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                //data.getData returns the content URI for the selected Image
                /*      val selectedImage: Uri? = data?.data
                      iv_imageView.setImageURI(selectedImage)*/


                val selectedImage = data!!.data
                val filePathColumn =
                    arrayOf(MediaStore.Images.Media.DATA)
                // Get the cursor
                val cursor: Cursor? =
                    contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
                // Move to first row
                cursor?.moveToFirst()
                //Get the column index of MediaStore.Images.Media.DATA
                val columnIndex: Int = cursor?.getColumnIndex(filePathColumn[0])!!
                //Gets the String value in the column
                val filePath: String = cursor?.getString(columnIndex)
                cursor?.close()
                // Set the Image in ImageView after decoding the String
                var file = File(filePath)
                if (file.exists()) {
                    iv_imageView.setImageBitmap(BitmapFactory.decodeFile(filePath))

                }

            }

        }
    }

    fun checkPermissionForReadExtertalStorage(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result: Int = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            return result == PackageManager.PERMISSION_GRANTED
        }
        return false
    }

    fun pickImageIntent(view: View) {
        pickFromGallery()
    }

    @Throws(Exception::class)
    fun requestPermissionForReadExtertalStorage() {
        try {
            val READ_STORAGE_PERMISSION_REQUEST_CODE = 100
            ActivityCompat.requestPermissions(
                (this as Activity?)!!,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_REQUEST_CODE
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
