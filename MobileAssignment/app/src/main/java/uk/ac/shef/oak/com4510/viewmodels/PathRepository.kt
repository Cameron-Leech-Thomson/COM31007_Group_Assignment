package uk.ac.shef.oak.com4510.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.shef.oak.com4510.model.Path
import uk.ac.shef.oak.com4510.model.PathDao
import uk.ac.shef.oak.com4510.model.TrackDatabase

/**
 * The path repository class.
 */

class PathRepository(application: Application) {
    private var pathDao: PathDao? = null

    init {
        val db: TrackDatabase? = TrackDatabase.getDatabase(application)
        if (db != null) {
            pathDao = db.pathDao()
        }
    }

    companion object {
        private val scope = CoroutineScope(Dispatchers.IO)

        private class InsertAsyncTask(private val dao: PathDao?) : ViewModel() {
            suspend fun insertInBackground(path: Path) {
                scope.launch {
                    this@InsertAsyncTask.dao?.insertPath(path)?.toString()
                }
            }
        }
    }

    /**
     * Insert one path in the database
     * @param path the path to be inserted
     */
    suspend fun insertPath(path: Path) {
        InsertAsyncTask(pathDao).insertInBackground(path)
    }

    /**
     * Get all the paths created in the app
     * @return list of all paths
     */
    fun getAllPaths(): LiveData<MutableList<Path>>? {
        return pathDao?.getAllPaths()
    }
}