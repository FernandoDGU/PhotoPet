package com.fcfm.photopet.utils.retrofit

import com.fcfm.photopet.model.Publication
import com.fcfm.photopet.model.Tag
import com.fcfm.photopet.model.User
import retrofit2.Call
import retrofit2.http.*

//Retrofi usa una interface para hacer la petición hacia el servidor
interface ServiceUser{

    //Servicios para consumir el Album
    @GET("user.php")
    fun getUsers(): Call<List<User>>

    @GET("user.php?email={emailParam}")
    fun getUser(@Path("emailParam") email: String): Call<User>

    @GET("user.php")
    fun logInUser(@Query("email") email: String, @Query("pass") password: String): Call<User>

    @Headers("Content-Type: application/json")
    @POST("user.php")
    fun insertUser(@Body userData: User): Call<RetrofitMessage>

    @Headers("Content-Type: application/json")
    @PUT("user.php")
    fun updateUser(@Query("email") email: String, @Body userData: User): Call<RetrofitMessage>

    @DELETE("user.php?email={emailParam}")
    fun deleteUser(@Path("emailParam") email: String): Call<Int>

}

interface ServiceTag{
    @GET("tag.php")
    fun getTags(): Call<List<Tag>>

    @Headers("Content-Type: application/json")
    @POST("tag.php")
    fun insertTag(@Body tagData: Tag): Call<RetrofitMessage>
}

interface ServicePost{
    @GET("publication.php")
    fun getPublications(): Call<List<Publication>>

    @Headers("Content-Type: application/json")
    @POST("publication.php")
    fun insertPost(@Body postData: Publication): Call<RetrofitMessage>
}