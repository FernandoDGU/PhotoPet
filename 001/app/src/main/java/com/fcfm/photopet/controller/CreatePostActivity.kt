package com.fcfm.photopet.controller

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.fcfm.photopet.R
import kotlinx.android.synthetic.main.activity_crearpublicacion.*
import kotlinx.android.synthetic.main.dialog_tags_publication.view.*

class CreatePostActivity: AppCompatActivity(), View.OnClickListener {
    lateinit var viewTagsPost: View
    lateinit var windowTagsPost: PopupWindow
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crearpublicacion)
        supportActionBar?.hide()
        viewTagsPost = layoutInflater.inflate(R.layout.dialog_tags_publication, null);
        windowTagsPost = PopupWindow(this)
        btnPublicar.setOnClickListener(this)
        btnTagsPost.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if(v!=null){
            when (v!!.id){
                R.id.btnPublicar ->{
                    showPost()
                }
                R.id.btnTagsPost ->{
                    windowTagsPost.contentView = viewTagsPost;
                    windowTagsPost.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    windowTagsPost.showAtLocation(btnTagsPost, Gravity.CENTER,0, 0);
                    viewTagsPost.btn_back.setOnClickListener(this)
                }
                R.id.btn_back ->{
                    windowTagsPost.dismiss()
                }
            }
        }
    }

    private fun showPost(){
        val intent = Intent(this, PostActivity::class.java)
        startActivity(intent)
    }
}