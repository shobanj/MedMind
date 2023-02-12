package com.sdnsoft.medmind

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ScanAdapter(model : ScanModel) : RecyclerView.Adapter<ScanAdapter.ScanViewHolder>() {
    private lateinit var model : ScanModel

    init {
        this.model = model
    }

    class ScanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tagTextView : TextView = itemView.findViewById<TextView>(R.id.textViewTag)
        private val dateTextView : TextView = itemView.findViewById<TextView>(R.id.textViewDate)

        fun bind(tag: String, dt: String) {
            tagTextView.text = tag
            dateTextView.text = dt
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scan_item_layout, parent, false)

        val detailsView = view.findViewById<ImageButton>(R.id.imageButton)
        detailsView.setOnClickListener {
            Toast.makeText(it.context, "Details clicked...", Toast.LENGTH_LONG).show()
        }

        return ScanViewHolder(view)
    }

    override fun getItemCount(): Int {
        return model.getCount()
    }

    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        holder.bind(model.getTag(position), model.getDate(position))
    }
}