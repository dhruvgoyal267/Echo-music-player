package com.example.echo.fragments


import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.echo.R
import com.example.echo.Songs
import com.example.echo.adapters.MainScreenAdapter
import com.example.echo.fragments.MainScreenFragment.Statified.recyclerViewGroup
import java.util.*
import kotlin.collections.ArrayList

class MainScreenFragment : Fragment(),
    android.support.v7.widget.SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val userInput = newText?.toLowerCase()
        val newList = ArrayList<Songs>()
        val oldList = getAllSongs()
        for (names in oldList) {
            if (names.songName.toLowerCase().contains(userInput!!)) {
                newList.add(names)
            }
        }
        mainScreenAdapter?.updateList(newList)
        return true
    }

    object Statified {
        var mediaPlayer: MediaPlayer? = null
        var bottomBar: RelativeLayout? = null
        var recyclerViewGroup: RecyclerView? = null
    }

    var songTitleBottomBar: TextView? = null
    var getSongsList: ArrayList<Songs>? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var visibleLayout: RelativeLayout? = null
    var noSongs: RelativeLayout? = null
    var noSongsText: TextView? = null
    var myActivity: Activity? = null
    var mainScreenAdapter: MainScreenAdapter? = null

    var trackPosition = 0
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var prefsForDark =
            myActivity?.getSharedPreferences(SettingFragment.Staticated.MY_PREFS_DARK, Context.MODE_PRIVATE)
        var isDark = prefsForDark?.getBoolean("DarkFeature", false)
        if (isDark as Boolean) {
            visibleLayout?.setBackgroundColor(Color.BLACK)
            noSongs?.setBackgroundColor(Color.BLACK)
            noSongsText?.setTextColor(Color.WHITE)
        }
        getSongsList = getAllSongs()
        val pref = activity?.getSharedPreferences("action", Context.MODE_PRIVATE)
        val action_sort_ascending = pref?.getString("action_sort_ascending", "true")
        val action_sort_date = pref?.getString("action_sort_date", "false")

        if (getSongsList == null) {
            visibleLayout?.visibility = View.INVISIBLE
            noSongs?.visibility = View.VISIBLE
        } else {
            mainScreenAdapter = MainScreenAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
            val layoutManager = LinearLayoutManager(myActivity)
            recyclerViewGroup?.layoutManager = layoutManager
            recyclerViewGroup?.itemAnimator = DefaultItemAnimator()
            recyclerViewGroup?.adapter = mainScreenAdapter
            myActivity?.title = "All Songs"
        }

        if (getSongsList != null) {
            if (action_sort_ascending!!.equals("true", ignoreCase = true)) {
                Collections.sort(getSongsList, Songs.Statitfied.nameComparator)
            } else if (action_sort_date!!.equals("true", ignoreCase = true)) {
                Collections.sort(getSongsList, Songs.Statitfied.dateComparator)
            }
        }
        mainScreenAdapter?.notifyDataSetChanged()
        bottomBar()
    }

    fun bottomBar() {
        try {
            bottomBarClickHandler()
            songTitleBottomBar?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle

            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener {
                SongPlayingFragment.Staticated.onCompletion()
                songTitleBottomBar?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle
            }
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                Statified.bottomBar?.visibility = View.VISIBLE

            } else {
                Statified.bottomBar?.visibility = View.INVISIBLE

            }
        } catch (e: Exception) {

        }
    }

    fun bottomBarClickHandler() {
        Statified.bottomBar?.setOnClickListener {
            Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
            var args = Bundle()
            val songPlayingFragment = SongPlayingFragment()
            args.putString("songArtist", SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putInt("songId", SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition", SongPlayingFragment.Statified.currentPosition)
            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)
            args.putString("BottomBar", "successMain")
            songPlayingFragment.arguments = args
            fragmentManager?.beginTransaction()?.replace(R.id.details_fragment, songPlayingFragment)
                ?.addToBackStack("Main Screen Fragment")
                ?.commit()
        }
        playPauseButton?.setOnClickListener {
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaPlayer!!.pause()
                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaPlayer!!.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        val view = inflater!!.inflate(R.layout.fragment_main_screen, container, false)
        visibleLayout = view?.findViewById(R.id.visibleLayout)
        playPauseButton = view?.findViewById<ImageButton>(R.id.playPauseButton)
        noSongs = view?.findViewById(R.id.noSongs)
        noSongsText = view?.findViewById(R.id.noSongsText)
        songTitle = view?.findViewById(R.id.songTitleMainScreen)
        songTitleBottomBar = view?.findViewById(R.id.songTitleMainScreen)
        recyclerViewGroup = view?.findViewById(R.id.contentMain)
        Statified.bottomBar = view?.findViewById(R.id.hiddenBarMainScreen)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)
        val menuItem = menu?.findItem(R.id.search)
        val searchView = menuItem?.actionView as android.support.v7.widget.SearchView
        searchView?.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {

        val item = menu?.findItem(R.id.sortIcon)
        item?.isVisible = true
        val item2 = menu?.findItem(R.id.search)
        item2?.isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity?
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val switcher = item?.itemId
        if (switcher == R.id.sort_by_name) {
            var editor = myActivity?.getSharedPreferences("action", Context.MODE_PRIVATE)
                ?.edit()
            editor?.putString("action_sort_ascending", "true")
            editor?.putString("action_sort_date", "false")
            editor?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statitfied.nameComparator)
            }
            mainScreenAdapter?.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.sort_by_date) {
            var editor = myActivity?.getSharedPreferences("action", Context.MODE_PRIVATE)
                ?.edit()
            editor?.putString("action_sort_ascending", "false")
            editor?.putString("action_sort_date", "true")
            editor?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statitfied.dateComparator)
            }
            mainScreenAdapter?.notifyDataSetChanged()
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    fun getAllSongs(): ArrayList<Songs> {
        var arrayList = ArrayList<Songs>()
        var contenResolver = myActivity?.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor = contenResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songName = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateAdded = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            arrayList.add(
                Songs(
                    songCursor.getLong(songId),
                    songCursor.getString(songName),
                    songCursor.getString(songArtist),
                    songCursor.getString(songData),
                    songCursor.getLong(dateAdded)
                )
            )

            while (songCursor.moveToNext()) {
                val currentid = songCursor.getLong(songId)
                val currentname = songCursor.getString(songName)
                val currentArtist = songCursor.getString(songArtist)
                val currentData = songCursor.getString(songData)
                val curretDate = songCursor.getLong(dateAdded)
                arrayList.add(Songs(currentid, currentname, currentArtist, currentData, curretDate))
            }
        }

        return arrayList
    }

}
