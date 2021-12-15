package uk.ac.shef.oak.com4510.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.util.Date

@Entity(
    foreignKeys = [ForeignKey(
        entity = Path::class,
        parentColumns = arrayOf("path_id"),
        childColumns = arrayOf("path_id"),
        onDelete = CASCADE
    )]
)
@TypeConverters(Converters::class)
data class Image(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "image_id")
    var image_id: Int,

    @ColumnInfo(name = "uri")
    var imageUri: String,

    @ColumnInfo(name = "image_title")
    var title: String,

    @ColumnInfo(name = "longitude")
    var longitude: Double,

    @ColumnInfo(name = "latitude")
    var latitude: Double,

    @ColumnInfo(name = "image_time")
    var time: Date,

    @ColumnInfo(name = "path_id")
    var path_id: Int,

    ) {

}