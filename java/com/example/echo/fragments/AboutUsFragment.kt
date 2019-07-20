package com.example.echo.fragments


import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.echo.R


class AboutUsFragment : Fragment() {
    var aboutUsContainer: RelativeLayout? = null
    var pic: ImageView? = null
    var nameText: TextView? = null
    var appversionText: TextView? = null
    var details: TextView? = null
    var myActivity: Activity? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about_us, container, false)
        aboutUsContainer = view?.findViewById(R.id.aboutScreen)
        pic = view?.findViewById(R.id.pic)
        nameText = view?.findViewById(R.id.name)
        appversionText = view?.findViewById(R.id.appVersionText)
        details = view?.findViewById(R.id.description)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        var prefsForDark =
            myActivity?.getSharedPreferences(SettingFragment.Staticated.MY_PREFS_DARK, Context.MODE_PRIVATE)
        var isDark = prefsForDark?.getBoolean("DarkFeature", false)
        if (isDark as Boolean) {
            aboutUsContainer?.setBackgroundColor(Color.BLACK)
            nameText?.setTextColor(Color.WHITE)
            appversionText?.setTextColor(Color.GRAY)
            details?.setTextColor(Color.WHITE)
        }
        super.onActivityCreated(savedInstanceState)
        myActivity?.title = "About Us"
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        val item = menu?.findItem(R.id.sortIcon)
        item?.isVisible = false
        val item2 = menu?.findItem(R.id.search)
        item2?.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }
}
