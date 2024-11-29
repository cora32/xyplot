package io.iskopasi.xyplot.models

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.iskopasi.xyplot.api.Repository
import io.iskopasi.xyplot.bg
import io.iskopasi.xyplot.getScreenShot
import io.iskopasi.xyplot.pojo.MessageObject
import io.iskopasi.xyplot.pojo.Status
import io.iskopasi.xyplot.pojo.XyPlotMessageType
import io.iskopasi.xyplot.ui
import io.iskopasi.xyplot.views.XyPlotValue
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class ResultModel @Inject constructor(
    context: Application,
    private val repository: Repository
) : AndroidViewModel(context) {
    val messageFlow = MutableStateFlow<MessageObject?>(null)
    val data: MutableLiveData<XyPlotValue> = MutableLiveData(XyPlotValue())

    init {
        // Fetch result from DB and update LiveData
        bg {
            val pointList = repository.getLatestData()
            val minMax = repository.getMinMax()

            ui {
                data.value = XyPlotValue(pointList, minMax)
            }
        }
    }

    fun saveScreenshot(view: View) {
        // View -> bitmap
        val bitmap = getScreenShot(view)

        bg {
            // Save screenshot
            val result = repository.saveScreenshot(bitmap)

            // Parse response
            when (result.status) {
                Status.OK -> {
                    messageFlow.emit(MessageObject(XyPlotMessageType.Info, "Saved"))
                }

                Status.Error -> {
                    messageFlow.emit(
                        MessageObject(
                            XyPlotMessageType.Info,
                            "Error -> ${result.error}"
                        )
                    )
                }

                Status.Unknown -> {}
            }
        }
    }
}