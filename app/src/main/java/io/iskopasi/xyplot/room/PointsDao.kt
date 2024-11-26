package io.iskopasi.xyplot.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.iskopasi.xyplot.pojo.MinMaxYValue

@Dao
interface PointsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dotsData: List<PointsEntity>)

    @Query("SELECT * FROM pointsentity ORDER BY x ASC")
    fun getAll(): List<PointsEntity>

    @Query("SELECT min(y) as min, max(y) as max FROM pointsentity")
    fun getMinMax(): MinMaxYValue

    @Query("DELETE FROM pointsentity")
    fun clear()
}