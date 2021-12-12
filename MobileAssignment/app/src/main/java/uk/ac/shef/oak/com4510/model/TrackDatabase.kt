package uk.ac.shef.oak.com4510.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Database(entities = [Image::class, Path::class], version = 1, exportSchema = false)
public abstract class TrackDatabase: RoomDatabase() {

    abstract fun imageDao(): ImageDao
    abstract fun pathDao(): PathDao

    companion object {
        private var INSTANCE: TrackDatabase? = null
        private val mutex = Mutex()

        fun getDatabase(context: Context): TrackDatabase? {
            if (INSTANCE == null) {
                runBlocking {
                    withContext(Dispatchers.Default) {
                        mutex.withLock(TrackDatabase::class) {
                            INSTANCE = databaseBuilder(
                                context.applicationContext,
                                TrackDatabase::class.java,
                                "track_database"
                            ).build()
                        }
                    }
                }
            }
            return INSTANCE
        }

        private val trackDatabaseCallback: RoomDatabase.Callback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Do any init operation here
            }
        }
    }
}