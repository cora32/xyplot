package io.iskopasi.xyplot.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.iskopasi.xyplot.adapters.DotsAdapter
import io.iskopasi.xyplot.databinding.ActivityResultBinding
import io.iskopasi.xyplot.models.ResultModel
import kotlin.getValue

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {
    private val binding: ActivityResultBinding by lazy {
        ActivityResultBinding.inflate(layoutInflater)
    }
    private val model: ResultModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        // Setting up adapter
        binding.dotsRv.layoutManager = LinearLayoutManager(this.application)
        val adapter = DotsAdapter()
        binding.dotsRv.adapter = adapter

        // Waiting till data is fetched from DB and displaying that data
        model.data.observe(this) {
            adapter.data = it.data
            binding.xyPlot.data = it
        }
    }
}