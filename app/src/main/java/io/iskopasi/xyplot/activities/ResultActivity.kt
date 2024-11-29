package io.iskopasi.xyplot.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.iskopasi.xyplot.R
import io.iskopasi.xyplot.adapters.DotsAdapter
import io.iskopasi.xyplot.databinding.ActivityResultBinding
import io.iskopasi.xyplot.models.ResultModel
import io.iskopasi.xyplot.pojo.XyPlotMessageType
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {
    private val binding: ActivityResultBinding by lazy {
        ActivityResultBinding.inflate(layoutInflater)
    }
    private val model: ResultModel by viewModels()
    private val launcher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultMap ->
        if (resultMap.values.all { it }) {
            model.saveScreenshot(binding.xyPlot)
        } else {
            showError(getString(R.string.permission_denied))
        }
    }

    private fun requestPermissionsL33() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    // Checks permission before saving screenshot
    private fun checkPermissionAndSaveScreenshot(view: View) {
        when (requestPermissionsL33()) {
            true -> model.saveScreenshot(view)
            else -> launcher.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        // Setting up adapter
        binding.dotsRv.layoutManager = LinearLayoutManager(this.application)
        val adapter = DotsAdapter()
        binding.dotsRv.adapter = adapter

        lifecycleScope.launch {
            // Re-collect flow on STARTED state after rotations
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Show toast with error if any error occurred
                model.messageFlow.collect { msg ->
                    if (msg != null) {
                        if (msg.type == XyPlotMessageType.Info) {
                            showInfo(msg.data)
                        } else {
                            showError(msg.data)
                        }
                    }
                }
            }
        }

        binding.fab.setOnClickListener { view ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                checkPermissionAndSaveScreenshot(binding.xyPlot)
            } else {
                model.saveScreenshot(binding.xyPlot)
            }
        }

        // Waiting till data is fetched from DB and displaying that data
        model.dataState.observe(this) {
            adapter.data = it.data
            binding.xyPlot.data = it
        }
    }

    private fun showInfo(str: String) {
        Toast.makeText(this, getString(R.string.info, str), Toast.LENGTH_SHORT).show()
    }

    private fun showError(str: String) {
        Toast.makeText(this, getString(R.string.error, str), Toast.LENGTH_SHORT).show()
    }
}