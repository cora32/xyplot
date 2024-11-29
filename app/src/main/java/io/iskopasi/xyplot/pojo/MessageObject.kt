package io.iskopasi.xyplot.pojo

enum class XyPlotMessageType() {
    Info,
    Error
}

data class MessageObject(
    val type: XyPlotMessageType,
    val data: String
)