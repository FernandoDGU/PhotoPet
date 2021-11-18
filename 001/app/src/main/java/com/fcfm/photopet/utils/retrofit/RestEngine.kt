package com.fcfm.photopet.utils.retrofit

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder

import com.google.gson.Gson
import android.net.NetworkInfo

import android.net.ConnectivityManager
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService





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
                .baseUrl("http://142.93.6.183/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()

            return  retrofit

        }

        fun hasInternetConnection(context: Context):Boolean
        {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            return isConnected
        }







    }




}