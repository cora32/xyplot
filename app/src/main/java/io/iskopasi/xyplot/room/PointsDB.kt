package io.iskopasi.xyplot.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PointsEntity::class],
    version = 1,
)
abstract class PointsDB : RoomDatabase() {
    abstract fun dao(): PointsDao
}

fun getDB(
    application: Context
): PointsDB = Room
    .databaseBuilder(application, PointsDB::class.java, "xyplot_db")
    .fallbackToDestructiveMigration()
    .build()
