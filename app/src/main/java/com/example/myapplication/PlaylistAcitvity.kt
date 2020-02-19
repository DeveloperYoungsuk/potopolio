package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.facebook.stetho.Stetho
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.android.synthetic.main.custom_music_player.*


/**
 * SQlite 학습을 위한 주석 (구글 sqlite 안드로이드 사용법 링크 https://developer.android.com/training/data-storage/sqlite#kotlin)
 * 테이블을 없애는 쿼리문 db.execSQL("DROP TABLE playlist")
 *
 */



class PlaylistAcitvity : AppCompatActivity() {

    private lateinit var musicName: String
    private lateinit var singerName: String
    private lateinit var albumImageURL: String
    private lateinit var lylics: String
    private lateinit var musicPath: String
    private lateinit var musicURL: String
    private lateinit var context: Context
    private lateinit var defaultHttpDataSourceFactory : DefaultHttpDataSourceFactory
    private var player: SimpleExoPlayer? = null

    private var playbackPosition = 0L
    private var currentWindow = 0
    private var playWhenReady = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        var mPosition: Int = 0

        context = applicationContext

        //Stetho 사용 하기 위해 필요한 코드
        Stetho.initializeWithDefaults(this)


        val dbHelper = FeedReaderDbHelper(applicationContext)

        //데이터를 작성하려면 dbHelper.writableDatabase를 사용해야 한다.
        val db = dbHelper.writableDatabase

        //////////////////////////////더미데이터 작성용 INSERT 코드///////////////////////////
        // 디비에 INSERT할 값들을 정해준다.
        val values = ContentValues().apply {
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_MUSIC_NAME, "METEOR")
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_MUSIC_URL, "http://115.68.216.113/changmo.mp3")
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_SINGER_NAME, "창모")
            put(
                FeedReaderContract.FeedEntry.COLUMN_NAME_ALBUM_IMAGE_URL,
                "http://115.68.216.113/meteor_album.jpg"
            )
        }

        // Insert the new row, returning the primary key value of the new row
//        val d = db?.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values)

        //////////////////////////////더미데이터 작성용 INSERT 코드///////////////////////////


        //데이터를 읽어오려면 dbHelper.readableDatabase를 사용해야 한다.
        val readDB = dbHelper.readableDatabase


        //SELECT 쿼리문을 보낸다.
        val cursor = readDB.rawQuery("SELECT * FROM playlist", null)

        val list = ArrayList<playlistItem>()


        with(cursor) {
            while (moveToNext()) {
                musicName = getString(getColumnIndex("musicName"))
                singerName = getString(getColumnIndex("singerName"))
                musicURL = getString(getColumnIndex("musicURL"))
                albumImageURL = getString(getColumnIndex("albumImageURL"))

                list.add(
                    playlistItem(
                        musicName = musicName,
                        singerName = singerName,
                        albumImageURL = albumImageURL,
                        musicURL = musicURL
                    )
                )
            }
        }
        val adapter = PlaylistAdapter(list, context)
        recyclerView.adapter = adapter

        adapter.itemClick = object: PlaylistAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                musicPath = list.get(position).musicURL
                mPosition = position

                custom_music_controller_tv_song.setText(list.get(position).musicName)
                custom_music_controller_tv_name.setText(list.get(position).singerName)
                val mediaSource = ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory)
                    .createMediaSource(Uri.parse(musicPath))
                player!!.prepare(mediaSource)
                player!!.seekTo(currentWindow, playbackPosition)
                player!!.playWhenReady = playWhenReady
            }
        }

        exo_ffwd.setOnClickListener { view ->
            Log.i("asdf", mPosition.toString())
            mPosition = mPosition +1
            custom_music_controller_tv_song.setText(list.get(mPosition).musicName)
            custom_music_controller_tv_name.setText(list.get(mPosition).singerName)

            val mediaSource = ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory)
                .createMediaSource(Uri.parse(list.get(mPosition).musicURL))
            player!!.prepare(mediaSource)
            player!!.seekTo(currentWindow, playbackPosition)
            player!!.playWhenReady = playWhenReady
        }

        exo_rew.setOnClickListener { view ->

            Log.i("asdf", mPosition.toString())
            mPosition = mPosition + -1
            custom_music_controller_tv_song.setText(list.get(mPosition).musicName)
            custom_music_controller_tv_name.setText(list.get(mPosition).singerName)

            val mediaSource = ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory)
                .createMediaSource(Uri.parse(list.get(mPosition).musicURL))
            player!!.prepare(mediaSource)
            player!!.seekTo(currentWindow, playbackPosition)
            player!!.playWhenReady = playWhenReady
        }

        exoplayer.setOnClickListener { view ->
            val intent = Intent(applicationContext, MusicDetailActivity::class.java)
            startActivity(intent)
        }

        initializePlayer()
        main_pcv.showTimeoutMs = 0

    }

    override fun onResume() {
        super.onResume()
        initializePlayer()
    }

    override fun onRestart() {
        super.onRestart()
        initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this)
            main_pcv.player = player
            defaultHttpDataSourceFactory =
                DefaultHttpDataSourceFactory(getString(R.string.app_name))

        }

    }

    private fun releasePlayer() {
        player?.let {
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            playWhenReady = it.playWhenReady
            it.release()
            player = null
        }
    }

}
