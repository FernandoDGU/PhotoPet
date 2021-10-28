package com.fcfm.photopet.utils.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder

import com.google.gson.Gson


class RetrofitMessage(var message:String? = null){
}

//Esta clase configura  el rest Adapter, definindo su configuración inicial
class RestEngine{
    // nos permite acceder sin instanciar el objecto
    companion object{
        fun getRestEngine(): Retrofit {
            //Creamos el interceptor
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val gson = GsonBuilder()
                .setLenient()
                .create()

            val client =  OkHttpClient.Builder().addInterceptor(interceptor).build()
            val retrofit =  Retrofit.Builder()
                .baseUrl("http://${this.ip}/PhotoPetAPI/") // tu url
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()

            return  retrofit

        }








        private val ip: String = "192.168.1.119";
    }




}