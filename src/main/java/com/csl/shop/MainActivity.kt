package com.csl.shop

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.row_function.view.*
import org.jetbrains.anko.*
import java.net.URL

class MainActivity : AppCompatActivity(), AnkoLogger {

    private val TAG = MainActivity::class.java.simpleName
    private val RC_SIGNUP =  200
    private val RC_NICKNAME = 210
    var cacheService: Intent? = null
    var signup = false
    val auth = FirebaseAuth.getInstance()
    val funtions = listOf<String>(
        "Camera",
        "Contacts",
        "Parking",
        "Movies",
        "Bus",
        "News",
    "Map",
    "C",
    "d",
    "E",
    "f",
    "G",
    "h")

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action.equals(CacheService.ACTION_CACHE_DONE))
                info("MainActivity cache informed")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))


        auth.addAuthStateListener {
            authChange(it)
        }


        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }


        //spinner
        val colors = arrayOf("Red", "Green", "Blue")
        val adapter = ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, colors)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d(TAG, "onItemSelected: ${colors[position]}")
            }

        }

        //RecyclerView
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)
        recycler.adapter = FunctionAdapter()
    }

    inner class FunctionAdapter : RecyclerView.Adapter<FunctionAdapter.FunctionHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FunctionHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_function, parent, false)
            val holder = FunctionHolder(view)
            return holder
        }

        override fun getItemCount(): Int {
            return funtions?.size ?: 0

        }

        override fun onBindViewHolder(holder: FunctionHolder, position: Int) {
            holder.nameText.text = funtions[position]
            holder.itemView.setOnClickListener {
                functionClicked(holder, position)
            }
        }

        private fun functionClicked(holder: MainActivity.FunctionAdapter.FunctionHolder, position: Int) {
            Log.d(TAG, "functionClicked: $position")
            when(position) {
                1 -> startActivity(Intent(holder.itemView.context, ContactActivity::class.java))
                2 ->  startActivity(Intent(holder.itemView.context, ParkingActivity::class.java))
                3 ->  startActivity(Intent(holder.itemView.context, MovieActivity::class.java))
                4 ->  startActivity(Intent(holder.itemView.context, BusActivity::class.java))
                5 ->  startActivity(Intent(holder.itemView.context, NewsActivity::class.java))
                6 ->  startActivity(Intent(holder.itemView.context, MapsActivity::class.java))
            }


        }

        inner class FunctionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var nameText : TextView = itemView.name
        }
    }

    private fun authChange(auth: FirebaseAuth) {
        if (auth.currentUser == null) {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivityForResult(intent, RC_SIGNUP)
        } else {
            Log.d(TAG, "authChange: ${auth.currentUser?.uid}")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_cache -> {

//                cacheService = Intent(this, CacheService::class.java)
//                startService(cacheService)

                doAsync {
                    //方法一
                    val json =
                        URL("https://gist.githubusercontent.com/saniyusuf/406b843afdfb9c6a86e25753fe2761f4/raw/523c324c7fcc36efab8224f9ebb7556c09b69a14/Film.JSON").readText()
                    val movies = Gson().fromJson<Movie>(json, Movie::class.java)
//                val intent = Intent(this, CacheService::class.java)
//                intent.putExtra("TITLE", movies[0].Title)
//                intent.putExtra("URL", movies[0].Poster)
//                startService(intent)

                    //使用ANKO
                    movies.forEach {
                        startService(
                            intentFor<CacheService>(
                                "TITLE" to it.Title,
                                "URL" to it.Poster
                            )
                        )
                    }
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) {
                val intent = Intent(this, NicknameActivity::class.java)
                startActivityForResult(intent, RC_NICKNAME)
            }
            else
                finish()
        }
        else if (requestCode == RC_NICKNAME) {

        }
    }

    override fun onResume() {
        super.onResume()

        if (auth.currentUser?.uid == null)
            nickname.text = getNickname()
        else {
            FirebaseDatabase.getInstance()
                .getReference("users")
                .child(auth.currentUser!!.uid)
                .child("nickname")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists())
                            nickname.text = dataSnapshot.value as String
                    }
                })
        }
    }

    override fun onStart() {
        super.onStart()

        val filter = IntentFilter(CacheService.ACTION_CACHE_DONE)
        registerReceiver(broadcastReceiver, filter)
    }

    override fun onStop() {
        super.onStop()

//        stopService(cacheService)
        unregisterReceiver(broadcastReceiver)
    }
}