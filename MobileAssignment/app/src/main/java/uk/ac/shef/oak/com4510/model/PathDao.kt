package uk.ac.shef.oak.com4510.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert

@Dao
public interface PathDao {

    @Query("SELECT * FROM Path")
    fun getAllPaths(): LiveData<MutableList<Path>>

    @Insert
    fun insertPath(path: Path)
}