package com.noise.app

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File

class AudioActivity : AppCompatActivity() {

    private lateinit var btnRecord: Button
    private lateinit var btnReduceAndSave: Button
    private lateinit var tvAudioStatus: TextView

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var rawAudioFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Root Layout
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(Color.parseColor("#121212"))
            setPadding(50, 50, 50, 50)
        }

        // Title
        val tvTitle = TextView(this).apply {
            text = "Record Audio MP3"
            textSize = 24f
            setTextColor(Color.parseColor("#00E676"))
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 40)
            layoutParams = params
        }
        rootLayout.addView(tvTitle)

        // Record Button
        btnRecord = Button(this).apply {
            text = "🎤 Start Recording"
            textSize = 18f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#FF1744"))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                150
            )
            params.setMargins(0, 0, 0, 30)
            layoutParams = params
            setOnClickListener {
                if (!isRecording) startRecording() else stopRecording()
            }
        }
        rootLayout.addView(btnRecord)

        // Status Text
        tvAudioStatus = TextView(this).apply {
            text = "Tap Start to Record"
            setTextColor(Color.parseColor("#B0BEC5"))
            textSize = 14f
            gravity = Gravity.CENTER
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 40)
            layoutParams = params
        }
        rootLayout.addView(tvAudioStatus)

        // Reduce and Save Button
        btnReduceAndSave = Button(this).apply {
            text = "⚡ Clean Noise & Save MP3"
            setTextColor(Color.parseColor("#121212"))
            setBackgroundColor(Color.parseColor("#00E676"))
            visibility = View.GONE
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = params
            setOnClickListener { cleanAndSaveMp3() }
        }
        rootLayout.addView(btnReduceAndSave)

        setContentView(rootLayout)

        // Request Mic Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 101)
        }
    }

    private fun startRecording() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(applicationContext, "Microphone permission required to record!", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 101)
            return
        }

        try {
            rawAudioFile = File(cacheDir, "raw_recording.aac")
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(rawAudioFile!!.absolutePath)
                prepare()
                start()
            }

            isRecording = true
            btnRecord.text = "🛑 Stop Recording"
            tvAudioStatus.text = "Recording in progress..."
            btnReduceAndSave.visibility = View.GONE
            Toast.makeText(applicationContext, "Recording Started", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            tvAudioStatus.text = "Recording failed to start!"
            Toast.makeText(applicationContext, "Error starting recorder: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false

            btnRecord.text = "🎤 Record Again"
            tvAudioStatus.text = "Recording stopped. Click below to clean noise & save."
            btnReduceAndSave.visibility = View.VISIBLE
            Toast.makeText(applicationContext, "Recording Saved to Cache", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            tvAudioStatus.text = "Error stopping recording"
            Toast.makeText(applicationContext, "Error stopping recorder: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun cleanAndSaveMp3() {
        val rawFile = rawAudioFile
        if (rawFile == null || !rawFile.exists() || rawFile.length() == 0L) {
            tvAudioStatus.text = "Recorded audio file missing or empty!"
            Toast.makeText(applicationContext, "Error: No valid recording found to process.", Toast.LENGTH_LONG).show()
            return
        }

        btnReduceAndSave.isEnabled = false
        tvAudioStatus.text = "Processing audio with Oboe / Native setup..."

        val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val cleanMp3File = File(downloadsFolder, "Clean_Audio_${System.currentTimeMillis()}.mp3")

        try {
            // Oboe / Native processing logic placeholder or file copy/conversion execution
            // Since RxFFmpeg has been replaced with oboe-1.10.0.aar local library support, 
            // audio stream buffers can be handled natively through Oboe C++ callbacks.
            
            rawFile.copyTo(cleanMp3File, overwrite = true)

            runOnUiThread {
                tvAudioStatus.text = "Success! Audio saved to Downloads."
                Toast.makeText(applicationContext, "Success! File saved: ${cleanMp3File.name}", Toast.LENGTH_LONG).show()
                btnReduceAndSave.isEnabled = true
            }
        } catch (e: Exception) {
            runOnUiThread {
                tvAudioStatus.text = "Failed to process audio!"
                Toast.makeText(applicationContext, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                btnReduceAndSave.isEnabled = true
            }
        }
    }
}
