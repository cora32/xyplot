package io.iskopasi.xyplot

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.core.view.drawToBitmap
import io.iskopasi.xyplot.pojo.XyPlotPoint
import io.iskopasi.xyplot.room.PointsEntity
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

val String.e: Unit
    get() {
        if (BuildConfig.DEBUG) Log.e("--> ERR:", this)
    }