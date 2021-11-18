package com.fcfm.photopet.controller.Fragment

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.res.Configuration
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
import com.fcfm.photopet.model.*
import com.fcfm.photopet.utils.LoadingDialog
import com.fcfm.photopet.utils.retrofit.RestEngine
import com.fcfm.photopet.utils.retrofit.ServicePost
import com.fcfm.photopet.utils.retrofit.ServiceTag
import com.fcfm.photopet.utils.retrofit.ServiceUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Fragment_Inicio : Fragment(), HomeRecyclerAdapter.OnPostClickListenener{
    private lateinit var rootView: View
    private lateinit var loading: LoadingDialog.FragmentLD
    private var posts: MutableList<Publication> = mutableListOf()
    private var homeAdapter = HomeRecyclerAdapter(this, posts,this)
    private var spanCount = 2;


    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView =  inflater.inflate(R.layout.fragment_inicio, container, false)
        loading = LoadingDialog().FragmentLD(this)
        val btnCreate = rootView.findViewById<FloatingActionButton>(R.id.floatbtnAddPost)
        val recyclerPostHome = rootView.findViewById<RecyclerView>(R.id.rvHomeCards)
        spanCount = isInPortrait()
        recyclerPostHome.layoutManager = GridLayoutManager(context, spanCount)
        recyclerPostHome.adapter = homeAdapter
        activity?.overridePendingTransition(0, 0)
        btnCreate!!.setOnClickListener{
            val intent = Intent(context, CreatePostActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.translate_left_side, R.anim.static_anim)
        }
        return rootView
    }

    override fun onResume() {
        super.onResume()
        loading.startLoading()
        this.fillPostList()

    }

    private fun fillPostList(){
        posts.clear()
        if(RestEngine.hasInternetConnection(requireContext())){
            val service: ServicePost =  RestEngine.getRestEngine().create(ServicePost::class.java)
            val result: Call<List<Publication>> = service.getPublications()

            result.enqueue(object: Callback<List<Publication>> {
                override fun onFailure(call: Call<List<Publication>>, t: Throwable) {
                    loading.isDismiss()
                    Toast.makeText(context,t.message.toString(), Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<List<Publication>>, response: Response<List<Publication>>) {
                    val item =  response.body()
                    if(item!![0].id_publication != null){
                        for(p in item){
                            posts.add(p)
                        }
                    }
                    homeAdapter.notifyDataSetChanged()
                    saveToSQLite()
                }
            })
        }else{
            val item = Publication().PublicationSQLite().GetPostImages()
            for(p in item){
                posts.add(p)
            }
            homeAdapter.notifyDataSetChanged()
            loading.isDismiss()
        }


    }

    private fun isInPortrait():Int{
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return 2
        } else {
            return 3
        }
    }

    override fun onitemClick(post: Publication) {
        val intent = Intent(context, PostActivity::class.java)
        intent.putExtra("post", post)
        startActivity(intent)
        activity?.overridePendingTransition(0, 0)
    }

    private fun saveToSQLite(){
        if(!RestEngine.hasInternetConnection(requireContext().applicationContext))
            return
        val service: ServiceUtils =  RestEngine.getRestEngine().create(ServiceUtils::class.java)
        val result: Call<SuperData> = service.getAllData("a")

        result.enqueue(object: Callback<SuperData> {
            override fun onFailure(call: Call<SuperData>, t: Throwable) {
                loading.isDismiss()
                Toast.makeText(context,t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<SuperData>, response: Response<SuperData>) {
                val item =  response.body()

                if(item!!.users[0].email!!.isNotEmpty())
                    User().UserSQLite().CreateUser(item.users)
                if(item.tags[0].id_tag != null)
                    Tag().TagSQLite().CreateTag(item.tags)
                if(item.publications[0].id_publication != null)
                    Publication().PublicationSQLite().CreatePost(item.publications)
                if(item.albums[0].id_album != null)
                    Album().AlbumSQLite().CreateAlbum(item.albums)
                if(item.likepublication[0].id_liked_publications != null)
                    LikedPublication().LikedPublicationSQLite().CreateLiked(item.likepublication)
                if(item.publicationtag[0].id_publication_tag != null)
                    Publication_tag().Publication_tagSQLite().CreatePostTag(item.publicationtag)
                loading.isDismiss()

            }
        })
    }



}

