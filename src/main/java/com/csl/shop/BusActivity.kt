package com.csl.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_bus.*
import kotlinx.android.synthetic.main.row_bus_info.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.*
import kotlin.collections.ArrayList as ArrayList1

class BusActivity : AppCompatActivity(), AnkoLogger {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://data.tycg.gov.tw/opendata/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus)

        doAsync {

            val busService  = retrofit.create(BusService::class.java)
            val buses = busService.listBuses()
                .execute()
                .body()


//            buses?.datas?.forEach {
//                info("${it.BusID}  ${it.RouteID}  ${it.Speed}")
//            }

            uiThread {
                recycler.layoutManager = LinearLayoutManager(this@BusActivity)
                recycler.setHasFixedSize(true)
                recycler.adapter = BusAdapter()

                if (buses != null) {
                    (recycler.adapter as BusAdapter).setData(buses.datas)
                }
            }
        }

    }
}

class BusAdapter : RecyclerView.Adapter<BusAdapter.BusHolder>() {

    private var data  = ArrayList<BusItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_bus_info, parent, false)
        return BusHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size ?: 0
    }

    override fun onBindViewHolder(holder: BusHolder, position: Int) {
        holder.myBus(data[position])
    }

    fun setData(data : ArrayList<BusItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    inner class BusHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val busIdText = itemView.bus_id
        val routeIdText = itemView.route_id
        val speedText = itemView.speed

        fun myBus(busItem: BusItem) {
            busIdText.text = busItem.BusID
            routeIdText.text = busItem.RouteID
            speedText.text = busItem.Speed
        }

    }
}


data class Bus(
    val datas: ArrayList<BusItem>
)

data class BusItem(
    val Azimuth: String,
    val BusID: String,
    val BusStatus: String,
    val DataTime: String,
    val DutyStatus: String,
    val GoBack: String,
    val Latitude: String,
    val Longitude: String,
    val ProviderID: String,
    val RouteID: String,
    val Speed: String,
    val ledstate: String,
    val sections: String
)

interface BusService {
    @GET("datalist/datasetMeta/download?id=b3abedf0-aeae-4523-a804-6e807cbad589&rid=bf55b21a-2b7c-4ede-8048-f75420344aed")
    fun listBuses() : Call<Bus>
}