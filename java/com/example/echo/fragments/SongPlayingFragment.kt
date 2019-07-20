package com.example.echo.fragments

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.*
import android.widget.*
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.echo.CurrentSongHelper
import com.example.echo.EchoDataBase
import com.example.echo.R
import com.example.echo.Songs
import com.example.echo.fragments.SongPlayingFragment.Staticated.onCompletion
import com.example.echo.fragments.SongPlayingFragment.Staticated.playNext
import com.example.echo.fragments.SongPlayingFragment.Staticated.playPre
import com.example.echo.fragments.SongPlayingFragment.Staticated.processInfo
import com.example.echo.fragments.SongPlayingFragment.Staticated.updateTextView
import com.example.echo.fragments.SongPlayingFragment.Statified.audioVisualization
import com.example.echo.fragments.SongPlayingFragment.Statified.currentPosition
import com.example.echo.fragments.SongPlayingFragment.Statified.currentSongHelper
import com.example.echo.fragments.SongPlayingFragment.Statified.endTimeText
import com.example.echo.fragments.SongPlayingFragment.Statified.fab
import com.example.echo.fragments.SongPlayingFragment.Statified.favoriteContent
import com.example.echo.fragments.SongPlayingFragment.Statified.fetchSongs
import com.example.echo.fragments.SongPlayingFragment.Statified.glView
import com.example.echo.fragments.SongPlayingFragment.Statified.loopButton
import com.example.echo.fragments.SongPlayingFragment.Statified.mSensorListener
import com.example.echo.fragments.SongPlayingFragment.Statified.mSensorManager
import com.example.echo.fragments.SongPlayingFragment.Statified.mediaPlayer
import com.example.echo.fragments.SongPlayingFragment.Statified.myActivity
import com.example.echo.fragments.SongPlayingFragment.Statified.nextButton
import com.example.echo.fragments.SongPlayingFragment.Statified.playPauseButton
import com.example.echo.fragments.SongPlayingFragment.Statified.prevButton
import com.example.echo.fragments.SongPlayingFragment.Statified.seekBar
import com.example.echo.fragments.SongPlayingFragment.Statified.shuffleButton
import com.example.echo.fragments.SongPlayingFragment.Statified.songArtistText
import com.example.echo.fragments.SongPlayingFragment.Statified.songPic
import com.example.echo.fragments.SongPlayingFragment.Statified.songTitleText
import com.example.echo.fragments.SongPlayingFragment.Statified.startTimeText
import com.example.echo.fragments.SongPlayingFragment.Statified.updateSongTime
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt
import kotlin.random.Random

class SongPlayingFragment : Fragment() {

