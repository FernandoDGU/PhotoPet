package com.fcfm.photopet.controller.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.photopet.R
import com.fcfm.photopet.controller.Adapter.PostListRecyclerAdapter
import com.fcfm.photopet.model.Publication

class Fragment_Busqueda: Fragment(), androidx.appcompat.widget.SearchView.OnQueryTextListener{
    private lateinit var rootView: View
    private var postAdapter: PostListRecyclerAdapter? = null

    private var posts: MutableList<Publication> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.fragment_busqueda, container, false)
        val recyclerPostSearch = rootView.findViewById<RecyclerView>(R.id.rvSearchCards)
        val editSV = rootView.findViewById<androidx.appcompat.widget.SearchView>(R.id.editSearch)
        recyclerPostSearch.layoutManager =  LinearLayoutManager(context)
        this.fillPostList()
        this.postAdapter =  PostListRecyclerAdapter(context, posts)
        recyclerPostSearch.adapter = this.postAdapter

        editSV.setOnQueryTextListener(this)

        return rootView
    }


    private fun fillPostList(){
        var post: Publication? = null
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            if(postAdapter != null) this.postAdapter?.filter?.filter(newText)
        }
        return false
    }

}
