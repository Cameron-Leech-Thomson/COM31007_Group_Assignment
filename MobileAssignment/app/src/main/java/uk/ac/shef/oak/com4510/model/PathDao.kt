package uk.ac.shef.oak.com4510.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert

@Dao
public interface PathDao {

    @Query("SELECT * FROM Path")
    fun getAllPaths(): List<Path>

    @Query("SELECT path_id FROM Path ORDER BY path_id DESC LIMIT 1")
    fun getLastPath(): Int

    @Query("SELECT * FROM Path WHERE path_id = :path_id")
    fun getPathByID(path_id: Int): List<Path>

    @Insert
    fun insertPath(path: Path)
}