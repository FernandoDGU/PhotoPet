package com.fcfm.photopet.utils.retrofit

import com.fcfm.photopet.model.User
import retrofit2.Call
import retrofit2.http.*

//Retrofi usa una interface para hacer la petición hacia el servidor
interface ServiceUser{

    //Servicios para consumir el Album
    @GET("user.php")
    fun getUsers(): Call<List<User>>

    @GET("user.php?email={emailParam}")
    fun getUser(@Path("email") email: String): Call<User>

    @GET("user.php?email={emailParam}&pass={passParam}")
    fun loggedUser(@Path("emailParam") email: String, @Path("passParam") password: String): Call<List<User>>

    @Headers("Content-Type: application/json")
    @POST("user.php")
    fun insertUser(@Body userData: User): Call<RetrofitMessage>

    @Headers("Content-Type: application/json")
    @PUT("user.php?email={emailParam}")
    fun updateUser(@Path("emailParam") email: String, @Body userData: User): Call<Int>

    @DELETE("user.php?email={emailParam}")
    fun deleteUser(@Path("emailParam") email: String): Call<Int>

}