package io.iskopasi.xyplot.pojo

import com.google.gson.annotations.SerializedName

data class PointsResponse(
    @SerializedName("points") val points: ArrayList<XyPlotPoint> = arrayListOf()
)

data class XyPlotPoint(
    @SerializedName("x") val x: Double? = null,
    @SerializedName("y") val y: Double? = null
)