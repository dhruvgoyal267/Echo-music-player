package com.example.echo.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.echo.R

class SplashActivity : AppCompatActivity() {
    var permissionString = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE
        , android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.PROCESS_OUTGOING_CALLS,
        android.Manifest.permission.RECORD_AUDIO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (!hasPermissions(this@SplashActivity, *permissionString)) {
            ActivityCompat.requestPermissions(this@SplashActivity, permissionString, 131)
        } else {
            startNew()
        }
    }

    fun startNew() {
        Handler().postDelayed(
            {
                var startIntent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(startIntent)
                this.finish()
            }, 1000
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            131 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                ) {
                    startNew()
                } else {
                    Toast.makeText(this@SplashActivity, "Please Grant All Permissions to continue", Toast.LENGTH_SHORT)
                        .show()
                    this.finish()
                }
            }
            else -> {
                Toast.makeText(this@SplashActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                this.finish()
            }
        }
    }

    fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        var hasAllPermission = true
        for (permission in permissions) {
            val res = context.checkCallingOrSelfPermission(permission)
            if (res != PackageManager.PERMISSION_GRANTED)
                hasAllPermission = false
        }
        return hasAllPermission
    }
}
