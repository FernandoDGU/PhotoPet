package com.fcfm.photopet.controller.Fragment

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.transition.Scene
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.photopet.controller.CreatePostActivity
import com.fcfm.photopet.R
import com.fcfm.photopet.controller.Adapter.HomeRecyclerAdapter
import com.fcfm.photopet.controller.FragmentsActivity
import com.fcfm.photopet.controller.PostActivity
import com.fcfm.photopet.model.Publication
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Fragment_Inicio : Fragment(), HomeRecyclerAdapter.OnPostClickListenener{
    private lateinit var rootView: View
    private var posts: MutableList<Publication> = mutableListOf()
    private var homeAdapter = HomeRecyclerAdapter(this, posts,this)
    private lateinit var scene: Scene

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.fragment_inicio, container, false)
        this.fillPostList()
        val btnCreate = rootView.findViewById<FloatingActionButton>(R.id.floatbtnAddPost)
        val recyclerPostHome = rootView.findViewById<RecyclerView>(R.id.rvHomeCards)
        recyclerPostHome.layoutManager = GridLayoutManager(context, 2)
        recyclerPostHome.adapter = homeAdapter
        homeAdapter.notifyDataSetChanged()
        activity?.overridePendingTransition(0, 0)
        btnCreate!!.setOnClickListener{
            val intent = Intent(context, CreatePostActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.translate_left_side, R.anim.static_anim)
        }

        return rootView
    }

    private fun fillPostList(){
        var post:Publication? = null
        post = Publication(0,"Buenas tardes", "sadarien@gmail.com", null, R.drawable.background001)
        posts?.add(post)

        post = Publication(1,"Buenas noches", "sadarien3@gmail.com", null, R.drawable.puppy)
        posts?.add(post)

        post = Publication(2,"Buenas mañanas", "sadarien5@gmail.com", null, R.drawable.puppy2)
        posts?.add(post)

        post = Publication(3,"Buenas buenas", "sadarie2n@gmail.com", null, R.drawable.puppy)
        posts?.add(post)

        post = Publication(4,"Buenas buenas buenas", "sadarien1@gmail.com", null, R.drawable.puppy2)
        posts?.add(post)

        post = Publication(4,"sdfgdsfgsdfg", "sadariesdfgsdfgn1@gmail.com", null, R.drawable.background001)
        posts?.add(post)

        post = Publication(4,"sdfgsdfgsdf", "sadariendsfgsdag1@gmail.com", null, R.drawable.puppy)
        posts?.add(post)
    }

    override fun onitemClick(post: Publication) {
        Log.d("post", post.description!!)
        Toast.makeText(context, post.id_publication.toString(), Toast.LENGTH_SHORT).show()
        val intent = Intent(context, PostActivity::class.java)
        startActivity(intent)
        activity?.overridePendingTransition(0, 0)
    }



}

