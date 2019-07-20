package com.example.echo.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.echo.R
import com.example.echo.activities.MainActivity
import com.example.echo.fragments.AboutUsFragment
import com.example.echo.fragments.FavoriteFragment
import com.example.echo.fragments.MainScreenFragment
import com.example.echo.fragments.SettingFragment

class NavigationDrawerAdapter(_contentList: ArrayList<String>, _getImages: IntArray, _context: Context) :
    RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>() {
    var contentList: ArrayList<String>? = null
    var getImages: IntArray? = null
    var mcontext: Context? = null

    init {
        this.contentList = _contentList
        this.getImages = _getImages
        this.mcontext = _context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
            .inflate(R.layout.row_custom_navigation_drawer, parent, false)
        return NavViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return (contentList as ArrayList).size
    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder?.icon_GET?.setBackgroundResource(getImages?.get(position) as Int)
        holder?.text_GET?.text = contentList?.get(position)
        var prefsForDark =
            (mcontext as Activity)?.getSharedPreferences(SettingFragment.Staticated.MY_PREFS_DARK, Context.MODE_PRIVATE)
        var isDark = prefsForDark?.getBoolean("DarkFeature", false)
        if (isDark as Boolean) {
            holder.holder_GET?.setBackgroundColor(Color.BLACK)
            holder.text_GET?.setTextColor(Color.WHITE)
        }
        holder?.holder_GET?.setOnClickListener {
            when (position) {
                0 -> {
                    val mainScreenFragment = MainScreenFragment()
                    (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction().replace(R.id.details_fragment, mainScreenFragment).commit()
                }
                1 -> {
                    val favoriteFragment = FavoriteFragment()
                    (mcontext as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.details_fragment, favoriteFragment).commit()
                }
                2 -> {
                    val settingFragment = SettingFragment()
                    (mcontext as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.details_fragment, settingFragment).commit()
                }
                else -> {
                    val aboutUsFragment = AboutUsFragment()
                    (mcontext as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.details_fragment, aboutUsFragment).commit()
                }
            }
            MainActivity.Statified.drawerLayout?.closeDrawers()
        }

    }

    class NavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon_GET: ImageView? = null
        var text_GET: TextView? = null
        var holder_GET: RelativeLayout? = null

        init {
            this.icon_GET = itemView?.findViewById(R.id.icon_navigation)
            this.text_GET = itemView?.findViewById(R.id.text_navigation)
            this.holder_GET = itemView?.findViewById(R.id.navigation_item_holder)
        }

    }

}