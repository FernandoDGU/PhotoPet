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
import com.fcfm.photopet.utils.LoadingDialog
import com.fcfm.photopet.utils.retrofit.RestEngine
import com.fcfm.photopet.utils.retrofit.ServicePost
import com.fcfm.photopet.utils.retrofit.ServiceTag
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.android.synthetic.main.fragment_busqueda.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

 class Fragment_Busqueda: Fragment(), androidx.appcompat.widget.SearchView.OnQueryTextListener, View.OnClickListener{
    private lateinit var rootView: View
    private lateinit var loading: LoadingDialog.FragmentLD
    private lateinit var autotv_search:MaterialAutoCompleteTextView
    private var postAdapter: PostListRecyclerAdapter? = null
     lateinit var autoTagListAdapter: ArrayAdapter<Tag>
     var tagList: MutableList<Tag> = mutableListOf()
     var completePostList: MutableList<Publication> = mutableListOf()
     var orderStatus = 1;
    private var posts: MutableList<Publication> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView =  inflater.inflate(R.layout.fragment_busqueda, container, false)
        loading = LoadingDialog().FragmentLD(this)
        val recyclerPostSearch = rootView.findViewById<RecyclerView>(R.id.rvSearchCards)
        val editSV = rootView.findViewById<androidx.appcompat.widget.SearchView>(R.id.editSearch)
        autotv_search = rootView.findViewById<MaterialAutoCompleteTextView>(R.id.autotv_search)
        val radioDesc = rootView.findViewById<RadioButton>(R.id.radioDesc)
        val radioTag = rootView.findViewById<RadioButton>(R.id.radioTag)
        val OrderOld = rootView.findViewById<RadioButton>(R.id.OrderOld)
        val OrderNew = rootView.findViewById<RadioButton>(R.id.OrderNew)
        val OrderPopular = rootView.findViewById<RadioButton>(R.id.OrderPopular)
        val textHeader = rootView.findViewById<TextView>(R.id.textView9)

        //val radioTag = rootView.findViewById<androidx.appcompat.widget.SearchView>(R.id.radioTag)

        recyclerPostSearch.layoutManager =  LinearLayoutManager(context)

        this.postAdapter =  PostListRecyclerAdapter(context, R.layout.card_publications2, posts, activity)
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
        this.fillPostList()
        return rootView
    }


    private fun fillPostList(){
        loading.startLoading()
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

                    if (item!![0].id_publication != null){
                        completePostList = item.toMutableList()
                        for(p in completePostList){
                            posts.add(p)
                        }

                    }
                    postAdapter!!.notifyDataSetChanged()
                    loading.isDismiss()

                }
            })
        }else{
            val item = Publication().PublicationSQLite().GetPostImages()
            if(item.size != 0){
                if (item[0].id_publication != null){
                    completePostList = item.toMutableList()
                    for(p in completePostList){
                        posts.add(p)
                    }
                }
            }

            postAdapter!!.notifyDataSetChanged()
            loading.isDismiss()
        }

    }

     private fun searchByTag(id_tag:Int){
         loading.startLoading()
         posts.clear()
         if(RestEngine.hasInternetConnection(requireContext())){
             val service: ServicePost =  RestEngine.getRestEngine().create(ServicePost::class.java)
             val result: Call<List<Publication>> = service.getPostTag(id_tag)

             result.enqueue(object: Callback<List<Publication>> {
                 override fun onFailure(call: Call<List<Publication>>, t: Throwable) {
                     loading.isDismiss()
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
                     loading.isDismiss()

                 }
             })
         }else{
             val item = Publication().PublicationSQLite().GetPostTag(id_tag)
             if(item.size != 0){
                 if (item[0].id_publication != null){
                     for(p in item){
                         posts.add(p)
                     }

                 }
             }
             postAdapter!!.notifyDataSetChanged()
             loading.isDismiss()
         }

     }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            if(postAdapter != null) {
                //posts = completePostList.toMutableList()
                postAdapter!!.notifyDataSetChanged()
                this.postAdapter?.filter?.filter(newText)
            }
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
                         //autotv_search
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
                         loading.startLoading()
                         orderStatus = Tag().TagServices().setOrder(posts, postAdapter!!, 0)
                         loading.isDismiss()

                     }

                 }

                 R.id.OrderNew ->{
                     if(orderStatus != 1){
                         loading.startLoading()
                        orderStatus = Tag().TagServices().setOrder(posts, postAdapter!!, 1)
                        loading.isDismiss()
                     }
                 }

                 R.id.OrderPopular ->{

                     if(orderStatus != 2){
                         loading.startLoading()
                         orderStatus = Tag().TagServices().setOrder(posts, postAdapter!!, 2)
                         loading.isDismiss()
                     }
                 }

             }
         }
     }

     private fun fillTagList(){
         loading.startLoading()
         tagList.clear()
         if(RestEngine.hasInternetConnection(requireContext())){
             val service: ServiceTag =  RestEngine.getRestEngine().create(ServiceTag::class.java)
             val result: Call<List<Tag>> = service.getTags()

             result.enqueue(object: Callback<List<Tag>> {
                 override fun onFailure(call: Call<List<Tag>>, t: Throwable) {
                     loading.isDismiss()
                     Toast.makeText(context,t.message.toString(), Toast.LENGTH_LONG).show()
                 }

                 override fun onResponse(call: Call<List<Tag>>, response: Response<List<Tag>>) {
                     val item =  response.body()
                     if(item!![0].id_tag != null){

                         tagList = item.toMutableList()

                         autoTagListAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, tagList)
                         autotv_search.setAdapter(autoTagListAdapter)
                     }
                     loading.isDismiss()

                 }
             })
         }else{
             val item = Tag().TagSQLite().GetTags()
             if(item.size != 0){
                 if(item[0].id_tag != null){

                     tagList = item.toMutableList()

                     autoTagListAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, tagList)
                     autotv_search.setAdapter(autoTagListAdapter)
                 }
             }

             loading.isDismiss()
         }



     }



 }
