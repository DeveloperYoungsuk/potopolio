package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns


private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${FeedReaderContract.FeedEntry.TABLE_NAME} (" +
            "${FeedReaderContract.FeedEntry.COLUMN_NAME_MUSIC_NAME} TEXT,"+
            "${FeedReaderContract.FeedEntry.COLUMN_NAME_MUSIC_URL} TEXT,"+
            "${FeedReaderContract.FeedEntry.COLUMN_NAME_SINGER_NAME} TEXT,"+
            "${FeedReaderContract.FeedEntry.COLUMN_NAME_ALBUM_IMAGE_URL} TEXT,"+
            "${FeedReaderContract.FeedEntry.COLUMN_NAME_LYLICS} TEXT,"+
            "${FeedReaderContract.FeedEntry.COLUMN_NAME_LIKE_NUMBER} TEXT)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FeedReaderContract.FeedEntry.TABLE_NAME}"

class FeedReaderDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 9
        const val DATABASE_NAME = "MUSIC"
    }
}

object FeedReaderContract {
    // Table contents are grouped together in an anonymous object.
    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "playlist"
        const val COLUMN_NAME_MUSIC_NAME = "musicName"
        const val COLUMN_NAME_MUSIC_URL = "musicURL"
        const val COLUMN_NAME_SINGER_NAME = "singerName"
        const val COLUMN_NAME_ALBUM_IMAGE_URL = "albumImageURL"
        const val COLUMN_NAME_LYLICS = "lylics"
        const val COLUMN_NAME_LIKE_NUMBER = "likeNumber"
    }
}
