package com.example.myapplication.practice

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class musicActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)


        val mediaPlayer: MediaPlayer? = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource("http://115.68.216.113/changmo.mp3")
            prepare()
            start()
        }

        findViewById<View>(R.id.button2).setOnClickListener {
            if (mediaPlayer != null) {
                mediaPlayer.start()
            }
        }

    }
}
