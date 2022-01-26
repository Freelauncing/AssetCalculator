package com.pozicointracker

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

        }

        fader()

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val myIntent = Intent(this, MainHomeActivity::class.java)
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(myIntent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                this.finish()
            }catch (e : Exception){
            }
        }, 2500)
    }

    // value range from [0 - 1] [invisible - visible]
    private fun fader() {
        val animator : ObjectAnimator = ObjectAnimator.ofFloat(
            findViewById(R.id.welcomeText), View.ALPHA,0f,1f)
        animator.duration = 1000
        animator.start()
    }
}