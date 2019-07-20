package com.example.echo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.echo.EchoDataBase.Staticated.Colomn_Id
import com.example.echo.EchoDataBase.Staticated.Colomn_song_artist
import com.example.echo.EchoDataBase.Staticated.Colomn_song_path
import com.example.echo.EchoDataBase.Staticated.Colomn_song_title
import com.example.echo.EchoDataBase.Staticated.DB_NAME
import com.example.echo.EchoDataBase.Staticated.DB_VERSION
import com.example.echo.EchoDataBase.Staticated.TABLE_NAME

class EchoDataBase : SQLiteOpenHelper {

    var songsList = ArrayList<Songs>()

    object Staticated {
        var DB_NAME = "Favourite DataBase"
        var TABLE_NAME = "FavTable"
        var Colomn_Id = "SongId"
        var Colomn_song_title = "SongTitle"
        var Colomn_song_artist = "SongArtist"
        var Colomn_song_path = "SOngPath"
        var DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE " + TABLE_NAME + "( " + Colomn_Id + " INTEGER," +
                    Colomn_song_title + " STRING," + Colomn_song_artist + " STRING," + Colomn_song_path + " STRING);"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(
        context, name, factory, version
    )

    constructor(context: Context?) : super(
        context, DB_NAME, null, DB_VERSION
    )

    fun storeAsFavorite(id: Int?, artist: String?, songTitle: String?, songPath: String?) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(Colomn_Id, id)
        contentValues.put(Colomn_song_artist, artist)
        contentValues.put(Colomn_song_title, songTitle)
        contentValues.put(Colomn_song_path, songPath)
        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    fun deleteFav(id: Int?) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$Colomn_Id = $id", null)
        db.close()
    }
    fun checkIdExists(id: Int?): Boolean {
        var storeid = -1900
        val db = this.readableDatabase
        val query = "SELECT * FROM " + TABLE_NAME + " WHERE SongId='$id'"
        val cSor = db.rawQuery(query, null)
        if (cSor.moveToFirst()) {
            do {
                storeid = cSor.getInt(cSor.getColumnIndexOrThrow(Colomn_Id))
            } while (cSor.moveToNext())
        } else
            return false
        return storeid != -1900
    }

    fun queryDBList(): ArrayList<Songs>? {
        try {
            val db = this.readableDatabase
            val query = "SELECT * FROM $TABLE_NAME"
            val cSor = db.rawQuery(query, null)
            if (cSor.moveToFirst()) {
                do {
                    var id = cSor.getInt(cSor.getColumnIndexOrThrow(Colomn_Id))
                    var title = cSor.getString(cSor.getColumnIndexOrThrow(Colomn_song_title))
                    var artist = cSor.getString(cSor.getColumnIndexOrThrow(Colomn_song_artist))
                    var path = cSor.getString(cSor.getColumnIndexOrThrow(Colomn_song_path))
                    songsList?.add(Songs(id.toLong(), title, artist, path, 0))
                } while (cSor.moveToNext())
            } else {
                return null
            }
        } catch (e: Exception) {

        }
        return songsList
    }

    fun checkSize(): Int {
        var count = 0
        var db = this.readableDatabase
        var query = "SELECT * FROM $TABLE_NAME"
        var cSor = db.rawQuery(query, null)
        if (cSor.moveToFirst()) {
            do {
                count += 1
            } while (cSor.moveToNext())
        } else {
            return 0
        }
        return count
    }
}