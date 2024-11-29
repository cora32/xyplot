package io.iskopasi.xyplot.models

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.iskopasi.xyplot.IoDispatcher
import io.iskopasi.xyplot.MainDispatcher
import io.iskopasi.xyplot.api.Repository
import io.iskopasi.xyplot.getScreenShot
import io.iskopasi.xyplot.views.XyPlotValue
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@HiltViewModel
class ResultModel @Inject constructor(
    context: Application,
    private val repository: Repository,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    @MainDispatcher private val mainDispatcher: CoroutineContext
) : BaseViewModel(
    context,
    ioDispatcher = ioDispatcher,
    mainDispatcher = mainDispatcher
) {
    private val _dataState: MutableLiveData<XyPlotValue> = MutableLiveData(XyPlotValue())
    val dataState: LiveData<XyPlotValue> = _dataState

    init {
        // Fetch result from DB and update LiveData
        bg {
            withContext(ioDispatcher) {
                val pointList = repository.getLatestData()
                val minMax = repository.getMinMax()

                withContext(mainDispatcher) {
                    _dataState.value = XyPlotValue(pointList, minMax)
                }
            }
        }
    }

    fun saveScreenshot(view: View) = bg {
        // View -> bitmap
        val bitmap = getScreenShot(view)

        withContext(ioDispatcher) {
            // Save screenshot
            repository.saveScreenshot(bitmap)

            info("Saved")
        }
    }
}