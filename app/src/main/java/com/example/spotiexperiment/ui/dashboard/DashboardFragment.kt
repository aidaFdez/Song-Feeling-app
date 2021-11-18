package com.example.spotiexperiment.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotiexperiment.CustomAdapter
import com.example.spotiexperiment.MainActivity
import com.example.spotiexperiment.R

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        //(activity as AppCompatActivity?)!!.getSupportActionBar()!!.show()

        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        //val textView: TextView = root.findViewById(R.id.text_dashboard)

        val songRV = root.findViewById<View>(R.id.songsRV) as RecyclerView
        val adapter = CustomAdapter((activity as MainActivity).getSongsAdded())
        songRV.adapter = adapter
        songRV.layoutManager = LinearLayoutManager(root.context)

        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            //textView.text = it
            val songRV = root.findViewById<View>(R.id.songsRV) as RecyclerView
            val adapter = CustomAdapter((activity as MainActivity).getSongsAdded())
            songRV.adapter = adapter
            songRV.layoutManager = LinearLayoutManager(root.context)

        })
        return root
    }
}