package com.opsc.opsc7312poe.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312poe.R

class AnalysisDateAdapter (
    private val onDateSelected: (String) -> Unit
) : RecyclerView.Adapter<AnalysisDateAdapter.ViewHolder>() {

    val dates = listOf("Daily", "Weekly", "Monthly", "Year")

    private var selectedPosition = -1 // Track the currently selected position

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateVal: TextView = itemView.findViewById(R.id.date_val)

        init {
            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition // Update selected position

                // Notify the adapter to refresh the views
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                // Trigger the callback with the selected category
                onDateSelected(dates[selectedPosition])
            }
        }

        fun bind(date: String) {
            dateVal.text = date

            // Change background color based on selection
            dateVal.setTextColor(
                if (adapterPosition == selectedPosition)
                    ContextCompat.getColor(itemView.context, R.color.white)
                else
                    ContextCompat.getColor(itemView.context, R.color.black) // Default background
            )

            itemView.setBackgroundResource(
                if (adapterPosition == selectedPosition)
                    R.drawable.selected_date_background // Use color resource for selected position
                else
                    R.drawable.analytics_layout_background // Use drawable for default background
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.date_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dates[position])
    }

    override fun getItemCount(): Int = dates.size
}