package com.fcfm.photopet.controller

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.fcfm.photopet.R

class PostActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publicacion)
        supportActionBar?.hide()
        overridePendingTransition(R.anim.zoom_in, R.anim.static_anim)
        val likebtn = findViewById(R.id.BtnLike) as LottieAnimationView
        val btnback = findViewById(R.id.BtnbackPost) as ImageButton
        var like = false
        likebtn.setOnClickListener{
            like = likeAnimation(likebtn, R.raw.apple_event, like)
        }
        btnback.setOnClickListener{
            onBackPressed()
        }

    }


    private fun likeAnimation(imageView: LottieAnimationView, animation: Int, like:Boolean):Boolean{
        if(!like){
            imageView.setAnimation(animation)
            imageView.playAnimation()
            Toast.makeText(applicationContext, "Te gusta", Toast.LENGTH_SHORT).show()
        }else{
            imageView.animate()
                .alpha(0f)
                .setDuration(200)
                .setListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator) {
                        imageView.setImageResource(R.drawable.twitter_like)
                        imageView.alpha = 1f
                    }
            })
            Toast.makeText(applicationContext, "Ya no te gusta", Toast.LENGTH_SHORT).show()

        }

        return !like
    }

    override public fun finish(){
        super.finish()
        overridePendingTransition(R.anim.static_anim, R.anim.zoom_out)
    }
}