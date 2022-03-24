package com.robertweather.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.robertweather.R
import com.robertweather.network.model.Current
import java.text.SimpleDateFormat
import java.util.*

class PredictionCardAdapter(private var mContext: Context, private val cardsData: List<Current>) :
    RecyclerView.Adapter<PredictionCardAdapter.PredictionCardHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionCardHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PredictionCardHolder(
            mContext,
            layoutInflater.inflate(
                R.layout.weather_small_card,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PredictionCardHolder, position: Int) {
        holder.render(cardsData[position])
    }

    override fun getItemCount(): Int = cardsData.size

    class PredictionCardHolder(private var mContext: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val predictionImage = itemView.findViewById<ImageView>(R.id.hour_prediction_icon)
        private val predictionTitle = itemView.findViewById<TextView>(R.id.hour_prediction_title)
        private val predictionValue = itemView.findViewById<TextView>(R.id.hour_prediction_value)

        fun render(current: Current) {
            val icon = current.weather.first().icon.replace('n', 'd')
            val iconResource = mContext.resources.getIdentifier("ic_weather_$icon", "drawable", mContext.packageName)
            val dateFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            val hour = dateFormatter.format(Date(current.dt*1000))

            predictionImage.load(iconResource)
            predictionTitle.text = hour
            predictionValue.text = mContext.getString(R.string.formatted_temp, current.temp.toString())
        }
    }
}