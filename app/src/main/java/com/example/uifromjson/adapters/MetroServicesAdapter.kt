package com.example.uifromjson.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uifromjson.R
import com.example.uifromjson.data.MetroService

class MetroServicesAdapter(private var services: List<MetroService>) :
    RecyclerView.Adapter<MetroServicesAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_services, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        holder.bind(service)
    }

    override fun getItemCount(): Int = services.size

    fun updateData(newServices: List<MetroService>) {
        services = newServices
        notifyDataSetChanged()
    }

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.ivServiceIcon)
        private val name: TextView = itemView.findViewById(R.id.tvServiceName)

        fun bind(service: MetroService) {
            name.text = service.name
            icon.setImageResource(R.drawable.ic_android_black_24dp)
        }
    }
}
