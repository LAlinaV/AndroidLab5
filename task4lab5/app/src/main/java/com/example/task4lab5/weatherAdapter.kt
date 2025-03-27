package com.example.task4lab5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class WeatherAdapter(private val cities: List<String>, private val activity: MainActivity) :
    RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = cities[position]
        holder.cityName.text = city
        holder.itemView.setOnClickListener { v: View? ->
            activity.showWeatherDetails(
                city
            )
        }
        holder.itemView.setOnLongClickListener { v: View? ->
            activity.showWeatherForecast(city)
            true
        }
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cityName: TextView

        init {
            cityName = itemView.findViewById<TextView>(R.id.cityName)
        }
    }
}