    object Statified {
        var myActivity: Activity? = null
        var playPauseButton: ImageButton? = null
        var prevButton: ImageButton? = null
        var nextButton: ImageButton? = null
        var loopButton: ImageButton? = null
        var fab: ImageButton? = null
        var songPic: ImageView? = null
        var shuffleButton: ImageButton? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var songTitleText: TextView? = null
        var songArtistText: TextView? = null
        var seekBar: SeekBar? = null
        var currentSongHelper: CurrentSongHelper? = null
        var mediaPlayer: MediaPlayer? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null
        var audioVisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var mSensorManager: SensorManager? = null
        var mSensorListener: SensorEventListener? = null
        var favoriteContent: EchoDataBase? = null
        var updateSongTime = object : Runnable {
            override fun run() {
                val getCurrent = mediaPlayer?.currentPosition
                startTimeText?.text = String.format(
                    "%d:%d", TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(getCurrent.toLong()) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            getCurrent.toLong()
                        )
                    )
                )
                seekBar?.progress = getCurrent
                Handler().postDelayed(this, 1000)
            }
        }
    }
    object Staticated {
        var MY_PREFS_SHUFFLE = "Shuffle Feature"
        var MY_PREFS_LOOP = "Loop Feature"

        fun playPre() {
            currentPosition -= 1
            if (currentPosition == -1) {
                currentPosition = 0
            }

            currentSongHelper?.isLoop = false
            var nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.songPath = nextSong?.songData
            currentSongHelper?.songArtist = nextSong?.artist
            currentSongHelper?.songTitle = nextSong?.songName
            currentSongHelper?.songId = nextSong?.songId
            currentSongHelper?.trackPosition = currentPosition
            updateTextView(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
            mediaPlayer?.reset()
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(currentSongHelper?.songPath)
            val image: ByteArray? = mmr.embeddedPicture
            if (image != null) {
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                songPic?.setImageBitmap(bitmap)
            } else {
                songPic?.setImageResource(R.drawable.url)
            }

            try {

                mediaPlayer?.setDataSource((myActivity as Context), Uri.parse(currentSongHelper?.songPath))
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                processInfo(mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (currentSongHelper?.isPlaying as Boolean) {
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }
            if (favoriteContent?.checkIdExists(currentSongHelper?.songId?.toInt()) as Boolean) {
                fab?.setImageResource(R.drawable.favorite_on)
            } else {
                fab?.setImageResource(R.drawable.favorite_off)
            }

        }

        fun playNext(check: String) {
            if (check.equals("PlayNextNormal", true)) {
                currentPosition += 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                val rand = Random.nextInt(fetchSongs?.size?.plus(1) as Int)
                currentPosition = rand
            }
            if (currentPosition == fetchSongs?.size)
                currentPosition = 0
            currentSongHelper?.isLoop = false

            var nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.songPath = nextSong?.songData
            currentSongHelper?.songArtist = nextSong?.artist
            currentSongHelper?.songTitle = nextSong?.songName
            currentSongHelper?.songId = nextSong?.songId
            currentSongHelper?.trackPosition = currentPosition
            currentSongHelper?.isPlaying = true
            updateTextView(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
            mediaPlayer?.reset()
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(currentSongHelper?.songPath)
            val image: ByteArray? = mmr.embeddedPicture
            if (image != null) {
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                songPic?.setImageBitmap(bitmap)
            } else {
                songPic?.setImageResource(R.drawable.url)
            }
            try {
                mediaPlayer?.setDataSource((myActivity as Context), Uri.parse(currentSongHelper?.songPath))
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                processInfo(mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (currentSongHelper?.isPlaying as Boolean) {
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }
            if (favoriteContent?.checkIdExists(currentSongHelper?.songId?.toInt())!!) {
                fab?.setImageResource(R.drawable.favorite_on)
            } else {
                fab?.setImageResource(R.drawable.favorite_off)
            }
        }

        fun processInfo(mediaPlayer: MediaPlayer) {
            var startTime = mediaPlayer.currentPosition
            var endTime = mediaPlayer.duration
            seekBar?.max = endTime

            startTimeText?.text = String.format(
                "%d:%d", TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()))
            )
            endTimeText?.text = String.format(
                "%d:%d", TimeUnit.MILLISECONDS.toMinutes(endTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(endTime.toLong()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(endTime.toLong()))
            )
            seekBar?.progress = startTime
            Handler().postDelayed(updateSongTime, 1000)
        }

        fun onCompletion() {
            if (currentSongHelper?.isSuffle as Boolean) {
                currentSongHelper?.isPlaying = true
                playNext("PlayNextLikeNormalShuffle")
            } else {
                if (currentSongHelper?.isLoop as Boolean) {
                    currentSongHelper?.isPlaying = true
                    var nextSong = fetchSongs?.get(currentPosition)
                    currentSongHelper?.songPath = nextSong?.songData
                    currentSongHelper?.songArtist = nextSong?.artist
                    currentSongHelper?.songTitle = nextSong?.songName
                    currentSongHelper?.songId = nextSong?.songId
                    currentSongHelper?.trackPosition = currentPosition
                    updateTextView(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
                    mediaPlayer?.reset()
                    val mmr = MediaMetadataRetriever()
                    mmr.setDataSource(currentSongHelper?.songPath)
                    val image: ByteArray? = mmr.embeddedPicture
                    if (image != null) {
                        val bitmap: Bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                        songPic?.setImageBitmap(bitmap)
                    } else {
                        songPic?.setImageResource(R.drawable.url)
                    }
                    try {
                        mediaPlayer?.setDataSource((myActivity as Context), Uri.parse(currentSongHelper?.songPath))
                        mediaPlayer?.prepare()
                        mediaPlayer?.start()
                        processInfo(mediaPlayer as MediaPlayer)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    currentSongHelper?.isPlaying = true
                    playNext("PlayNextNormal")
                }
            }
        }

        fun updateTextView(songName: String, songArtist: String) {
            var updateSongTitle = songName
            var updateSongArtist = songArtist
            if (updateSongTitle == "<unknown>") {
                updateSongTitle = "unknown"
            }
            if (updateSongArtist == "<unknown>") {
                updateSongArtist = "unknown"
            }
            songTitleText?.text = updateSongTitle
            songArtistText?.text = updateSongArtist
        }
    }

    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSensorManager = myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        mAcceleration = 0f
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }

    fun bindShakeListener() {
        Statified.mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent) {

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                mAccelerationLast = mAccelerationCurrent
                mAccelerationCurrent = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                val delta = mAccelerationCurrent - mAccelerationLast
                mAcceleration = mAcceleration * 0.9f + delta
                if (mAcceleration > 12) {
                    var prefsForShake = myActivity?.getSharedPreferences(
                        SettingFragment.Staticated.MY_PREFS_SHAKE,
                        Context.MODE_PRIVATE
                    )
                    var isShake = prefsForShake?.getBoolean("ShakeFeature", false)
                    if (isShake as Boolean) {
                        playNext("PlayNextNormal")
                    }
                }
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_song_playing, container, false).also {
            playPauseButton = it.findViewById(R.id.playPauseButton)
            nextButton = it.findViewById(R.id.nextButton)
            prevButton = it.findViewById(R.id.previousButton)
            loopButton = it.findViewById(R.id.loopButton)
            shuffleButton = it.findViewById(R.id.shuffleButton)
            fab = it.findViewById(R.id.favouriteIcon)
            songPic = it.findViewById<ImageView>(R.id.musicPic)
            seekBar = it.findViewById(R.id.seekBar)
            songArtistText = it.findViewById(R.id.songArtist)
            songTitleText = it.findViewById(R.id.songTitle)
            startTimeText = it.findViewById(R.id.startTime)
            endTimeText = it.findViewById(R.id.endTime)
            glView = it.findViewById(R.id.visualizer_view)
            setHasOptionsMenu(true)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            super.onViewCreated(view, savedInstanceState)
            audioVisualization = glView as AudioVisualization
        } catch (e: Exception) {

        }
    }

    override fun onResume() {
        super.onResume()
        try {
            audioVisualization?.onResume()
        } catch (e: Exception) {

        }
        mSensorManager?.registerListener(
            mSensorListener,
            mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        try {
            audioVisualization?.onPause()
        } catch (e: Exception) {

        }
        mSensorManager?.unregisterListener(mSensorListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            audioVisualization?.release()
        } catch (e: Exception) {

        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.backToList)
        item?.isVisible = true

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.backToList -> {
                myActivity?.onBackPressed()
                return false
            }

        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favoriteContent = EchoDataBase(myActivity)
        myActivity?.title = "Now Playing"
        currentSongHelper = CurrentSongHelper()
        currentSongHelper?.isPlaying = true
        currentSongHelper?.isLoop = false
        currentSongHelper?.isSuffle = false
        var _path: String? = null
        var _songTitle: String?
        var _songArtist: String?
        var _songId: Long

        var bottomClick: String? = null
        try {
            _path = arguments?.getString("path")
            _songTitle = arguments?.getString("songTitle")
            _songArtist = arguments?.getString("songArtist")
            _songId = arguments?.getInt("songId")?.toLong()!!
            currentPosition = arguments?.getInt("songPosition")!!
            fetchSongs = arguments?.getParcelableArrayList("songData")
            bottomClick = arguments?.getString("BottomBar")
            currentSongHelper?.songPath = _path
            currentSongHelper?.songArtist = _songArtist
            currentSongHelper?.songTitle = _songTitle
            currentSongHelper?.songId = _songId
            currentSongHelper?.trackPosition = currentPosition
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(_path)
            val image: ByteArray? = mmr.embeddedPicture
            if (image != null) {
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                songPic?.setImageBitmap(bitmap)
            } else {
                songPic?.setImageResource(R.drawable.url)
            }

        } catch (e: Exception) {

        }
        if (bottomClick != null) {
            if (bottomClick == "successMain") {
                mediaPlayer = MainScreenFragment.Statified.mediaPlayer

            } else if (bottomClick == "successFav") {
                mediaPlayer = FavoriteFragment.Staticated.mediaPlayer
            }
            if (mediaPlayer?.isPlaying as Boolean) {
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }
        } else {
            try {
                if (mediaPlayer?.isPlaying as Boolean) {
                    mediaPlayer?.stop()
                }
            } catch (e: Exception) {
            }
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                mediaPlayer?.setDataSource((myActivity as Context), Uri.parse(_path))
                mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            mediaPlayer?.start()
            if (currentSongHelper?.isPlaying as Boolean) {
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }
        }
        updateTextView(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
        processInfo(mediaPlayer as MediaPlayer)
        clickHandler()
        var prefsForShuffle = myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
        var prefsForLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)
        var isShuffled = prefsForShuffle?.getBoolean("feature", false)
        var isLooped = prefsForLoop?.getBoolean("feature", false)
        if (isShuffled as Boolean) {
            currentSongHelper?.isLoop = false
            currentSongHelper?.isSuffle = true
            shuffleButton?.setBackgroundResource(R.drawable.shuffle_icon)
            loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            currentSongHelper?.isSuffle = false
            shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        if (isLooped as Boolean) {
            currentSongHelper?.isLoop = true
            currentSongHelper?.isSuffle = false
            shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            currentSongHelper?.isLoop = false
            loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        audioVisualization?.linkTo(DbmHandler.Factory.newVisualizerHandler(myActivity as Context, 0))

        if (favoriteContent?.checkIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean) {
            fab?.setImageResource(R.drawable.favorite_on)
        } else {
            fab?.setImageResource(R.drawable.favorite_off)
        }

        mediaPlayer?.setOnCompletionListener {
            onCompletion()
        }
    }


    private fun clickHandler() {
        currentSongHelper?.isPlaying = true

        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    mediaPlayer?.seekTo(progress)
            }

        })
        fab?.setOnClickListener {
            if (favoriteContent?.checkIdExists(currentSongHelper?.songId?.toInt()) as Boolean) {
                fab?.setImageResource(R.drawable.favorite_off)
                favoriteContent?.deleteFav(currentSongHelper?.songId?.toInt())
                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
            } else {
                fab?.setImageResource(R.drawable.favorite_on)
                favoriteContent?.storeAsFavorite(
                    currentSongHelper?.songId?.toInt(),
                    currentSongHelper?.songArtist,
                    currentSongHelper?.songTitle,
                    currentSongHelper?.songPath
                )
                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
            }

        }

        playPauseButton?.setOnClickListener {
            if (mediaPlayer?.isPlaying as Boolean) {
                mediaPlayer?.pause()
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                currentSongHelper?.isPlaying = false
            } else {
                mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
                currentSongHelper?.isPlaying = true
            }
        }
        nextButton?.setOnClickListener {
            currentSongHelper?.isPlaying = true
            if (currentSongHelper?.isSuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
            } else {
                playNext("PlayNextNormal")
            }
        }
        prevButton?.setOnClickListener {
            currentSongHelper?.isPlaying = true
            if (currentSongHelper?.isLoop as Boolean) {
                currentSongHelper?.isLoop = false
                loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPre()
        }
        shuffleButton?.setOnClickListener {
            var editorShuffle =
                myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
            if (currentSongHelper?.isSuffle as Boolean) {
                currentSongHelper?.isSuffle = false
                shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else {
                currentSongHelper?.isSuffle = true
                currentSongHelper?.isLoop = false
                loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
                shuffleButton?.setBackgroundResource(R.drawable.shuffle_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        }
        loopButton?.setOnClickListener {
            var editorShuffle =
                myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
            if (currentSongHelper?.isLoop as Boolean) {
                currentSongHelper?.isLoop = false
                loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
                currentSongHelper?.isLoop = true
                currentSongHelper?.isSuffle = false
                loopButton?.setBackgroundResource(R.drawable.loop_icon)
                shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            }
        }
    }
}


