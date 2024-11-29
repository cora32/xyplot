package io.iskopasi.xyplot.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.iskopasi.xyplot.IoDispatcher
import io.iskopasi.xyplot.MainDispatcher
import io.iskopasi.xyplot.api.Repository
import io.iskopasi.xyplot.pojo.MessageObject
import io.iskopasi.xyplot.pojo.XyPlotMessageType
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

enum class XyPlotEvent {
    IDLE,
    SHOW_RESULT
}

@HiltViewModel
class InputModel @Inject constructor(
    context: Application,
    private val repository: Repository,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    @MainDispatcher private val mainDispatcher: CoroutineContext
) : AndroidViewModel(context) {
    val messageFlow = MutableStateFlow<MessageObject?>(null)
    val loadingFlow = MutableStateFlow<Boolean>(false)
    val activityLaunchFlow = MutableStateFlow<XyPlotEvent>(XyPlotEvent.IDLE)
    val coroutineExceptionHandler = CoroutineExceptionHandler { context, exception ->
        viewModelScope.launch(mainDispatcher) {
            messageFlow.emit(
                MessageObject(
                    XyPlotMessageType.Error,
                    "Error -> $exception"
                )
            )
        }
    }

    fun requestsDots(dotAmount: Int) = viewModelScope.launch(coroutineExceptionHandler) {
        // Set loading animation
        loadingFlow.emit(true)

        // Request dots
        withContext(ioDispatcher) {
            val result = repository.requestsDots(dotAmount)

            // Save data to DB to avoid Intent payload limit
            repository.rewriteResult(result)

            // and send event that should launch new activity that will show the result
            activityLaunchFlow.emit(XyPlotEvent.SHOW_RESULT)
        }
    }.invokeOnCompletion {
        viewModelScope.launch {
            loadingFlow.emit(false)
        }
    }

    fun validate(dotAmount: Int?): Boolean = dotAmount in 1..1000
}