package com.fcfm.photopet.controller

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.photopet.R
import com.fcfm.photopet.controller.Adapter.HomeRecyclerAdapter
import com.fcfm.photopet.controller.Adapter.TagListRecyclerAdapter
import com.fcfm.photopet.model.Tag
import com.fcfm.photopet.utils.loggedUser
import com.fcfm.photopet.utils.retrofit.RestEngine
import com.fcfm.photopet.utils.retrofit.RetrofitMessage
import com.fcfm.photopet.utils.retrofit.ServiceTag
import kotlinx.android.synthetic.main.activity_crearpublicacion.*
import kotlinx.android.synthetic.main.dialog_tags_publication.*
import kotlinx.android.synthetic.main.dialog_tags_publication.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreatePostActivity: AppCompatActivity(), View.OnClickListener, TagListRecyclerAdapter.OnTagClickListenener, TagListRecyclerAdapter.OnDeleteClickListener {
    lateinit var viewTagsPost: View
    lateinit var windowTagsPost: Dialog
    lateinit var autoCompleteTag: AutoCompleteTextView
    lateinit var autoTagListAdapter: ArrayAdapter<Tag>
    var tagList: MutableList<Tag> = mutableListOf()
    var selectedTagList: MutableList<Tag> = mutableListOf()
    private var tagListAdapter = TagListRecyclerAdapter(this, selectedTagList,this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crearpublicacion)
        supportActionBar?.hide()
        viewTagsPost = layoutInflater.inflate(R.layout.dialog_tags_publication, null);
        windowTagsPost = Dialog(this)
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
                    windowTagsPost.setContentView(viewTagsPost)
                    autoCompleteTag = viewTagsPost.findViewById<AutoCompleteTextView>(R.id.autotv_Tag)

                    autoCompleteTag.setOnItemClickListener { parent, view, position, id ->
                        var selectedTag = parent!!.getItemAtPosition(position) as Tag
                        selectedTagList.add(selectedTag);
                        tagListAdapter.notifyDataSetChanged()
                        autoCompleteTag.text = null

                        tagList.remove(selectedTag)
                        autoTagListAdapter.remove(selectedTag)
                        autoCompleteTag.setAdapter(autoTagListAdapter)
                    }
                    val recycler_tagList = viewTagsPost.findViewById<RecyclerView>(R.id.rv_tagList)
                    recycler_tagList.layoutManager = LinearLayoutManager(this)
                    recycler_tagList.adapter = tagListAdapter



                    windowTagsPost.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                    val window = windowTagsPost.window
                    window!!.setGravity(Gravity.CENTER)
                    windowTagsPost.show()
                    viewTagsPost.btn_createtag.setOnClickListener(this)

                    fillTagList()
                }
                R.id.btn_createtag ->{
                    val inputText = autoCompleteTag.text.toString()

                    if(inputText.isNullOrEmpty())
                        return

                    var alreadyExists = false
                    for (tag in selectedTagList){
                        if(tag.tagname!!.lowercase() == inputText.lowercase()){
                            alreadyExists = true;
                            break;
                        }
                    }
                    if(!alreadyExists){
                        for (tag in tagList){
                            if(tag.tagname!!.lowercase() == inputText.lowercase()){
                                alreadyExists = true;
                                break;
                            }
                        }
                    }

                    if(!alreadyExists){
                        insertTag(Tag(null, inputText))
                    }else{
                        Toast.makeText(this, R.string.tagNameErr, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    private fun insertTag(tag: Tag){
        val service: ServiceTag =  RestEngine.getRestEngine().create(ServiceTag::class.java)
        val result: Call<RetrofitMessage> = service.insertTag(tag)

        result.enqueue(object: Callback<RetrofitMessage> {
            override fun onFailure(call: Call<RetrofitMessage>, t: Throwable) {

                Toast.makeText(this@CreatePostActivity,t.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<RetrofitMessage>, response: Response<RetrofitMessage>) {
                val item =  response.body()
                when(item!!.message){
                    "ok" -> {
                        selectedTagList.add(tag)
                        tagListAdapter.notifyDataSetChanged()
                        autoCompleteTag.text = null
                        fillTagList()
                    }
                    else -> {
                        Toast.makeText(this@CreatePostActivity, item!!.message, Toast.LENGTH_SHORT).show()
                    }
                }

            }
        })
    }


    private fun showPost(){
        val intent = Intent(this, PostActivity::class.java)
        startActivity(intent)
    }

    private fun fillTagList(){
        tagList.clear()
        val service: ServiceTag =  RestEngine.getRestEngine().create(ServiceTag::class.java)
        val result: Call<List<Tag>> = service.getTags()

        result.enqueue(object: Callback<List<Tag>> {
            override fun onFailure(call: Call<List<Tag>>, t: Throwable) {
                //loading.isDismiss()
                Toast.makeText(this@CreatePostActivity,t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Tag>>, response: Response<List<Tag>>) {
                val item =  response.body()
                /*for( tag in item!!){
                    tagList.add(tag)
                }*/
                tagList = item!!.toMutableList()

                for(tag in selectedTagList){
                    for(innerTag in tagList){
                        if(innerTag.tagname!!.lowercase() == tag.tagname!!.lowercase()){
                            tagList.remove(innerTag)
                            break
                        }

                    }
                }
                autoTagListAdapter = ArrayAdapter(viewTagsPost.context, android.R.layout.simple_list_item_1, tagList)
                autoCompleteTag.setAdapter(autoTagListAdapter)
            }
        })


    }

    override fun onitemClick(tag: Tag) {
        Toast.makeText(this, tag.tagname, Toast.LENGTH_SHORT).show()
    }

    override fun ondeleteitemClick(tag: Tag) {
        selectedTagList.remove(tag)
        tagListAdapter.notifyDataSetChanged()
        tagList.add(tag)
        autoTagListAdapter.add(tag)
        autoCompleteTag.setAdapter(autoTagListAdapter)
    }


}