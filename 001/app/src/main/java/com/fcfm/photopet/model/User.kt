package com.fcfm.photopet.model

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import com.fcfm.photopet.utils.UserApplication
import java.io.ByteArrayOutputStream
import java.util.*


class User(var email: String? = null,
           var fullname: String? = null,
           var firstname: String? = null,
           var lastname: String? = null,
           var password: String? = null,
           var phone: String? = null,
           var description: String? = null,
           var image:String? =  null) {

    inner class UserSQLite{
        fun CreateUser(users: MutableList<User>){
            if(!UserApplication.dbHelper.deleteUserTable()){
                return
            }
            for (u in users){
                UserApplication.dbHelper.insertUser(u)
            }


        }

        fun GetUser(email:String):User{
            val getUser:User? = UserApplication.dbHelper.getUser002(email)!!
            return getUser!!
        }
    }

}

class SuperData(
    var users: MutableList<User> = mutableListOf(),
    var tags: MutableList<Tag> = mutableListOf(),
    var publications: MutableList<Publication> = mutableListOf(),
    var albums: MutableList<Album> = mutableListOf(),
    var likepublication: MutableList<LikedPublication> = mutableListOf(),
    var publicationtag: MutableList<Publication_tag> = mutableListOf(),
){

}