package com.example.echo.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.widget.RelativeLayout
import com.example.echo.R
import com.example.echo.activities.MainActivity.Statified.notificationManager
import com.example.echo.adapters.NavigationDrawerAdapter
import com.example.echo.fragments.MainScreenFragment
import com.example.echo.fragments.SettingFragment
import com.example.echo.fragments.SongPlayingFragment

class MainActivity : AppCompatActivity() {

    private val navigationDrawerIconsList: ArrayList<String> = arrayListOf()
    private val icons_for_navDrawer = intArrayOf(
        R.drawable.navigation_allsongs,
        R.drawable.navigation_favorites
        , R.drawable.navigation_settings
        , R.drawable.navigation_aboutus
    )

    object Statified {
        var drawerLayout: DrawerLayout? = null
        var notificationManager: NotificationManagerCompat? = null
    }

    var builder: NotificationCompat.Builder? = null
    var navView: RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Statified.drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navDrawer)
        var prefsForDark = this?.getSharedPreferences(SettingFragment.Staticated.MY_PREFS_DARK, Context.MODE_PRIVATE)
        var isDark = prefsForDark?.getBoolean("DarkFeature", false)
        if (isDark as Boolean) {

            navView?.setBackgroundColor(Color.BLACK)
        }
        navigationDrawerIconsList.add("All Songs")
        navigationDrawerIconsList.add("Favorites")
        navigationDrawerIconsList.add("Setting")
        navigationDrawerIconsList.add("About Us")
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, Statified.drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        Statified.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        val mainScreen = MainScreenFragment()
        this.supportFragmentManager.beginTransaction().add(R.id.details_fragment, mainScreen, "MainScreenFragment")
            .commit()

        var nav_recycle = findViewById<RecyclerView>(R.id.navigation)
        nav_recycle.layoutManager = LinearLayoutManager(this)
        nav_recycle.itemAnimator = DefaultItemAnimator()


        var _navigationAdapter = NavigationDrawerAdapter(navigationDrawerIconsList, icons_for_navDrawer, this)
        _navigationAdapter.notifyDataSetChanged()

        nav_recycle.adapter = _navigationAdapter
        nav_recycle.setHasFixedSize(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("EchoNotification", "Echo", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val intent = Intent(this,MainActivity::class.java)
        val pintent = PendingIntent.getActivity(this,System.currentTimeMillis().toInt(),intent,0)
        builder = NotificationCompat.Builder(this, "EchoNotification")
            .setSmallIcon(R.drawable.echo_icon)
            .setContentTitle("Echo Player")
            .setAutoCancel(true)
            .setContentIntent(pintent)
            .setContentText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager = NotificationManagerCompat.from(this)

    }

    override fun onStart() {
        super.onStart()
        try {
            notificationManager?.cancel(1978)
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            notificationManager?.cancel(1978)
        } catch (e: Exception) {
        }
    }

    override fun onStop() {
        super.onStop()
        if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
            notificationManager?.notify(1978, builder!!.build())
        }
    }
}
