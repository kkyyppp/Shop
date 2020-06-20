package com.csl.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.row_movie.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import org.jetbrains.anko.uiThread
import java.net.URL

class MovieActivity : AppCompatActivity(), AnkoLogger {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        doAsync {
            val url =
                URL("https://gist.githubusercontent.com/saniyusuf/406b843afdfb9c6a86e25753fe2761f4/raw/523c324c7fcc36efab8224f9ebb7556c09b69a14/Film.JSON")
            val json = url.readText()


            //val movies = Gson().fromJson<List<MovieItem>>(json, object : TypeToken<List<MovieItem>>(){}.type)
            val movies = Gson().fromJson<Movie>(json, Movie::class.java)

            movies.forEach {
                info("${it.Title}  ${it.imdbRating}")
            }


            uiThread {
                recycler.layoutManager = LinearLayoutManager(this@MovieActivity)
                recycler.setHasFixedSize(true)
                recycler.adapter = MovieAdapter()
                (recycler.adapter as MovieAdapter).setData(movies)
            }
        }
    }

}

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieHolder>() {

    private var data : Movie = Movie()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.row_movie, parent, false)
        return MovieHolder(view)
    }

    override fun getItemCount(): Int {
       return data.size ?: 0
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
       holder.myMovie(data[position])
    }

     fun setData(data : Movie) {
        this.data = data
        notifyDataSetChanged()
    }

    inner class MovieHolder(view : View) : RecyclerView.ViewHolder(view){
        val titleText : TextView = view.movie_title
        val imdbText : TextView = view.movie_imdb
        val directorText : TextView = view.movie_director
        val posterImage : ImageView = view.movie_poster
        val view = view

        fun myMovie(movieItem : MovieItem) {
            titleText.text = movieItem.Title
            imdbText.text = movieItem.imdbRating
            directorText.text = movieItem.Director
            Glide.with(view.context)
                .load("https://m.media-amazon.com/images/M/MV5BNGVjNWI4ZGUtNzE0MS00YTJmLWE0ZDctN2ZiYTk2YmI3NTYyXkEyXkFqcGdeQXVyMTkxNjUyNQ@@._V1_SY1000_CR0,0,674,1000_AL_.jpg")
                .override(300)
                .into(posterImage)
        }
    }
}

class Movie : ArrayList<MovieItem>()

data class MovieItem(
    val Actors: String,
    val Awards: String,
    val ComingSoon: Boolean,
    val Country: String,
    val Director: String,
    val Genre: String,
    val Images: List<String>,
    val Language: String,
    val Metascore: String,
    val Plot: String,
    val Poster: String,
    val Rated: String,
    val Released: String,
    val Response: String,
    val Runtime: String,
    val Title: String,
    val Type: String,
    val Writer: String,
    val Year: String,
    val imdbID: String,
    val imdbRating: String,
    val imdbVotes: String,
    val totalSeasons: String
)

