package com.fcfm.photopet.controller.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.photopet.R
import com.fcfm.photopet.controller.Adapter.PostListRecyclerAdapter
import com.fcfm.photopet.model.Publication
import com.fcfm.photopet.model.Tag
import com.fcfm.photopet.utils.retrofit.RestEngine
import com.fcfm.photopet.utils.retrofit.ServicePost
import com.fcfm.photopet.utils.retrofit.ServiceTag
import kotlinx.android.synthetic.main.fragment_busqueda.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

 class Fragment_Busqueda: Fragment(), androidx.appcompat.widget.SearchView.OnQueryTextListener, View.OnClickListener{
    private lateinit var rootView: View
    private var postAdapter: PostListRecyclerAdapter? = null
     lateinit var autoTagListAdapter: ArrayAdapter<Tag>
     var tagList: MutableList<Tag> = mutableListOf()
     var orderStatus = 0;

    private var posts: MutableList<Publication> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView =  inflater.inflate(R.layout.fragment_busqueda, container, false)
        val recyclerPostSearch = rootView.findViewById<RecyclerView>(R.id.rvSearchCards)
        val editSV = rootView.findViewById<androidx.appcompat.widget.SearchView>(R.id.editSearch)
        val autotv_search = rootView.findViewById<AutoCompleteTextView>(R.id.autotv_search)
        val radioDesc = rootView.findViewById<RadioButton>(R.id.radioDesc)
        val radioTag = rootView.findViewById<RadioButton>(R.id.radioTag)
        val OrderOld = rootView.findViewById<RadioButton>(R.id.OrderOld)
        val OrderNew = rootView.findViewById<RadioButton>(R.id.OrderNew)
        val OrderPopular = rootView.findViewById<RadioButton>(R.id.OrderPopular)

        //val radioTag = rootView.findViewById<androidx.appcompat.widget.SearchView>(R.id.radioTag)

        recyclerPostSearch.layoutManager =  LinearLayoutManager(context)
        this.fillPostList()
        this.postAdapter =  PostListRecyclerAdapter(context, posts, activity)
        recyclerPostSearch.adapter = this.postAdapter

        editSV.setOnQueryTextListener(this)
        autotv_search.setOnItemClickListener { parent, view, position, id ->
            var selectedTag = parent!!.getItemAtPosition(position) as Tag
            searchByTag(selectedTag.id_tag!!)
        }
        radioDesc.setOnClickListener(this)
        radioTag.setOnClickListener(this)
        OrderOld.setOnClickListener(this)
        OrderNew.setOnClickListener(this)
        OrderPopular.setOnClickListener(this)

        return rootView
    }


    private fun fillPostList(){
        posts.clear()
        val service: ServicePost =  RestEngine.getRestEngine().create(ServicePost::class.java)
        val result: Call<List<Publication>> = service.getPublications()

        result.enqueue(object: Callback<List<Publication>> {
            override fun onFailure(call: Call<List<Publication>>, t: Throwable) {
                //loading.isDismiss()
                Toast.makeText(context,t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Publication>>, response: Response<List<Publication>>) {
                val item =  response.body()
                if (item!![0].id_publication != null){
                    for(p in item){
                        posts.add(p)
                    }

                }
                postAdapter!!.notifyDataSetChanged()

            }
        })
    }

     private fun searchByTag(id_tag:Int){
         posts.clear()
         val service: ServicePost =  RestEngine.getRestEngine().create(ServicePost::class.java)
         val result: Call<List<Publication>> = service.getPostTag(id_tag)

         result.enqueue(object: Callback<List<Publication>> {
             override fun onFailure(call: Call<List<Publication>>, t: Throwable) {
                 //loading.isDismiss()
                 Toast.makeText(context,t.message.toString(), Toast.LENGTH_LONG).show()
             }

             override fun onResponse(call: Call<List<Publication>>, response: Response<List<Publication>>) {
                 val item =  response.body()
                 if (item!![0].id_publication != null){
                     for(p in item){
                         posts.add(p)
                     }

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

     override fun onClick(v: View?) {
         val radioDesc = rootView.findViewById<RadioButton>(R.id.radioDesc)
         val radioTag = rootView.findViewById<RadioButton>(R.id.radioTag)


         val editSV = rootView.findViewById<androidx.appcompat.widget.SearchView>(R.id.editSearch)

         if(v!=null){
             when (v.id){
                 R.id.radioDesc ->{
                     if(radioDesc.isChecked){

                         editSV.visibility = View.VISIBLE
                         autotv_search.visibility = View.GONE
                         iconSearch.visibility = View.INVISIBLE
                         autotv_search.text = null
                         fillPostList()

                     }
                 }
                 R.id.radioTag ->{
                     if(radioTag.isChecked){
                         editSV.visibility = View.GONE
                         autotv_search.visibility = View.VISIBLE
                         iconSearch.visibility = View.VISIBLE
                         fillTagList()
                     }
                 }
                 R.id.OrderOld ->{
                     if(orderStatus != 0){
                         orderStatus = Tag().TagServices().setOrder(posts, postAdapter!!, 0)

                     }

                 }

                 R.id.OrderNew ->{
                     if(orderStatus != 1){
                        orderStatus = Tag().TagServices().setOrder(posts, postAdapter!!, 1)

                     }
                 }

                 R.id.OrderPopular ->{

                     if(orderStatus != 2){
                         orderStatus = Tag().TagServices().setOrder(posts, postAdapter!!, 2)

                     }
                 }

             }
         }
     }

     private fun fillTagList(){
         tagList.clear()
         val service: ServiceTag =  RestEngine.getRestEngine().create(ServiceTag::class.java)
         val result: Call<List<Tag>> = service.getTags()

         result.enqueue(object: Callback<List<Tag>> {
             override fun onFailure(call: Call<List<Tag>>, t: Throwable) {
                 //loading.isDismiss()
                 Toast.makeText(context,t.message.toString(), Toast.LENGTH_LONG).show()
             }

             override fun onResponse(call: Call<List<Tag>>, response: Response<List<Tag>>) {
                 val item =  response.body()
                 if(item!![0].id_tag != null){

                     tagList = item.toMutableList()

                     autoTagListAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, tagList)
                     autotv_search.setAdapter(autoTagListAdapter)
                 }

             }
         })


     }



 }
