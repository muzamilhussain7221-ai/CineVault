package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        WatchlistEntity::class,
        ContinueWatchingEntity::class,
        DownloadEntity::class,
        CustomContentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CineVaultDatabase : RoomDatabase() {
    abstract fun dao(): CineVaultDao

    companion object {
        @Volatile
        private var INSTANCE: CineVaultDatabase? = null

        fun getInstance(context: Context): CineVaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CineVaultDatabase::class.java,
                    "cinevault_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
