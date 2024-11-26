package io.iskopasi.xyplot.pojo

enum class Status() {
    OK,
    Error,
    Unknown,
}

data class XyPlotResult<T>(
    val data: T? = null,
    val status: Status = Status.Unknown,
    val error: String = ""
) {
    companion object {
        fun <T> error(error: String): XyPlotResult<T> = XyPlotResult<T>(
            status = Status.Error,
            error = error
        )
    }
}