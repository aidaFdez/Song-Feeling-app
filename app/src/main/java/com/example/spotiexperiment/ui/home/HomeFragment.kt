package com.example.spotiexperiment.ui.home

import FeelingAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotiexperiment.MainActivity
import com.example.spotiexperiment.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

//import com.example.spotiexperiment.signed_in

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity?)!!.supportActionBar!!.hide()



        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        var track = (activity as MainActivity).getCurrentTrack()


        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            //Get the views of the buttons
            val button_connect = view?.findViewById<FloatingActionButton>(R.id.spotify_login_btn)
            val addSongBtn = view?.findViewById<Button>(R.id.add_song)

            //RecyclerView of feelings
            val feelingsRV = root.findViewById<View>(R.id.feeling_list) as RecyclerView
            val adapterFeels = FeelingAdapter((activity as MainActivity).getFeelingsList())

            feelingsRV.adapter = adapterFeels
            feelingsRV.layoutManager = LinearLayoutManager(root.context)

            //Update track info on screen
            if (track != null) {
                (activity as MainActivity).updateSongName(track)
                Log.d("Main Activity", "Album cover being updated from Fragment")
                (activity as MainActivity).updateAlbumCover(track)
            }
            //Connect to spotify button function
            if (button_connect != null) {
                button_connect.setOnClickListener {
                    Log.d("Button connect", "Button for connecting pressed in fragment 2")
                    //if (!signed_in){
                    (activity as MainActivity).getConnected()
                    //}
                }
            }
            //Add song button function
            if (addSongBtn != null) {
                addSongBtn.setOnClickListener {
                    Log.d("Button adding", "Button for adding song pressed in fragment")
                    var trackHere = (activity as MainActivity).getCurrentTrack()
                    if (trackHere != null) {
                        //Add to the arraylist in the MainActivity
                        (activity as MainActivity).addSong(trackHere)
                    } else {
                        Log.d("Button connect", "Song is null")
                    }
                }
            }

        })
        return root
    }

    /*override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }*/
}