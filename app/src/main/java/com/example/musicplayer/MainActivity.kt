package com.example.musicplayer

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat




class MainActivity : AppCompatActivity() {
    private lateinit var playBtn: ImageButton
    private lateinit var stopBtn: ImageButton
    private lateinit var nextBtn: ImageButton
    private lateinit var previousBtn: ImageButton

    private lateinit var musicService: MusicPlayerService

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicPlayerService.MusicBinder
            musicService = binder.getService()
            musicService.isPlaying.observe(this@MainActivity) {
                if (it) {
                    playBtn.visibility = View.INVISIBLE
                    stopBtn.visibility = View.VISIBLE
                } else {
                    playBtn.visibility = View.VISIBLE
                    stopBtn.visibility = View.INVISIBLE
                }
            }
        }
        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playBtn = findViewById(R.id.play_btn)
        stopBtn = findViewById(R.id.btn_stop)
        nextBtn = findViewById(R.id.next_btn)
        previousBtn = findViewById(R.id.previous_btn)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !Manifest.permission.POST_NOTIFICATIONS.checkPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }
        val intentBind = Intent(this, MusicPlayerService::class.java)
        bindService(intentBind, connection, Context.BIND_AUTO_CREATE)

        stopBtn.setOnClickListener {
            musicService.stop()
        }
        playBtn.setOnClickListener {
            musicService.play()
        }
        nextBtn.setOnClickListener {
            musicService.setNext()
        }
        previousBtn.setOnClickListener {
            musicService.setPrevious()
        }
    }
    private fun String.checkPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this@MainActivity, this) == PackageManager.PERMISSION_GRANTED
    }
}
