package io.iskopasi.xyplot.models

import android.app.Application
import android.content.Intent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.iskopasi.xyplot.IoDispatcher
import io.iskopasi.xyplot.MainDispatcher
import io.iskopasi.xyplot.activities.ResultActivity
import io.iskopasi.xyplot.api.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class InputModel @Inject constructor(
    context: Application,
    private val repository: Repository,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    @MainDispatcher private val mainDispatcher: CoroutineContext
) : BaseViewModel(
    context,
    ioDispatcher = ioDispatcher,
    mainDispatcher = mainDispatcher
) {
    private val _loadingFlow = MutableStateFlow<Boolean>(false)
    val loadingFlow: StateFlow<Boolean> = _loadingFlow

    fun requestsDots(dotAmount: Int) = bg {
        // Set loading animation
        _loadingFlow.emit(true)

        // Request dots
        try {
            val result = repository.requestsDots(dotAmount)

            // Save data to DB to avoid Intent payload limit
            repository.rewriteResult(result)

            // and send event that should launch new activity that will show the result
            startResultActivity()
        } catch (ex: Exception) {
            ex.printStackTrace()
            error("Network error: $ex")
        }

        _loadingFlow.emit(false)
    }

    fun validate(dotAmount: Int?): Boolean = dotAmount in 1..1000

    private fun startResultActivity() = bg {
        val context = getApplication<Application>()
        context.startActivity(Intent(context, ResultActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}