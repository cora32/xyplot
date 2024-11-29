package io.iskopasi.xyplot.api

import android.graphics.Bitmap
import io.iskopasi.xyplot.pojo.MinMaxYValue
import io.iskopasi.xyplot.pojo.XyPlotPoint
import io.iskopasi.xyplot.room.PointsDao
import io.iskopasi.xyplot.room.PointsEntity
import io.iskopasi.xyplot.saveIntoDownloads
import io.iskopasi.xyplot.toPointEntities
import java.io.File
import javax.inject.Inject

class Repository @Inject constructor(
    private val restApi: RestApi,
    private val dao: PointsDao
) {
    // Returns XyPlotResult with either array of dots or error string
    suspend fun requestsDots(dotsCount: Int): List<XyPlotPoint> =
        restApi.requestsDots(dotsCount).body()!!.points

    // Clears old data and saves the new
    suspend fun rewriteResult(listOfPoints: List<XyPlotPoint>) {
        dao.clear()
        dao.insert(listOfPoints.toPointEntities())
    }

    // Fetch data from DB. The data is sorted by x ASC.
    suspend fun getLatestData(): List<PointsEntity> =
        dao.getAll()

    // Get min and max values.
    suspend fun getMinMax(): MinMaxYValue = dao.getMinMax()

    // Creates file and saves bitmap into the file
    fun saveScreenshot(bitmap: Bitmap): File = saveIntoDownloads(bitmap)
}

