package com.example.spotiexperiment

import android.graphics.Bitmap
import com.spotify.protocol.client.CallResult
import com.spotify.protocol.types.Track

class SavedSong(var track:Track, var imagePath: String){
    fun getTrackSong():Track{
        return track
    }

    fun getImageTrack():String{
        return imagePath
    }

    init{
        this.track = track
        this.imagePath = imagePath
    }
}