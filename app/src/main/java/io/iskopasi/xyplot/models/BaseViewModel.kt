package io.iskopasi.xyplot.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.iskopasi.xyplot.pojo.MessageObject
import io.iskopasi.xyplot.pojo.XyPlotMessageType
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

open class BaseViewModel(context: Application) : AndroidViewModel(context) {
    private val _messageFlow = MutableSharedFlow<MessageObject?>()
    val messageFlow: SharedFlow<MessageObject?> = _messageFlow

    private val coroutineExceptionHandler = CoroutineExceptionHandler { context, exception ->
        viewModelScope.launch {
            emitMessage(
                MessageObject(
                    XyPlotMessageType.Error,
                    "Error -> $exception"
                )
            )
        }
    }

    private fun emitMessage(message: MessageObject) = viewModelScope.launch {
        _messageFlow.emit(message)
    }

    protected fun info(message: String) = viewModelScope.launch {
        emitMessage(MessageObject(XyPlotMessageType.Info, message))
    }

    protected fun error(message: String) = viewModelScope.launch {
        emitMessage(MessageObject(XyPlotMessageType.Error, message))
    }

    // Runs block in a default coroutine and handle all uncaught exceptions
    protected fun bg(block: suspend (CoroutineScope) -> Unit): Job =
        viewModelScope.launch(coroutineExceptionHandler) {
            block(this)
        }
}