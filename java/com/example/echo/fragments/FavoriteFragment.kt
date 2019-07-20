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
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.echo.EchoDataBase
import com.example.echo.R
import com.example.echo.Songs
import com.example.echo.adapters.FavoriteScreenAdapter

class FavoriteFragment : Fragment(){

    var myActivity :Activity? = null
    var playPauseButton : ImageButton? = null
    var songTitle : TextView? = null
    var visibleLayout : RelativeLayout? = null
    var recyclerViewGroup : RecyclerView? = null
    var noSongs : RelativeLayout? = null
    var noSongsText : TextView ? = null
    var favoriteScreenAdapter : FavoriteScreenAdapter?= null
    var bottomBar : RelativeLayout? = null
    var favoriteContent:EchoDataBase ? = null
    var songTitleBottomBar :TextView?=null
    var trackPosition =0
object Staticated
{
    var mediaPlayer : MediaPlayer? = null


}
    var refreshList =ArrayList<Songs>()
    var getListFromDataBase = ArrayList<Songs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        visibleLayout = view?.findViewById(R.id.visibleFavLayout)
        playPauseButton = view?.findViewById(R.id.playPauseButton)
        noSongs = view?.findViewById(R.id.noFavSongs)
        noSongsText = view?.findViewById(R.id.noFavSongText)
        songTitle = view?.findViewById(R.id.songTitleFavScreen)
        recyclerViewGroup = view?.findViewById(R.id.contentFav)
        songTitleBottomBar = view?.findViewById(R.id.songTitleFavScreen)
        bottomBar = view?.findViewById(R.id.hiddenBarFavScreen)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        var prefsForDark = myActivity?.getSharedPreferences(SettingFragment.Staticated.MY_PREFS_DARK, Context.MODE_PRIVATE)
        var isDark = prefsForDark?.getBoolean("DarkFeature", false)
            if (isDark as Boolean) {
                visibleLayout?.setBackgroundColor(Color.BLACK)
                noSongs?.setBackgroundColor(Color.BLACK)
               noSongsText?.setTextColor(Color.WHITE)
            }

        favoriteContent = EchoDataBase(myActivity)
            myActivity?.title = "Favorite Songs"
        bottomBar()
        displayFavouriteBySearching()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onPrepareOptionsMenu(menu: Menu?) {
        val item = menu?.findItem(R.id.sortIcon)
        item?.isVisible = false
        val item2 = menu?.findItem(R.id.search)
        item2?.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }
fun getAllSongs():ArrayList<Songs>
{
    var arrayList = ArrayList<Songs>()
    var contenResolver = myActivity?.contentResolver
    var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    var songCursor = contenResolver?.query(songUri,null,null,null,null)
    if(songCursor!=null && songCursor.moveToFirst())
    {
        val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
        val songName =songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
        val songArtist=songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
        val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
        val dateAdded = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
        arrayList.add(Songs(songCursor.getLong(songId),songCursor.getString(songName),songCursor.getString(songArtist),songCursor.getString(songData),songCursor.getLong(dateAdded)))

        while (songCursor.moveToNext())
        {
            val currentid = songCursor.getLong(songId)
            val currentname = songCursor.getString(songName)
            val currentArtist = songCursor.getString(songArtist)
            val currentData = songCursor.getString(songData)
            val curretDate = songCursor.getLong(dateAdded)
            arrayList.add(Songs(currentid,currentname,currentArtist,currentData,curretDate))
        }
    }

    return arrayList
}
    fun displayFavouriteBySearching()
    {
            if(favoriteContent?.checkSize() as Int > 0)
            {
                refreshList = ArrayList()
                getListFromDataBase = favoriteContent?.queryDBList()!!
                val fetchListFromPhone = getAllSongs()
                if(fetchListFromPhone!= null)
                {
                    for(i in 0 until fetchListFromPhone.size)
                    {
                        for(j in 0 until getListFromDataBase?.size)
                        {
                            if(getListFromDataBase?.get(j)?.songId == fetchListFromPhone[i].songId)
                            {
                                refreshList?.add((getListFromDataBase )[j])
                            }
                        }
                    }
                }
                if(refreshList == null)
                {
                    visibleLayout?.visibility = View.INVISIBLE
                    noSongs?.visibility = View.VISIBLE
                }
                else
                {
                    favoriteScreenAdapter = FavoriteScreenAdapter(refreshList as ArrayList<Songs>,myActivity as Context)
                    val layoutManager = LinearLayoutManager(myActivity)
                    recyclerViewGroup?.layoutManager = layoutManager
                    recyclerViewGroup?.itemAnimator = DefaultItemAnimator()
                    recyclerViewGroup?.adapter = favoriteScreenAdapter
                    recyclerViewGroup?.setHasFixedSize(true)
                }
            }
        else{
                visibleLayout?.visibility = View.INVISIBLE
                noSongs?.visibility = View.VISIBLE
            }
    }
    fun bottomBar()
    {
        try
        {
            bottomBarClickHandler()
            songTitleBottomBar?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener {
                songTitleBottomBar?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle
                SongPlayingFragment.Staticated.onCompletion()
            }
            if(SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean)
            {
                bottomBar?.visibility =View.VISIBLE
            }
            else
            {
                bottomBar?.visibility = View.INVISIBLE
            }
        }
        catch (e : Exception)
        {

        }
    }
    fun bottomBarClickHandler()
    {
        Staticated.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
        bottomBar?.setOnClickListener{
            var args = Bundle()
            val songPlayingFragment = SongPlayingFragment()
            args.putString("songArtist",SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("songTitle",SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putString("path",SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putInt("songId",SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition",SongPlayingFragment.Statified.currentPosition)
            args.putParcelableArrayList("songData",SongPlayingFragment.Statified.fetchSongs)
            args.putString("BottomBar","successFav")
            songPlayingFragment.arguments = args
            fragmentManager?.beginTransaction()?.replace(R.id.details_fragment,songPlayingFragment)
                ?.addToBackStack("Favorite Fragment")
                ?.commit()
        }
        playPauseButton?.setOnClickListener {
            if(SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean)
            {
                SongPlayingFragment.Statified.mediaPlayer!!.pause()
                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }
            else{
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaPlayer!!.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        }
    }

}
