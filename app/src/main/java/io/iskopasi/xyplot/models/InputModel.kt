package io.iskopasi.xyplot.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.iskopasi.xyplot.api.Repository
import io.iskopasi.xyplot.bg
import io.iskopasi.xyplot.pojo.Status
import io.iskopasi.xyplot.ui
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

enum class XyPlotEvent {
    IDLE,
    SHOW_RESULT
}

@HiltViewModel
class InputModel @Inject constructor(
    context: Application,
    private val repository: Repository,
) : AndroidViewModel(context) {
    val errorFlow = MutableStateFlow<String?>(null)
    val activityLaunchFlow = MutableStateFlow<XyPlotEvent>(XyPlotEvent.IDLE)
    val isLoadingValue: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)

    fun requestsDots(dotAmount: Int) = bg {
        // Set loading animation
        ui {
            isLoadingValue.value = true
        }

        // Request dots
        val result = repository.requestsDots(dotAmount)

        // Parse response
        when (result.status) {
            Status.OK -> {
                Log.e("->>", "status: ${result.status}")
                // Save data to DB to avoid Intent payload limit
                repository.rewriteResult(result.data!!)

                // and send event that should launch new activity that will show the result
                activityLaunchFlow.emit(XyPlotEvent.SHOW_RESULT)
            }

            Status.Error -> {
                errorFlow.emit("Error -> ${result.error}")
            }

            Status.Unknown -> {}
        }

        ui {
            // Remove loading animation
            isLoadingValue.value = false
        }
    }

    fun validate(dotAmount: Int?): Boolean = dotAmount in 0..1000
}