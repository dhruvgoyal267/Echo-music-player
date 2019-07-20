package com.example.echo.fragments


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import com.example.echo.R
import com.example.echo.activities.MainActivity
import com.example.echo.fragments.SettingFragment.Staticated.myActivity

class SettingFragment : Fragment() {

    var shakeSwitch: Switch? = null
    var shakeText: TextView? = null
    var darkThemeSwitch: Switch? = null
    var darkThemeText: TextView? = null
    var setting: RelativeLayout? = null

    object Staticated {
        var myActivity: Activity? = null
        val MY_PREFS_SHAKE = "ShakeFeature"
        val MY_PREFS_DARK = "DarkFeature"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        shakeSwitch = view?.findViewById(R.id.shakeSwitch)
        darkThemeSwitch = view?.findViewById(R.id.darkThemeSwitch)
        setting = view?.findViewById(R.id.settingLayout)
        shakeText = view?.findViewById(R.id.shakeFeatureText)
        darkThemeText = view?.findViewById(R.id.darkThemeText)
        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        var prefsForShake = myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHAKE, Context.MODE_PRIVATE)
        var prefsForDark = myActivity?.getSharedPreferences(Staticated.MY_PREFS_DARK, Context.MODE_PRIVATE)
        var isShake = prefsForShake?.getBoolean("ShakeFeature", false)
        var isDark = prefsForDark?.getBoolean("DarkFeature", false)
        shakeSwitch?.isChecked = isShake as Boolean
        if (isDark as Boolean) {
            darkThemeSwitch?.isChecked = true
            setting?.setBackgroundColor(Color.BLACK)
            shakeText?.setTextColor(Color.WHITE)
            darkThemeText?.setTextColor(Color.WHITE)
        } else {
            darkThemeSwitch?.isChecked = false
            setting?.setBackgroundColor(Color.WHITE)
            shakeText?.setTextColor(Color.BLACK)
            darkThemeText?.setTextColor(Color.BLACK)
        }
        shakeSwitch?.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                var editor =
                    myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHAKE, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("ShakeFeature", true)
                editor?.apply()
            } else {
                var editor =
                    myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHAKE, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("ShakeFeature", false)
                editor?.apply()
            }
        }
        darkThemeSwitch?.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                var editor =
                    myActivity?.getSharedPreferences(Staticated.MY_PREFS_DARK, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("DarkFeature", true)
                editor?.apply()
                val dialog = AlertDialog.Builder(myActivity)
                dialog.setMessage("App must be restarted to apply theme properly ")
                    .setTitle("Dark Theme")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Restart"

                    ) { dialogInterface: DialogInterface, i: Int ->

                        val intent = Intent(myActivity, MainActivity::class.java)
                        myActivity?.finish()
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.cancel()
                        setting?.setBackgroundColor(Color.BLACK)
                        shakeText?.setTextColor(Color.WHITE)
                        darkThemeText?.setTextColor(Color.WHITE)
                    }
                val alert: AlertDialog = dialog.create()
                alert.show()

            } else {

                var editor =
                    myActivity?.getSharedPreferences(Staticated.MY_PREFS_DARK, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("DarkFeature", false)
                editor?.apply()
                val dialog = AlertDialog.Builder(myActivity)
                dialog.setMessage("App must be restarted to apply theme properly ")
                    .setTitle("Light Theme")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Restart"

                    ) { dialogInterface: DialogInterface, i: Int ->

                        val intent = Intent(myActivity, MainActivity::class.java)
                        myActivity?.finish()
                        startActivity(intent)

                    }
                    .setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.cancel()
                        setting?.setBackgroundColor(Color.WHITE)
                        shakeText?.setTextColor(Color.BLACK)
                        darkThemeText?.setTextColor(Color.BLACK)
                    }
                val alert: AlertDialog = dialog.create()
                alert.show()
            }
        }
        myActivity?.title = "Setting"
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        val item = menu?.findItem(R.id.sortIcon)
        item?.isVisible = false
        val item2 = menu?.findItem(R.id.search)
        item2?.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }
}
