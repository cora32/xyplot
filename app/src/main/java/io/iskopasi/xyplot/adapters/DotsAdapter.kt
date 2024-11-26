package io.iskopasi.xyplot.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.iskopasi.xyplot.databinding.DotItemBinding
import io.iskopasi.xyplot.room.PointsEntity

class DotsAdapter() : RecyclerView.Adapter<DotsAdapter.ViewHolder>() {
    var data: List<PointsEntity> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    class ViewHolder(val binding: DotItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DotItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.binding.itemTv.text = "x: ${item.x} y: ${item.y}"
    }
}