package com.fcfm.photopet.utils

import android.content.Context
import android.net.wifi.hotspot2.pps.Credential
import com.fcfm.photopet.model.Album
import com.fcfm.photopet.model.Publication
import com.google.gson.Gson

class SharedPref (val context: Context){

    val SHARED_AUTOLOGIN = "AUTOLOGIN"
    val SHARED_DRAFT = "POSTDRAFT"



    val managerAutologin =  context.getSharedPreferences(SHARED_AUTOLOGIN, Context.MODE_PRIVATE)
    val managerDraft =  context.getSharedPreferences(SHARED_DRAFT, Context.MODE_PRIVATE)

    fun saveEmail(email: String){
        var editor =  managerAutologin.edit()
        editor.putString("email", email)
        editor.commit()
    }

    fun getEmail(): String? {
        return managerAutologin.getString("email","")
    }

    fun savePost(post:Publication){
        val albumList = post.albums
        post.albums = mutableListOf()
        val postJSON = Gson().toJson(post)
        var editor =  managerDraft.edit()
        editor.putString("post", postJSON)
        editor.commit()
        for (i in 1..10){
            if(i <= albumList!!.size){
                val albumJSON = Gson().toJson(albumList[i-1])
                editor.putString("album"+i.toString(), albumJSON)
                editor.commit()
            }
            else{
                val albumJSON = Gson().toJson(Album(null,null,null,"xa100",null))
                editor.putString("album"+i.toString(), albumJSON)
                editor.commit()
            }
        }

    }

    fun getPost():Publication{
        val postJSON = managerDraft.getString("post",null)
        if(postJSON.isNullOrEmpty()){
            return Publication()
        }else{
            var post = Gson().fromJson(postJSON, Publication::class.java)
            for (i in 1..10){
                val albumJSON = managerDraft.getString("album"+i.toString(),null)
                if(!albumJSON.isNullOrEmpty()){
                    val album = Gson().fromJson(albumJSON, Album::class.java)
                    if(album.description!! != "xa100")
                        post.albums!!.add(album)
                }

            }
                return post

        }

    }
}