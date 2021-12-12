package uk.ac.shef.oak.com4510.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Image::class, Path::class], version = 1, exportSchema = false)
public abstract class TrackDatabase: RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: TrackDatabase? = null
        fun getDatabase(context: Context): TrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackDatabase::class.java,
                    "track_database",
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}