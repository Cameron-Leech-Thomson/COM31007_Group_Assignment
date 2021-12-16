package uk.ac.shef.oak.com4510.model

import androidx.room.*
import java.util.*

@Entity
data class Path(
    @PrimaryKey(autoGenerate = true)
    var path_id: Int,

    @ColumnInfo(name = "path_title")
    var title: String,

    @ColumnInfo(name = "path_date")
    var time: Date,


    ){

}