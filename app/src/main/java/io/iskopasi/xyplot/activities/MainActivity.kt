package io.iskopasi.xyplot.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.iskopasi.xyplot.R
import io.iskopasi.xyplot.databinding.ActivityMainBinding
import io.iskopasi.xyplot.models.InputModel
import io.iskopasi.xyplot.models.XyPlotEvent
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val model: InputModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        lifecycleScope.launch {
            // Show toast with error if any error occurred
            model.messageFlow.collect { msg ->
                if (msg != null) showError(msg.data)
            }
        }

        lifecycleScope.launch {
            // Launch ResultActivity if data fetched successfully
            model.activityLaunchFlow.collect { event ->
                if (event == XyPlotEvent.SHOW_RESULT) {
                    startActivity(Intent(this@MainActivity, ResultActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                }
            }
        }

        lifecycleScope.launch {
            // Reacts to data fetch start
            model.loadingFlow.collect { isLoading ->
                binding.inputBtn.isEnabled = isLoading == false
            }
        }

        binding.inputBtn.setOnClickListener {
            val dotAmount = binding.inputEt.text.toString().toIntOrNull()

            if (model.validate(dotAmount)) {
                model.requestsDots(dotAmount!!)
            } else {
                showError(getString(R.string.some_error))
            }
        }
    }

    private fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }
}