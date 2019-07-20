package com.example.echo.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.echo.R
import com.example.echo.Songs
import com.example.echo.fragments.MainScreenFragment
import com.example.echo.fragments.SettingFragment
import com.example.echo.fragments.SongPlayingFragment

class MainScreenAdapter(_songsList: ArrayList<Songs>, _context: Context) :
    RecyclerView.Adapter<MainScreenAdapter.MainScreenViewHolder>() {

    var songList: ArrayList<Songs>? = null
    var context: Context? = null

    init {
        this.songList = _songsList
        this.context = _context
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MainScreenViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
            .inflate(R.layout.row_custom_main_screen, parent, false)
        return MainScreenViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if (songList == null) {
            return 0
        } else {
            return (songList as ArrayList<Songs>).size
        }
    }

    override fun onBindViewHolder(holder: MainScreenViewHolder, position: Int) {
        val songObject = songList?.get(position)
        var updateSongTitle = songObject?.songName
        var updateSongArtist = songObject?.artist
        if (updateSongTitle == "<unknown>") {
            updateSongTitle = "unknown"
        }
        if (updateSongArtist == "<unknown>") {
            updateSongArtist = "unknown"
        }
        holder.trackArtist?.text = updateSongArtist
        holder.trackTitle?.text = updateSongTitle
        var prefsForDark =
            (context as Activity)?.getSharedPreferences(SettingFragment.Staticated.MY_PREFS_DARK, Context.MODE_PRIVATE)
        var isDark = prefsForDark?.getBoolean("DarkFeature", false)
        if (isDark as Boolean) {
            holder.contentHolder?.setBackgroundColor(Color.BLACK)
            holder.trackTitle?.setTextColor(Color.WHITE)
            holder.trackArtist?.setTextColor(Color.GRAY)
        }
        holder.contentHolder?.setOnClickListener {
            var args = Bundle()
            val songPlayingFragment = SongPlayingFragment()
            args.putString("songArtist", songObject?.artist)
            args.putString("songTitle", songObject?.songName)
            args.putString("path", songObject?.songData)
            args.putInt("songId", songObject?.songId?.toInt() as Int)
            args.putInt("songPosition", position)
            args.putParcelableArrayList("songData", songList)
            songPlayingFragment.arguments = args
            val inputManager: InputMethodManager =
                (context as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(MainScreenFragment.Statified.recyclerViewGroup?.windowToken, 0)
            (context as FragmentActivity).supportFragmentManager
                .beginTransaction().replace(R.id.details_fragment, songPlayingFragment)
                .addToBackStack("Song Playing Fragment")
                .commit()
        }
    }

    class MainScreenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            this.trackTitle = itemView.findViewById(R.id.trackTitle) as TextView
            this.trackArtist = itemView.findViewById(R.id.trackArtist) as TextView
            this.contentHolder = itemView.findViewById(R.id.contentRow) as RelativeLayout
        }
    }

    fun updateList(newList: ArrayList<Songs>) {
        songList = ArrayList()
        songList!!.addAll(newList)
        notifyDataSetChanged()
    }
}