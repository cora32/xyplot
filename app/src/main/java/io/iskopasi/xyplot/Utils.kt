package io.iskopasi.xyplot

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Environment
import android.util.TypedValue
import android.view.View
import androidx.core.view.drawToBitmap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import io.iskopasi.xyplot.pojo.Status
import io.iskopasi.xyplot.pojo.XyPlotPoint
import io.iskopasi.xyplot.pojo.XyPlotResult
import io.iskopasi.xyplot.room.PointsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

// Int <-> Dp,Sp converters
val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Float.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Context.spToPx(dp: Int): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    resources.displayMetrics
)

// Coroutine launchers
fun ViewModel.ui(block: suspend (CoroutineScope) -> Unit): Job = viewModelScope.launch(
    Dispatchers.Main
) {
    block(this)
}

fun ViewModel.bg(block: suspend (CoroutineScope) -> Unit): Job = viewModelScope.launch(
    Dispatchers.IO
) {
    block(this)
}

fun LifecycleOwner.ui(block: suspend (CoroutineScope) -> Unit): Job = lifecycleScope.launch(
    Dispatchers.Main
) {
    block(this)
}

fun LifecycleOwner.bg(block: suspend (CoroutineScope) -> Unit): Job = lifecycleScope.launch(
    Dispatchers.IO
) {
    block(this)
}

// Response wrappers
fun <T> T.asOk(): XyPlotResult<T> = XyPlotResult(this, Status.OK)

fun <T> String.asError() = XyPlotResult.error<T>(error = this)

// Network -> Room Entity convertor
fun List<XyPlotPoint>.toPointEntities(): List<PointsEntity> = map {
    PointsEntity(
        x = it.x?.toFloat() ?: 0f,
        y = it.y?.toFloat() ?: 0f,
    )
}

// File and bitmap utils
fun getScreenShot(view: View) = view.drawToBitmap(Bitmap.Config.ARGB_8888)

fun getNewFileInDownloads(ext: String): File {
    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    return File(path, "xyplot_${System.currentTimeMillis()}$ext")
}

fun saveIntoDownloads(bitmap: Bitmap): File {
    val file = getNewFileInDownloads(".jpg")

    FileOutputStream(file).use { targetOutputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, targetOutputStream)
    }

    return file
}