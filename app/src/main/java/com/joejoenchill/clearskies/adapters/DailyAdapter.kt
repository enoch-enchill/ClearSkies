package com.joejoenchill.clearskies.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.joejoenchill.clearskies.R
import com.joejoenchill.clearskies.models.DailyItem
import com.joejoenchill.clearskies.utils.TimeConverter


class DailyAdapter(private val dailyDataList: List<DailyItem>, private val context: Context) : RecyclerView.Adapter<DailyAdapter.DailyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.daily_item, parent, false)
        return DailyHolder(view)
    }

    override fun onBindViewHolder(holder: DailyHolder, position: Int) {
        val dailyDataItem: DailyItem = dailyDataList[position]

        // Day
        val day: String = TimeConverter.toDay((dailyDataItem.dt))
        holder.txtDayOfWeek.text = day

        // Date
        val date: String = TimeConverter.toDate(dailyDataItem.dt)
        holder.txtDate.text = date

        // Icon
        val imageUrl: String = context.getString(R.string.icon, dailyDataItem.weather[0].icon)
        holder.imgIcon.load(imageUrl)

        // Temperature
        val valTemp: String = context.getString(R.string.temperature,dailyDataItem.temp.day.toInt())
        holder.textTemperature.text = valTemp

        // Humidity
        val valHumidity: String = context.getString(R.string.humidity, dailyDataItem.humidity)
        holder.txtHumidity.text = valHumidity

        // Percentage chance of Precipitation
        holder.txtPoP.text = dailyDataItem.pop.toString()

    }

    override fun getItemCount(): Int {
        return dailyDataList.size
    }

    class DailyHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var txtDayOfWeek: TextView = itemView.findViewById(R.id.txtDailyDayOfWeek)
        var txtDate: TextView = itemView.findViewById(R.id.txtDailyDate)
        var imgIcon: ImageView = itemView.findViewById(R.id.imgDailyWeatherIcon)
        var textTemperature: TextView = itemView.findViewById(R.id.textDailyTemperature)
        var txtHumidity : TextView = itemView.findViewById(R.id.txtDailyHumidity)
        var txtPoP : TextView = itemView.findViewById(R.id.txtDailyPoP)
    }

}