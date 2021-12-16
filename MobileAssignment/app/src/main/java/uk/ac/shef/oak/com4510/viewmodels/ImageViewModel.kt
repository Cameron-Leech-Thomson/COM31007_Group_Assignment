package uk.ac.shef.oak.com4510.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import uk.ac.shef.oak.com4510.model.Image

class ImageViewModel(application: Application) : AndroidViewModel(application) {
    private val imageRepo: ImageRepository = ImageRepository(application)

    fun findAllImages(): List<Image>? {
        return imageRepo.findAllImages()
    }

    fun findImagesByPathId(path_id: Int): LiveData<MutableList<Image>>? {
        return imageRepo.findImagesByPathId(path_id)
    }

    suspend fun insertImage(image: Image) {
        imageRepo.insertImage(image)
    }
}