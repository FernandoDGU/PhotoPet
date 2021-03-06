package com.fcfm.photopet.model

import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.fcfm.photopet.controller.Adapter.PostListRecyclerAdapter
import com.fcfm.photopet.utils.UserApplication
import com.fcfm.photopet.utils.retrofit.RestEngine
import com.fcfm.photopet.utils.retrofit.ServiceTag
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class Tag(var id_tag: Int? = null, var tagname: String? = null)  : Serializable {
    override fun toString(): String {
        return this.tagname!!
    }

    inner class TagSQLite{
       fun CreateTag(tags: MutableList<Tag>){
            if(!UserApplication.dbHelper.deleteTagTable()){
                return
            }

            for (t in tags){
                UserApplication.dbHelper.insertTag(t)
            }

        }

        fun GetTags(): MutableList<Tag>{
            val getTags:MutableList<Tag> = UserApplication.dbHelper.getAllTags()
            return getTags
        }

        fun GetPostTag(id_post: Int): MutableList<Tag> {
            return UserApplication.dbHelper.Tags_by_Posts(id_post)
        }
    }

    inner class TagServices{
        fun setOrder(posts : MutableList<Publication>, adapter: PostListRecyclerAdapter, order:Int): Int{
            var orderedPost = posts.toMutableList()
            if(order == 0){
                orderedPost.sortBy { it.id_publication }
            }else if(order == 1){
                orderedPost.sortByDescending { it.id_publication }
            } else if(order == 2){
                orderedPost.sortByDescending { it.likes }
            }
            posts.clear()
            for(p in orderedPost){
                posts.add(p)
            }
            adapter!!.notifyDataSetChanged()
            return order;
        }
        fun listToString(tags : MutableList<Tag>): String{
            var text = ""
            if(tags.isNotEmpty()){
                for(t in tags){
                    if(tags[tags.lastIndex].id_tag == t.id_tag){
                        text += "${t.tagname}."
                    }else{
                        text += "${t.tagname}, "
                    }
                }
            }
            return text


        }
    }
}