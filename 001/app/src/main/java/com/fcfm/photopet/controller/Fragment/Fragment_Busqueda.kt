package com.fcfm.photopet.controller.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.photopet.R
import com.fcfm.photopet.controller.Adapter.PostListRecyclerAdapter
import com.fcfm.photopet.model.Publication
import com.fcfm.photopet.utils.retrofit.RestEngine
import com.fcfm.photopet.utils.retrofit.ServicePost
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        val service: ServicePost =  RestEngine.getRestEngine().create(ServicePost::class.java)
        val result: Call<List<Publication>> = service.getPublications()

        result.enqueue(object: Callback<List<Publication>> {
            override fun onFailure(call: Call<List<Publication>>, t: Throwable) {
                //loading.isDismiss()
                Toast.makeText(context,t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Publication>>, response: Response<List<Publication>>) {
                val item =  response.body()
                for(p in item!!){
                    posts.add(p)
                }
                postAdapter!!.notifyDataSetChanged()
            }
        })
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
