package uk.ac.shef.oak.com4510.model


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverters

@Dao
@TypeConverters(Converters::class)
interface ImageDao {

    @Query("SELECT * FROM Image WHERE path_id = :path_id")
    fun findImagesByPathId(path_id: Int): LiveData<MutableList<Image>>

    @Query("SELECT * FROM Image WHERE image_id = :image_id")
    fun findImageByImageId(image_id: Int): LiveData<Image>

    @Query("SELECT * FROM Image")
    fun findAllImages(): List<Image>

    @Insert
    fun insertImage(image: Image)
}