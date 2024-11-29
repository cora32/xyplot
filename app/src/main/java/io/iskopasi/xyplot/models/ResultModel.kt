package io.iskopasi.xyplot.models

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.iskopasi.xyplot.IoDispatcher
import io.iskopasi.xyplot.MainDispatcher
import io.iskopasi.xyplot.api.Repository
import io.iskopasi.xyplot.getScreenShot
import io.iskopasi.xyplot.pojo.MessageObject
import io.iskopasi.xyplot.pojo.XyPlotMessageType
import io.iskopasi.xyplot.views.XyPlotValue
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@HiltViewModel
class ResultModel @Inject constructor(
    context: Application,
    private val repository: Repository,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    @MainDispatcher private val mainDispatcher: CoroutineContext
) : AndroidViewModel(context) {
    val messageFlow = MutableStateFlow<MessageObject?>(null)
    val data: MutableLiveData<XyPlotValue> = MutableLiveData(XyPlotValue())
    val coroutineExceptionHandler = CoroutineExceptionHandler { context, exception ->
        viewModelScope.launch {
            messageFlow.emit(
                MessageObject(
                    XyPlotMessageType.Error,
                    "Error -> $exception"
                )
            )
        }
    }

    init {
        // Fetch result from DB and update LiveData
        viewModelScope.launch(coroutineExceptionHandler) {
            withContext(ioDispatcher) {
                val pointList = repository.getLatestData()
                val minMax = repository.getMinMax()

                withContext(mainDispatcher) {
                    data.value = XyPlotValue(pointList, minMax)
                }
            }
        }
    }

    fun saveScreenshot(view: View) = viewModelScope.launch(coroutineExceptionHandler) {
        // View -> bitmap
        val bitmap = getScreenShot(view)

        withContext(ioDispatcher) {
            // Save screenshot
            repository.saveScreenshot(bitmap)

            messageFlow.emit(MessageObject(XyPlotMessageType.Info, "Saved"))
        }
    }
}