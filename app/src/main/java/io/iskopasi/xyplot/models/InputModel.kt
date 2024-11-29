package io.iskopasi.xyplot.models

import android.app.Application
import android.content.Intent
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.iskopasi.xyplot.IoDispatcher
import io.iskopasi.xyplot.MainDispatcher
import io.iskopasi.xyplot.activities.ResultActivity
import io.iskopasi.xyplot.api.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class InputModel @Inject constructor(
    context: Application,
    private val repository: Repository,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    @MainDispatcher private val mainDispatcher: CoroutineContext
) : BaseViewModel(context) {
    val loadingFlow = MutableStateFlow<Boolean>(false)

    fun requestsDots(dotAmount: Int) = viewModelScope.launch(coroutineExceptionHandler) {
        // Set loading animation
        loadingFlow.emit(true)

        // Request dots
        withContext(ioDispatcher) {
            val result = repository.requestsDots(dotAmount)

            // Save data to DB to avoid Intent payload limit
            repository.rewriteResult(result)

            // and send event that should launch new activity that will show the result
//            activityLaunchFlow.emit(XyPlotEvent.SHOW_RESULT)
            startResultActivity()
        }
    }.invokeOnCompletion {
        viewModelScope.launch {
            loadingFlow.emit(false)
        }
    }

    fun validate(dotAmount: Int?): Boolean = dotAmount in 1..1000

    private fun startResultActivity() = viewModelScope.launch {
        val context = getApplication<Application>()
        context.startActivity(Intent(context, ResultActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}