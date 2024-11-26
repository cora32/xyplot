package io.iskopasi.xyplot.api

import io.iskopasi.xyplot.asError
import io.iskopasi.xyplot.asOk
import io.iskopasi.xyplot.pojo.MinMaxYValue
import io.iskopasi.xyplot.pojo.XyPlotPoint
import io.iskopasi.xyplot.pojo.XyPlotResult
import io.iskopasi.xyplot.room.PointsDao
import io.iskopasi.xyplot.room.PointsEntity
import io.iskopasi.xyplot.toPointEntities
import retrofit2.HttpException
import javax.inject.Inject

class Repository @Inject constructor(
    private val restApi: RestApi,
    private val dao: PointsDao,
) {
    // Returns XyPlotResult with either array of dots or error string
    suspend fun requestsDots(dotsCount: Int): XyPlotResult<List<XyPlotPoint>> {
        return try {
            restApi.requestsDots(dotsCount).let {
                if (it.isSuccessful) {
                    it.body()!!.points.asOk()
                } else {
                    "Code: ${it.code()}".asError()
                }
            }
        } catch (e: HttpException) {
            e.printStackTrace()
            "Network exception: ${e.message}".asError()
        } catch (e: Throwable) {
            e.printStackTrace()
            "General exception: ${e.message}".asError()
        }
    }

    // Clears old data and saves the new
    fun rewriteResult(listOfPoints: List<XyPlotPoint>) {
        dao.clear()
        dao.insert(listOfPoints.toPointEntities())
    }

    // Fetch data from DB. The data is sorted by x ASC.
    fun getLatestData(): List<PointsEntity> = dao.getAll()

    // Get min and max values.
    fun getMinMax(): MinMaxYValue = dao.getMinMax()
}

