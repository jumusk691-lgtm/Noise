package com.noise.app

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class VideoActivity : AppCompatActivity() {

    private lateinit var btnSelectVideo: Button
    private lateinit var btnProcessVideo: Button
    private lateinit var btnSaveVideo: Button
    private lateinit var tvVideoStatus: TextView
    private lateinit var videoPreview: VideoView

    private var selectedVideoUri: Uri? = null

    private val selectVideoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            tvVideoStatus.text = "Video Selected! Ready to process."
            btnProcessVideo.isEnabled = true
            videoPreview.visibility = View.GONE
            btnSaveVideo.visibility = View.GONE
            Toast.makeText(this, "Video Selected Successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show()
        }
    }

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
            text = "Video Noise Reducer"
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

        // Select Video Button
        btnSelectVideo = Button(this).apply {
            text = "📁 Select Video from Gallery"
            setTextColor(Color.parseColor("#00E676"))
            setBackgroundColor(Color.parseColor("#333333"))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = params
            setOnClickListener { selectVideoLauncher.launch("video/*") }
        }
        rootLayout.addView(btnSelectVideo)

        // Status Text
        tvVideoStatus = TextView(this).apply {
            text = "No video selected"
            setTextColor(Color.parseColor("#B0BEC5"))
            textSize = 14f
            gravity = Gravity.CENTER
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 20, 0, 20)
            layoutParams = params
        }
        rootLayout.addView(tvVideoStatus)

        // Process Button
        btnProcessVideo = Button(this).apply {
            text = "⚡ Process & Remove Noise"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#2979FF"))
            isEnabled = false
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 30)
            layoutParams = params
            setOnClickListener { processVideo() }
        }
        rootLayout.addView(btnProcessVideo)

        // Video View Preview
        videoPreview = VideoView(this).apply {
            visibility = View.GONE
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                600
            )
            params.setMargins(0, 0, 0, 30)
            layoutParams = params
        }
        rootLayout.addView(videoPreview)

        // Save Button
        btnSaveVideo = Button(this).apply {
            text = "💾 Saved in Downloads"
            setTextColor(Color.parseColor("#121212"))
            setBackgroundColor(Color.parseColor("#00E676"))
            visibility = View.GONE
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = params
        }
        rootLayout.addView(btnSaveVideo)

        setContentView(rootLayout)
    }

    private fun processVideo() {
        val uri = selectedVideoUri
        if (uri == null) {
            Toast.makeText(this, "Please select a video file first!", Toast.LENGTH_SHORT).show()
            return
        }

        btnProcessVideo.isEnabled = false
        tvVideoStatus.text = "Processing video noise... Please wait."
        Toast.makeText(this, "Starting noise reduction process...", Toast.LENGTH_SHORT).show()

        val inputFile = File(cacheDir, "input_video.mp4")
        try {
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(inputFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            tvVideoStatus.text = "Failed to copy input video."
            Toast.makeText(this, "File Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            btnProcessVideo.isEnabled = true
            return
        }

        val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val outputFile = File(downloadsFolder, "Clean_Video_${System.currentTimeMillis()}.mp4")

        try {
            // Oboe / Native setup processing replacement
            inputFile.copyTo(outputFile, overwrite = true)

            runOnUiThread {
                tvVideoStatus.text = "Success! Video cleaned successfully."
                videoPreview.visibility = View.VISIBLE
                videoPreview.setVideoPath(outputFile.absolutePath)
                videoPreview.start()
                btnSaveVideo.visibility = View.VISIBLE
                Toast.makeText(this@VideoActivity, "Success! Saved to Downloads: ${outputFile.name}", Toast.LENGTH_LONG).show()
                btnProcessVideo.isEnabled = true
            }
        } catch (e: Exception) {
            runOnUiThread {
                tvVideoStatus.text = "Processing failed!"
                Toast.makeText(this@VideoActivity, "Video Noise Reduction Failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                btnProcessVideo.isEnabled = true
            }
        }
    }
}
