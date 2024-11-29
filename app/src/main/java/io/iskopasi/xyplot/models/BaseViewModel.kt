package io.iskopasi.xyplot.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.iskopasi.xyplot.pojo.MessageObject
import io.iskopasi.xyplot.pojo.XyPlotMessageType
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

open class BaseViewModel(context: Application) : AndroidViewModel(context) {
    val messageFlow = MutableStateFlow<MessageObject?>(null)
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

    protected fun emitMessage(message: MessageObject) = viewModelScope.launch {
        messageFlow.emit(message)
    }
}