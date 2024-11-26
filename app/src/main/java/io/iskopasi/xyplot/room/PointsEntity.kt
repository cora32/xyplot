package io.iskopasi.xyplot.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PointsEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(index = true, name = "x") val x: Float = 0f,
    @ColumnInfo(index = true, name = "y") val y: Float = 0f,
)