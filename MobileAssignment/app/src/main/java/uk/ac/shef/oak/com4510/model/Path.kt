package uk.ac.shef.oak.com4510.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Path(
    @PrimaryKey(autoGenerate = true)
    var path_id: Int,

    @ColumnInfo(name = "path_title")
    var title: String,

    @ColumnInfo(name = "path_date")
    var time: Date,

    @ColumnInfo(name = "pressure")
    var pressure: String,

    @ColumnInfo(name = "temperature")
    var temperature: String,


){

}