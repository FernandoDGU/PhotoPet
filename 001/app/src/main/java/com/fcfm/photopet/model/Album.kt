package com.fcfm.photopet.model

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import com.fcfm.photopet.utils.UserApplication
import java.io.ByteArrayOutputStream
import java.io.Serializable
import java.util.*

class Album(var id_album: Int? = null, var id_publication: Int? = null, var image: ByteArray? = null, var description: String? = "",
            var imageString: String? = "")  : Serializable {

                inner class AlbumSQLite{
                    fun CreateAlbum(albums:MutableList<Album>){
                        if(!UserApplication.dbHelper.deleteAlbumTable()){
                            return
                        }
                        for (a in albums){
                            UserApplication.dbHelper.insertAlbum(a)
                        }
                    }

                    fun GetAlbum(id_album:Int):Album{
                        val getAlbum:Album? = UserApplication.dbHelper.getAlbum(id_album)!!
                        return getAlbum!!
                    }

                    fun GetAllbum():MutableList<Album>{
                        val getAlbums:MutableList<Album> = UserApplication.dbHelper.getAllAlbums()
                        return getAlbums
                    }

                    fun GetAlbumPost(id_post: Int):MutableList<Album>{
                        val getAlbums:MutableList<Album> = UserApplication.dbHelper.getAlbumsPost(id_post)
                        return getAlbums
                    }
                }

}