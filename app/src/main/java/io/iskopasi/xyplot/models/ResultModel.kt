package io.iskopasi.xyplot.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.iskopasi.xyplot.api.Repository
import io.iskopasi.xyplot.bg
import io.iskopasi.xyplot.ui
import io.iskopasi.xyplot.views.XyPlotValue
import javax.inject.Inject

@HiltViewModel
class ResultModel @Inject constructor(
    context: Application,
    private val repository: Repository
) : AndroidViewModel(context) {
    val data: MutableLiveData<XyPlotValue> = MutableLiveData(XyPlotValue())

    init {
        // Fetch result from DB and update LiveData
        bg {
            val pointList = repository.getLatestData()
            val minMax = repository.getMinMax()

            Log.e("-->", "-> ${pointList.size}, ${minMax.min}${minMax.max}")
            ui {
                data.value = XyPlotValue(pointList, minMax)
            }
        }
    }
}