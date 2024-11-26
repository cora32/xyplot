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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.iskopasi.xyplot.R
import io.iskopasi.xyplot.adapters.DotsAdapter
import io.iskopasi.xyplot.databinding.ActivityResultBinding
import io.iskopasi.xyplot.models.ResultModel
import io.iskopasi.xyplot.screenshotIntoDownloads
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
            saveScreenshot(binding.xyPlot)
        } else {
            onResult(getString(R.string.permission_denied))
        }
    }

    private fun requestPermissionsL33() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    // Checks permission before saving screenshot
    private fun checkPermissionAndSaveScreenshot(view: View) {
        when (requestPermissionsL33()) {
            true -> saveScreenshot(view)
            else -> launcher.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            )
        }
    }

    // Creates file and saves bitmap into the file
    private fun saveScreenshot(view: View) {
        // Saving screenshot into Downloads folder
        screenshotIntoDownloads(binding.xyPlot) { str ->
            // Displaying toast with result msg
            onResult(str)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        // Setting up adapter
        binding.dotsRv.layoutManager = LinearLayoutManager(this.application)
        val adapter = DotsAdapter()
        binding.dotsRv.adapter = adapter

        binding.fab.setOnClickListener { view ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                checkPermissionAndSaveScreenshot(view)
            } else {
                saveScreenshot(view)
            }
        }

        // Waiting till data is fetched from DB and displaying that data
        model.data.observe(this) {
            adapter.data = it.data
            binding.xyPlot.data = it
        }
    }

    private fun onResult(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }
}