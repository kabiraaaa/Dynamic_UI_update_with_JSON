package com.example.uifromjson

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.uifromjson.adapters.MetroServicesAdapter
import com.example.uifromjson.data.MetroService
import com.example.uifromjson.databinding.ActivityMainBinding
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.google.gson.Gson
import com.google.gson.JsonObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        RemoteConfigHelper.initializeRemoteConfig { json ->
            val gson = Gson()
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            updateRecyclerView(jsonObject, gson)
            homeBanner(jsonObject)
            updateHeader(jsonObject)
        }
    }

    private fun updateHeader(jsonObject: JsonObject?) {
        val headerJson = jsonObject?.getAsJsonObject("header")
        val title = headerJson?.get("greeting")?.asString
        val buttons = headerJson?.getAsJsonArray("buttons")

        runOnUiThread {
            binding.tvGreet.text = title

            binding.ivAccount.visibility = View.GONE
            binding.ivAlert.visibility = View.GONE
            binding.ivSearch.visibility = View.GONE
            binding.btnMap.visibility = View.GONE

            buttons?.forEach { element ->
                val button = element.asJsonObject
                val icon = button.get("icon")?.asString
                val visible = button.get("visible")?.asBoolean ?: false

                when (icon) {
                    "account" -> binding.ivAccount.visibility =
                        if (visible) View.VISIBLE else View.GONE

                    "alerts" -> binding.ivAlert.visibility =
                        if (visible) View.VISIBLE else View.GONE

                    "search" -> binding.ivSearch.visibility =
                        if (visible) View.VISIBLE else View.GONE

                    "map" -> binding.btnMap.visibility = if (visible) View.VISIBLE else View.GONE
                }
            }
        }
    }


    private fun setupRecyclerView() {
        binding.rvMetroServices.layoutManager = GridLayoutManager(this, 3)
        binding.rvMetroServices.adapter = MetroServicesAdapter(emptyList())
    }

    private fun updateRecyclerView(jsonObject: JsonObject, gson: Gson) {
        val servicesJson = jsonObject.getAsJsonObject("services")
        val columns = servicesJson.get("columns").asInt
        val list = servicesJson.getAsJsonArray("list")

        val services = list.map { gson.fromJson(it, MetroService::class.java) }
            .filter { it.visible }

        runOnUiThread {
            (binding.rvMetroServices.adapter as MetroServicesAdapter).updateData(services)
            (binding.rvMetroServices.layoutManager as GridLayoutManager).spanCount = columns
        }
    }

    private fun homeBanner(jsonObject: JsonObject) {
        val shimmer = Shimmer.AlphaHighlightBuilder()
            .setDuration(1800)
            .setBaseAlpha(0.7f)
            .setHighlightAlpha(0.6f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()

        val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(shimmer)
        }

        binding.ivHomeBanner.setImageDrawable(shimmerDrawable)

        val imageUrl = jsonObject.getAsJsonObject("banner").get("imageUrl").asString

        Glide.with(this)
            .load(imageUrl)
            .placeholder(shimmerDrawable)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.ivHomeBanner.setImageDrawable(resource)
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(binding.ivHomeBanner)
    }

}
