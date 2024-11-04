package com.opsc.opsc7312poe.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312poe.R
import com.opsc.opsc7312poe.api.data.TransactionAnalysis

class TransactionAnalysisAdapter (
    private val analysisList: MutableList<TransactionAnalysis>
) : RecyclerView.Adapter<TransactionAnalysisAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val holder: LinearLayout = itemView.findViewById(R.id.analysis_holder)
        private val logoHolder: LinearLayout = itemView.findViewById(R.id.logo_bg)
        private val logoImg: ImageView = itemView.findViewById(R.id.logo_img)
        private val label: TextView = itemView.findViewById(R.id.label)
        private val amount: TextView = itemView.findViewById(R.id.amount)


        fun bind(analysis: TransactionAnalysis) {
            // Bind data to the UI components
            holder.setBackgroundResource(analysis.background) // Assuming you have a name field
            logoHolder.setBackgroundResource(analysis.background)
            logoImg.setImageResource(analysis.logo)
            label.text = analysis.type
            amount.text = analysis.amount
            amount.setTextColor(ContextCompat.getColor(itemView.context, analysis.color))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.transactions_analysis_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val analysis = analysisList[position]
        holder.bind(analysis)
    }

    override fun getItemCount(): Int = analysisList.size

//    fun updateProducts(products: List<Product>){
//        productList.clear()
//        productList.addAll(products)
//        notifyDataSetChanged()
//    }
}