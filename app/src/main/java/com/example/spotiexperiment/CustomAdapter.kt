package com.example.spotiexperiment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spotify.protocol.types.Track
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


class CustomAdapter(songs: ArrayList<SavedSong>): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val titleTV = itemView.findViewById<TextView>(R.id.titleSongRV)
        val imageTV = itemView.findViewById<ImageView>(R.id.imageAlbumRV)
        val authorTV = itemView.findViewById<TextView>(R.id.authorSongRV)
    }

    private var songs: ArrayList<SavedSong> = songs


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        //Inflate the custom layout
        val trackView = inflater.inflate(R.layout.card_view_design, parent, false)
        //Return a new holder instance
        return ViewHolder(trackView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get the data model based on position
        val track: Track = songs.get(position).track
        //Set item views based on your views and data model
        val textViewTitle = holder.titleTV
        textViewTitle.setText(track.name)
        val textViewAuthor = holder.authorTV
        textViewAuthor.setText(track.artist.name)

        val imageView= holder.imageTV
        //val image = songs.get(position).getImageTrack()
        val context = imageView.context
        var image = loadImageFromStorage(songs.get(position).imagePath, songs.get(position).track.album.name)
        Log.d("Image path adapter", songs.get(position).imagePath)
        if(image == null){
            //image = BitmapFactory.decodeResource()
            Log.d("IMAGE", "THE IMAGE IS NULL")
        }else{
            Log.d("IMAGE", "THE IMAGE IS NOOOTTT NULL")
        }
        imageView.setImageBitmap(image)
    }

    override fun getItemCount(): Int {
        return songs.size
    }
    private fun loadImageFromStorage(path: String, name:String): Bitmap? {
        try {
            val f = File(path, "$name.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            //val img = findViewById<View>(R.id.imgPicker) as ImageView
            return b
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

}