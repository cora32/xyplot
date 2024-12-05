package io.iskopasi.xyplot.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

enum class XyPlotMessageType() {
    Info,
    Error
}

data class MessageObject(
    val type: XyPlotMessageType,
    val data: String
)

open class BaseViewModel(
    context: Application,
    private val ioDispatcher: CoroutineContext,
    private val mainDispatcher: CoroutineContext
) : AndroidViewModel(context) {
    private val _messageFlow = MutableSharedFlow<MessageObject?>()
    val messageFlow: SharedFlow<MessageObject?> = _messageFlow

    private fun emitMessage(message: MessageObject) = viewModelScope.launch(mainDispatcher) {
        _messageFlow.emit(message)
    }

    protected fun info(message: String) = viewModelScope.launch(mainDispatcher) {
        emitMessage(MessageObject(XyPlotMessageType.Info, message))
    }

    protected fun error(message: String) = viewModelScope.launch(mainDispatcher) {
        emitMessage(MessageObject(XyPlotMessageType.Error, message))
    }

    // Runs block in a default coroutine and handle all uncaught exceptions
    protected fun bg(block: suspend (CoroutineScope) -> Unit): Job =
        viewModelScope.launch(ioDispatcher) {
            block(this)
        }
}