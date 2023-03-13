package com.tec.mvvm_notesapp

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.airbnb.lottie.LottieAnimationView

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        this.supportActionBar?.hide()
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        actionBar?.hide()

        val backgroundanimation: LottieAnimationView= findViewById(R.id.animation_view)

        Handler().postDelayed({
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        },2600)
    }
}