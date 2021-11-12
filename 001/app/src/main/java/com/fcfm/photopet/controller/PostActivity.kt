package com.fcfm.photopet.controller

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.fcfm.photopet.R
import com.fcfm.photopet.model.Album
import com.fcfm.photopet.model.LikedPublication
import com.fcfm.photopet.model.Publication
import com.fcfm.photopet.model.Tag
import com.fcfm.photopet.utils.ImageUtils
import com.fcfm.photopet.utils.loggedUser
import com.fcfm.photopet.utils.retrofit.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_crearpublicacion.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PostActivity: AppCompatActivity(), View.OnClickListener{

    private lateinit var post:Publication

    private lateinit var likebtn: LottieAnimationView
    private lateinit var btnback: ImageButton

    private lateinit var imageProfilePost: CircleImageView
    private lateinit var txtNamePost: TextView
    private lateinit var DescPost:TextView
    private lateinit var imagesPost: ImageView
    private lateinit var btnLeftPost: ImageButton
    private lateinit var btnRightPost: ImageButton
    private lateinit var txtCountPost:TextView
    private lateinit var DescImagePost: TextView
    private lateinit var txtTagsPost: TextView

    private var like = false
    private var imagePos = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publicacion)
        supportActionBar?.hide()
        overridePendingTransition(R.anim.zoom_in, R.anim.static_anim)
        post = intent.getSerializableExtra("post") as Publication;
        findingViews()
        getTags()





    }


    private fun likeAnimation(animation: Int){
        if(!like){
            likebtn.setAnimation(animation)
            likebtn.playAnimation()
            Toast.makeText(applicationContext, "Te gusta", Toast.LENGTH_SHORT).show()
            likePost()
        }else{
            likebtn.animate()
                .alpha(0f)
                .setDuration(200)
                .setListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator) {
                        likebtn.setImageResource(R.drawable.twitter_like)
                        likebtn.alpha = 1f
                    }
            })
            Toast.makeText(applicationContext, "Ya no te gusta", Toast.LENGTH_SHORT).show()
            dislikePost()

        }

        like = !like
    }

    override fun finish(){
        super.finish()
        overridePendingTransition(R.anim.static_anim, R.anim.zoom_out)
    }

    override fun onClick(v: View?) {
        if(v!=null){
            when (v.id){
                R.id.BtnLike ->{
                    likeAnimation(R.raw.apple_event)
                }
                R.id.BtnbackPost ->{
                    onBackPressed()
                }
                R.id.btnLeftPost ->{
                    if(post.albums!!.isEmpty())
                        return

                    if(imagePos == 1){
                        imagePos = post.albums!!.size
                    }else{
                        imagePos--
                    }
                    val index = imagePos - 1
                    changeImage(index)
                }
                R.id.btnRightPost ->{
                    if(post.albums!!.isEmpty())
                        return

                    if(imagePos == post.albums!!.size){
                        imagePos = 1
                    }else{
                        imagePos++
                    }
                    val index = imagePos - 1
                    changeImage(index)
                }
            }
        }
    }

    private fun findingViews(){
        likebtn = findViewById(R.id.BtnLike)
        btnback = findViewById(R.id.BtnbackPost)
        imageProfilePost = findViewById(R.id.imageProfilePost)
        txtNamePost = findViewById(R.id.txtNamePost)
        DescPost = findViewById(R.id.DescPost)
        imagesPost = findViewById(R.id.imagesPost)
        btnLeftPost = findViewById(R.id.btnLeftPost)
        btnRightPost = findViewById(R.id.btnRightPost)
        txtCountPost = findViewById(R.id.txtCountPost)
        DescImagePost = findViewById(R.id.DescImagePost)
        txtTagsPost = findViewById(R.id.txtTagsPost)

        likebtn.setOnClickListener(this)
        btnback.setOnClickListener(this)
        btnLeftPost.setOnClickListener(this)
        btnRightPost.setOnClickListener(this)
    }

    private fun fillPage(){
        var strImage:String =  post.authorImage!!.replace("data:image/png;base64,","")
        var byteArray =  Base64.getDecoder().decode(strImage)
        imageProfilePost.setImageBitmap(ImageUtils.getBitMapFromByteArray(byteArray))
        txtNamePost.text = post.author
        DescPost.text = post.description
        changeImage(0)
        var text :String = ""
        if(post.tags != null)
            text = txtTagsPost.text.toString() + Tag().TagServices().listToString(post.tags!!)
        txtTagsPost.text = text

    }

    private fun getTags(){
        val service: ServiceTag =  RestEngine.getRestEngine().create(ServiceTag::class.java)
        val result: Call<List<Tag>> = service.getTagPost(post.id_publication!!)

        result.enqueue(object: Callback<List<Tag>> {
            override fun onFailure(call: Call<List<Tag>>, t: Throwable) {
                //loading.isDismiss()
                Toast.makeText(this@PostActivity,t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Tag>>, response: Response<List<Tag>>) {
                val item =  response.body()
                if(item!![0].id_tag != null){
                    post.tags = item.toMutableList()
                }

                getAlbums()

            }
        })
    }

    private fun getAlbums(){
        val service: ServiceAlbum =  RestEngine.getRestEngine().create(ServiceAlbum::class.java)
        val result: Call<List<Album>> = service.getAlbumPost(post.id_publication!!)

        result.enqueue(object: Callback<List<Album>> {
            override fun onFailure(call: Call<List<Album>>, t: Throwable) {
                //loading.isDismiss()
                Toast.makeText(this@PostActivity,t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Album>>, response: Response<List<Album>>) {
                val item =  response.body()
                if(item!![0].id_album != null){
                    post.albums = item.toMutableList()
                }
                searchLikedPost()
            }
        })
    }

    private fun searchLikedPost(){
        val service: ServicePost =  RestEngine.getRestEngine().create(ServicePost::class.java)
        val result: Call<Int> = service.getLikedPost(post.id_publication!!, loggedUser.getUser().email!!)

        result.enqueue(object: Callback<Int> {
            override fun onFailure(call: Call<Int>, t: Throwable) {
                //loading.isDismiss()
                Toast.makeText(this@PostActivity,t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                val item =  response.body()
                if(item == 0) like = false
                else if(item == 1) like = true
                if(like){
                    likebtn.setAnimation(R.raw.apple_event)
                    likebtn.playAnimation()
                }


                fillPage()
            }
        })
    }

    private fun likePost(){
        var likedPost = LikedPublication(null, post.id_publication, loggedUser.getUser().email)

        val service: ServicePost =  RestEngine.getRestEngine().create(ServicePost::class.java)
        val result: Call<RetrofitMessage> = service.likePost(likedPost)

        result.enqueue(object: Callback<RetrofitMessage> {
            override fun onFailure(call: Call<RetrofitMessage>, t: Throwable) {
                //loading.isDismiss()
                Toast.makeText(this@PostActivity,t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<RetrofitMessage>, response: Response<RetrofitMessage>) {
                val item =  response.body()
                when(item!!.message){
                    "ok" -> {

                    }
                    else -> {
                        Toast.makeText(this@PostActivity,item.message,Toast.LENGTH_LONG).show()
                    }
                }

            }
        })
    }

    private fun dislikePost(){
        var likedPost = LikedPublication(null, post.id_publication, loggedUser.getUser().email)

        val service: ServicePost =  RestEngine.getRestEngine().create(ServicePost::class.java)
        val result: Call<RetrofitMessage> = service.dislikePost(likedPost.id_publication!!, likedPost.email!!)

        result.enqueue(object: Callback<RetrofitMessage> {
            override fun onFailure(call: Call<RetrofitMessage>, t: Throwable) {
                //loading.isDismiss()
                Toast.makeText(this@PostActivity,t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<RetrofitMessage>, response: Response<RetrofitMessage>) {
                val item =  response.body()
                when(item!!.message){
                    "ok" -> {

                    }
                    else -> {
                        Toast.makeText(this@PostActivity,item.message,Toast.LENGTH_LONG).show()
                    }
                }

            }
        })
    }

    private fun changeImage(index: Int){
        val strImage:String =  post.albums!![index].imageString!!.replace("data:image/png;base64,","")
        val byteArray =  Base64.getDecoder().decode(strImage)
        imagesPost.setImageBitmap(ImageUtils.getBitMapFromByteArray(byteArray))
        txtCountPost.text = "${imagePos}/${post.albums!!.size}"
        if(post.albums!![index].description.isNullOrEmpty())
            DescImagePost.setText(R.string.NoDescImageMessage)
        else
            DescImagePost.text = post.albums!![index].description
    }

}