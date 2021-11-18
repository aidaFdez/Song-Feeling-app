package com.example.spotiexperiment

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Image
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import java.io.*
import java.lang.reflect.Type
import API_codes

private val clientId = API_codes().clientId()
private val redirectUri = "https://github.com/aidaFdez"
private var mSpotifyAppRemote: SpotifyAppRemote? = null
//var signed_in = false

var builder = AuthorizationRequest.Builder(clientId, AuthorizationResponse.Type.TOKEN, redirectUri)

private var REQUEST_CODE = 1337

private var songsAdded = ArrayList<SavedSong>()
private var albumSaved = HashMap<String, String>()
private var songNames = ArrayList<String>()
private var feelingsList = ArrayList<String>()

class MainActivity : AppCompatActivity() {
    private var currentTrack: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)


        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
                )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onStart(){
        super.onStart()

        val connectionParams = ConnectionParams.Builder(clientId).
            setRedirectUri(redirectUri)
            .showAuthView(true).build()

        SpotifyAppRemote.connect(this, connectionParams,
                object : Connector.ConnectionListener {
                    override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote
                        Log.d("MainActivity", "Connected! Yay!")

                        // Now you can start interacting with App Remote
                        connected(mSpotifyAppRemote!!)
                    }

                    override fun onFailure(throwable: Throwable) {
                        Log.e("MainActivity", throwable.message, throwable)

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                })
    }


    private fun connected(spotifyAppRemote: SpotifyAppRemote){
        spotifyAppRemote.playerApi.subscribeToPlayerState().setEventCallback {
            val track: Track = it.track
            Log.d("MainActivity", track.name + " by " + track.artist.name)
            currentTrack = track
            //Update song name
            updateSongName(track)
            //Change album image
            Log.d("Main Activity", "Album cover being updated from Main")
            updateAlbumCover(track)
        }
    }

    fun getConnected(){
        builder.setScopes(arrayOf("streaming"))
        val request = builder.build()
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
        Log.d("Logging", "Requested code")
        //finish()
        Log.d("Logging", "Activity supposedly finished")
        //onActivityResult(REQUEST_CODE, REQUEST_CODE, null)
    }

    fun addSong(track: Track){
        Log.d("Main Activity", "Adding song")
        //Create the object
        var albumImage: Bitmap?

        //Get the list of added songs
        songsAdded = getSongsAdded()

        //Get the list of song names that have been saved
        songNames = getSongNames()

        //Get the list of downloaded album images
        albumSaved = getAlbumSaved()

        //If the song has not been added before, add it
        if (!songNames.contains(track.name)){
            var imagePath = ""

            if(!albumSaved.containsKey(track.album.name)){
                //Get the thumbnail of the album
                mSpotifyAppRemote
                        ?.imagesApi
                        ?.getImage(track.imageUri, Image.Dimension.THUMBNAIL)
                        ?.setResultCallback { bitmap ->
                            albumImage = bitmap
                            Log.d("Album image", "The album image downloading")
                            //Save the image
                            imagePath = albumImage?.let { saveToInternalStorage(it, track.album.name) }.toString()

                            //Saving the thing
                            if (imagePath != "") {
                                albumSaved.put(track.album.name, imagePath)
                                putSharedPrefs("saved_albums", albumSaved)
                            }
                            else{
                                Log.d("Saving album image", "The path to the album image was null")
                            }

                            //Save the stuff that needs to be saved
                            val toAdd = SavedSong(track, imagePath)
                            if(songsAdded.isNullOrEmpty()){
                                //Make sure that the list at least exists (it should given the definition of the function it called)
                                songsAdded =  ArrayList<SavedSong>()
                            }
                            songsAdded.add(toAdd)
                            songNames.add(track.name)
                            putSharedPrefs("saved_names", songNames)

                            //Update the saved list of added songs
                            putSharedPrefs("saved_songs", songsAdded)

                            val toast = Toast.makeText(this.applicationContext, "The song has been added", Toast.LENGTH_SHORT)
                            toast.show()

                            //return@setResultCallback
                        }
            }
            else{
                //Get the path of the already saved album image
                imagePath = albumSaved[track.album.name].toString()

                Log.d("Image album path 2", imagePath)

                val toAdd = SavedSong(track, imagePath)
                if(songsAdded.isNullOrEmpty()){

                    songsAdded =  ArrayList<SavedSong>()
                    songsAdded.add(toAdd)
                }
                songsAdded.add(toAdd)
                songNames.add(track.name)
                putSharedPrefs("saved_names", songNames)
                putSharedPrefs("saved_songs", songsAdded)

                val toast = Toast.makeText(this.applicationContext, "The song has been added", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        else{
            val toast = Toast.makeText(this.applicationContext, "The song is already saved", Toast.LENGTH_SHORT)
            toast.show()
        }
        //Update the saved list of added songs
        //putSharedPrefs("saved_songs", songsAdded)

    }

    fun updateAlbumCover(track: Track){
        Log.d("Main Activity", "Album cover being updated")
        val albumCover = findViewById<ImageView>(R.id.album_image)
        lateinit var albumImage: Bitmap
        //Change the album on screen
        mSpotifyAppRemote
                ?.imagesApi
                ?.getImage(track.imageUri, Image.Dimension.MEDIUM)
                ?.setResultCallback { bitmap ->
                    albumCover.setImageBitmap(bitmap)
                }

        Log.d("Album uri", track.imageUri.toString())
    }

    fun updateSongName(track: Track){
        val titleView = findViewById<TextView>(R.id.text_home)
        titleView.text = track.name
    }

    fun <T> putSharedPrefs(key: String, `object`: T){
        val sharedPrefs = this.getSharedPreferences("shared_prefs_songs", MODE_PRIVATE)
        val jsonString = GsonBuilder().create().toJson(`object`)
        //Save to the file
        sharedPrefs.edit().putString(key, jsonString).apply()
    }

    inline fun <reified T> getSharedPrefsData(key: String, typeToken: Type): T {
        val sharedPrefs = this.getSharedPreferences("shared_prefs_songs", MODE_PRIVATE)
        val value = sharedPrefs.getString(key, null)
        return GsonBuilder().create().fromJson(value, typeToken)
    }


    //GETTER FUNCTIONS

    fun getSigned(): Boolean {
        return true
    }

    fun getCurrentTrack(): Track?{
        return currentTrack
    }

    fun getSongsAdded(): ArrayList<SavedSong>{
        val type = object:TypeToken<ArrayList<SavedSong>>(){}.type
        var toRet: ArrayList<SavedSong> = getSharedPrefsData("saved_songs", type)
        if(toRet.isNullOrEmpty()){
            toRet = ArrayList<SavedSong>()
        }
        return toRet
    }

    fun getSongNames(): ArrayList<String>{
        val type = object:TypeToken<ArrayList<String>>(){}.type
        var toRet: ArrayList<String> = getSharedPrefsData("saved_names", type)
        if(toRet.isNullOrEmpty()){
            toRet = ArrayList<String>()
        }
        return toRet
    }

    fun getAlbumSaved(): HashMap<String, String>{
        val type = object:TypeToken<HashMap<String, String>>(){}.type
        var toRet: HashMap<String, String> = getSharedPrefsData("saved_albums", type)
        if(toRet.isNullOrEmpty()){
            toRet = HashMap<String, String>()
        }
        return toRet
    }

    fun getFeelingsList(): ArrayList<String>{
        val type = object:TypeToken<ArrayList<String>>(){}.type
        var toRet: ArrayList<String> = getSharedPrefsData("feelings_list", type)
        if(toRet.isNullOrEmpty()){
            toRet = ArrayList()
            toRet.add("Happy")
            toRet.add("Sad")
            toRet.add("Melancholic")
            toRet.add("Angry")
            toRet.add("Friendly")
            toRet.add("Heartbroken")
            toRet.add("Lonely")
            toRet.add("Grief")
            toRet.add("Hopeful")
        }
        //Sort the list alphabetically so it's easier for the user
        val sorted = toRet.sorted()
        toRet = ArrayList<String>(sorted)
        return toRet
    }


    //Other helping functions
    private fun assetsToBitmap(fileName: String): Bitmap? {
        Log.d("Download image", fileName)
        return try{
            val stream = assets.open(fileName)
            BitmapFactory.decodeFile(fileName)
        }catch (e: IOException){
            e.printStackTrace()
            null
        }
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap, name:String): String {
        val cw = ContextWrapper(applicationContext)
        // path to /data/data/yourapp/app_data/imageDir
        val directory: File = cw.getDir("imageDir", MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, "$name.jpg")
        Log.d("album image", mypath.path)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (fos != null) {
                    fos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("Download of image", "The download was unsuccessful")
                return ""
            }
        }
        return directory.getAbsolutePath()
    }

    private fun loadImageFromStorage(path: String): Bitmap? {
        try {
            val f = File(path, "profile.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            //val img = findViewById<View>(R.id.imgPicker) as ImageView
            return b
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
}