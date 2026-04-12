package com.melancholicbastard.handyasr.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.melancholicbastard.handyasr.data.db.node.NodeDao
import com.melancholicbastard.handyasr.data.db.node.NodeEntity

@Database(
    entities = [NodeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun nodeDao(): NodeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "handy_asr.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

