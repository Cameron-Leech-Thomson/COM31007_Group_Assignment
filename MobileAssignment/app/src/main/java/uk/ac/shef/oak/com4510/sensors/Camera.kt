package com.example.lab2

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import uk.ac.shef.oak.com4510.*
import java.io.File
import java.io.IOException
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
class Camera constructor(private val mainActivity: MapsActivity){

    fun takePicture(){
        Log.e("Hi!", "takePicture()")
        // Open Camera:
        dispatchTakePictureIntent()
    }

    private val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(mainActivity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    Log.d("PhotoFile", "Trying CreateImageFile()")
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.d("Error!","CreateImageFile() Failed!!!!")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        mainActivity.applicationContext,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(mainActivity, takePictureIntent, REQUEST_IMAGE_CAPTURE, null)
                }
            }
        }
    }

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Get primary locale:
        val locale = mainActivity.resources.configuration.locales.get(0)
        Log.d("Locale", locale.toString())
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", locale).format(Date())
        Log.d("timeStamp", timeStamp)
        val storageDir: File? = mainActivity.applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.d("StorageDir", storageDir.toString())
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = this.absolutePath
        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            mainActivity.applicationContext.sendBroadcast(mediaScanIntent)
        }
    }

}