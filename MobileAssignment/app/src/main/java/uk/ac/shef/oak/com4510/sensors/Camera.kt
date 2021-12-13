package com.example.lab2

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import pl.aprilapps.easyphotopicker.*
import uk.ac.shef.oak.com4510.*

@RequiresApi(Build.VERSION_CODES.N)
class CameraInteraction constructor(private val mainActivity: MapsActivity) : AppCompatActivity(){

    private var easyImage: EasyImage

    init {
        easyImage = EasyImage.Builder(mainActivity)
            .setChooserTitle("Pick media")
            .setFolderName("EasyImage sample")
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(false)
            .setCopyImagesToPublicGalleryFolder(true)
            .build()
    }

    fun openCamera(){
        easyImage.openChooser(mainActivity)
    }

    fun getImageFile(): MediaFile?{
        return if (::imageFile.isInitialized){
            imageFile
        }else{
            null
        }
    }

    private lateinit var imageFile: MediaFile

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(requestCode, resultCode,data,mainActivity,
            object: DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    // Only returns 1 image, so take the first from the array.
                    imageFile = imageFiles[0]
                }
                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    super.onImagePickerError(error, source)
                }
                override fun onCanceled(source: MediaSource) {
                    super.onCanceled(source)
                }
            })
    }

}