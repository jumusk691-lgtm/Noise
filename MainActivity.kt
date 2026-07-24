package com.noise.app

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        // Native C++ Oboe library load karein
        init {
            System.loadLibrary("noise_engine")
        }
    }

    // Native functions declaration
    external fun startAudioEngine(): Boolean
    external fun stopAudioEngine()

    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Main Layout constructed programmatically without XML
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#121212"))
            setPadding(60, 60, 60, 60)
        }

        // Title
        val tvTitle = TextView(this).apply {
            text = "AI Noise Reducer"
            textSize = 28f
            setTextColor(Color.parseColor("#00E676"))
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 80)
            layoutParams = params
        }
        rootLayout.addView(tvTitle)

        // Noise Engine Toggle Button (Oboe Test)
        val btnNoiseToggle = Button(this).apply {
            text = "🔊 Start Oboe Noise"
            textSize = 18f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#00897B"))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                150
            )
            params.setMargins(0, 0, 0, 40)
            layoutParams = params
            setOnClickListener {
                if (isPlaying) {
                    stopAudioEngine()
                    isPlaying = false
                    text = "🔊 Start Oboe Noise"
                } else {
                    val success = startAudioEngine()
                    if (success) {
                        isPlaying = true
                        text = "🔇 Stop Oboe Noise"
                    }
                }
            }
        }
        rootLayout.addView(btnNoiseToggle)

        // Video Button
        val btnVideoModule = Button(this).apply {
            text = "🎥 Video Noise Reducer"
            textSize = 18f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#2979FF"))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                150
            )
            params.setMargins(0, 0, 0, 40)
            layoutParams = params
            setOnClickListener {
                startActivity(Intent(this@MainActivity, VideoActivity::class.java))
            }
        }
        rootLayout.addView(btnVideoModule)

        // Audio Button
        val btnAudioModule = Button(this).apply {
            text = "🎤 Record Audio MP3"
            textSize = 18f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#FF1744"))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                150
            )
            layoutParams = params
            setOnClickListener {
                startActivity(Intent(this@MainActivity, AudioActivity::class.java))
            }
        }
        rootLayout.addView(btnAudioModule)

        setContentView(rootLayout)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlaying) {
            stopAudioEngine()
        }
    }
}
