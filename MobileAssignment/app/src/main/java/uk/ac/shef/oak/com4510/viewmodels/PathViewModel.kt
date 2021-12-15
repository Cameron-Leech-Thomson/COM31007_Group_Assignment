package uk.ac.shef.oak.com4510.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import uk.ac.shef.oak.com4510.model.Path

class PathViewModel(application: Application) : AndroidViewModel(application) {

    private val pathRepo: PathRepository = PathRepository(application)

    suspend fun insertPath(path: Path) {
        pathRepo.insertPath(path)
    }

    fun getAllPaths(): LiveData<MutableList<Path>>? {
        return pathRepo.getAllPaths()
    }
}