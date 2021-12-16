package uk.ac.shef.oak.com4510.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.shef.oak.com4510.model.Image
import uk.ac.shef.oak.com4510.model.ImageDao
import uk.ac.shef.oak.com4510.model.TrackDatabase

/**
 * The image repository class
 */

class ImageRepository(application: Application) {
    private var imageDao: ImageDao? = null

    init {
        val db: TrackDatabase? = TrackDatabase.getDatabase(application)
        if (db != null) {
            imageDao = db.imageDao()
        }
    }

    companion object {
        private val scope = CoroutineScope(Dispatchers.IO)

        private class InsertAsyncTask(private val dao: ImageDao?) : ViewModel() {
            suspend fun insertInBackground(image: Image) {
                scope.launch {
                    this@InsertAsyncTask.dao?.insertImage(image)?.toString()
                }
            }
        }
    }

    suspend fun insertImage(image: Image) {
        InsertAsyncTask(imageDao).insertInBackground(image)
    }

    /**
     * Find all the images that have the same path
     * @return list containing all the path images
     */
    fun findImagesByPathId(path_id: Int): LiveData<MutableList<Image>>? {
        return imageDao?.findImagesByPathId(path_id)
    }

    /**
     * Find image by given id
     * @return image with the given id
     */
    fun findImageByImageId(image_id: Int): List<Image>? {
        return imageDao?.findImageByImageId(image_id)
    }

    /**
     * Find all images taken in the app
     * @return list of all images in the app
     */
    fun findAllImages(): List<Image>? {
        return imageDao?.findAllImages()
    }
}