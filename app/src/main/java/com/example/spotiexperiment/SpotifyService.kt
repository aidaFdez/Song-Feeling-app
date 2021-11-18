package com.example.spotiexperiment

import android.content.Context
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote

object SpotifyService {
    private const val clientId = "254980f1c3ab43b9889e9f234941297e"
    private const val redirectUri = "https://github.com/aidaFdez"

    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var connectionParams: ConnectionParams = ConnectionParams.Builder(clientId)
        .setRedirectUri(redirectUri)
        .showAuthView(true)
        .build()

    fun connect(context: Context, handler:(connected:Boolean) -> Unit){
        if (spotifyAppRemote?.isConnected == true){
            handler (true)
            return
        }
        val connectionListener = object: Connector.ConnectionListener{
            override fun onConnected(spotifyAppRemote: SpotifyAppRemote){
                this@SpotifyService.spotifyAppRemote = spotifyAppRemote
                handler(true)
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("SpotifyService", throwable.message, throwable)
                handler(false)
            }
        }
        SpotifyAppRemote.connect(context, connectionParams, connectionListener)
    }
}