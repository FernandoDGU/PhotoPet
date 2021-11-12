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
import android.widget.ArrayAdapter
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
import com.fcfm.photopet.model.Tag
import com.fcfm.photopet.utils.retrofit.RestEngine
import com.fcfm.photopet.utils.retrofit.ServicePost
import com.fcfm.photopet.utils.retrofit.ServiceTag
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Fragment_Inicio : Fragment(), HomeRecyclerAdapter.OnPostClickListenener{
    private lateinit var rootView: View
    private var posts: MutableList<Publication> = mutableListOf()
    private var homeAdapter = HomeRecyclerAdapter(this, posts,this)

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView =  inflater.inflate(R.layout.fragment_inicio, container, false)

        val btnCreate = rootView.findViewById<FloatingActionButton>(R.id.floatbtnAddPost)
        val recyclerPostHome = rootView.findViewById<RecyclerView>(R.id.rvHomeCards)
        recyclerPostHome.layoutManager = GridLayoutManager(context, 2)
        recyclerPostHome.adapter = homeAdapter
        activity?.overridePendingTransition(0, 0)
        btnCreate!!.setOnClickListener{
            val intent = Intent(context, CreatePostActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.translate_left_side, R.anim.static_anim)
        }
        this.fillPostList()

        return rootView
    }

    private fun fillPostList(){
        val service: ServicePost =  RestEngine.getRestEngine().create(ServicePost::class.java)
        val result: Call<List<Publication>> = service.getPublications()

        result.enqueue(object: Callback<List<Publication>> {
            override fun onFailure(call: Call<List<Publication>>, t: Throwable) {
                //loading.isDismiss()
                Toast.makeText(context,t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Publication>>, response: Response<List<Publication>>) {
                val item =  response.body()
                if(item!![0].id_publication != null){
                    for(p in item){
                        posts.add(p)
                    }
                    homeAdapter.notifyDataSetChanged()
                }

            }
        })
    }

    override fun onitemClick(post: Publication) {
        val intent = Intent(context, PostActivity::class.java)
        intent.putExtra("post", post)
        startActivity(intent)
        activity?.overridePendingTransition(0, 0)
    }



}

