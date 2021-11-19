package com.fcfm.photopet.controller

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.fcfm.photopet.R
import com.fcfm.photopet.controller.Adapter.PostListRecyclerAdapter
import com.fcfm.photopet.model.*
import com.fcfm.photopet.utils.ImageUtils
import com.fcfm.photopet.utils.LoadingDialog
import com.fcfm.photopet.utils.loggedUser
import com.fcfm.photopet.utils.retrofit.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_crearpublicacion.*
import kotlinx.android.synthetic.main.activity_publicacion.*
import kotlinx.android.synthetic.main.dialog_user_publication.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PostActivity: AppCompatActivity(), View.OnClickListener{

    private var post:Publication = Publication()
    private lateinit var loading : LoadingDialog.ActivityLD
    private var postAdapter: PostListRecyclerAdapter? = null

    private lateinit var likebtn: LottieAnimationView
    private lateinit var btnback: ImageButton
    private lateinit var btnModif: Button

    private lateinit var imageProfilePost: CircleImageView
    private lateinit var txtNamePost: TextView
    private lateinit var DescPost:TextView
    private lateinit var imagesPost: ImageView
    private lateinit var btnLeftPost: ImageButton
    private lateinit var btnRightPost: ImageButton
    private lateinit var txtCountPost:TextView
    private lateinit var DescImagePost: TextView
    private lateinit var txtTagsPost: TextView
    private var posts = mutableListOf<Publication>()
    lateinit var windowTagsPost: Dialog
    lateinit var viewUserPost: View

    private var like = false
    private var imagePos = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loading = LoadingDialog().ActivityLD(this)
        loading.startLoading()
        setContentView(R.layout.activity_publicacion)
        supportActionBar?.hide()
        overridePendingTransition(R.anim.zoom_in, R.anim.static_anim)
        post = intent.getSerializableExtra("post") as Publication;

        windowTagsPost = Dialog(this)
        viewUserPost = layoutInflater.inflate(R.layout.dialog_user_publication, null);

        findingViews()
        getTags()





    }


    private fun likeAnimation(animation: Int){
        if(!like){
            likebtn.setAnimation(animation)
            likebtn.playAnimation()
            Toast.makeText(applicationContext, R.string.likeMessage, Toast.LENGTH_SHORT).show()
            likebtn.contentDescription = getString(R.string.LikeButton)
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
            Toast.makeText(applicationContext, R.string.dislikeMessage, Toast.LENGTH_SHORT).show()
            likebtn.contentDescription = getString(R.string.DislikeButton)
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
                R.id.imageProfilePost -> {
                    windowTagsPost.setContentView(viewUserPost)
                    windowTagsPost.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                    val window = windowTagsPost.window
                    window!!.setGravity(Gravity.CENTER)
                    windowTagsPost.show()
                    this.postAdapter =  PostListRecyclerAdapter(this, R.layout.card_publications2, posts, this)
                    viewUserPost.findViewById<RecyclerView>(R.id.rvShowUserPosts).layoutManager =  LinearLayoutManager(this)
                    viewUserPost.findViewById<RecyclerView>(R.id.rvShowUserPosts).adapter = this.postAdapter
                    viewUserPost.findViewById<Button>(R.id.btn_goBackDialog).setOnClickListener(this)
                    loading.startLoading()
                    getUserofPost()


                }
                R.id.BtnLike ->{
                    if(RestEngine.hasInternetConnection(this)){
                        likeAnimation(R.raw.apple_event)
                    }else{
                        Toast.makeText(this, R.string.InternetErr, Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.BtnbackPost ->{
                    onBackPressed()
                }
                R.id.BtnModif ->{
                    val intent = Intent(this, CreatePostActivity::class.java)
                    post.albums = null
                    post.tags = null
                    intent.putExtra("post", post)
                    startActivity(intent)
                    this.overridePendingTransition(0, 0)
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
                R.id.btn_goBackDialog ->{
                    windowTagsPost.dismiss()
                }
            }
        }
    }

    private fun findingViews(){
        likebtn = findViewById(R.id.BtnLike)
        btnback = findViewById(R.id.BtnbackPost)
        btnModif = findViewById(R.id.BtnModif)
        imageProfilePost = findViewById(R.id.imageProfilePost)
        txtNamePost = findViewById(R.id.txtNamePost)
        DescPost = findViewById(R.id.DescPost)
        imagesPost = findViewById(R.id.imagesPost)
        btnLeftPost = findViewById(R.id.btnLeftPost)
        btnRightPost = findViewById(R.id.btnRightPost)
        txtCountPost = findViewById(R.id.txtCountPost)
        DescImagePost = findViewById(R.id.DescImagePost)
        txtTagsPost = findViewById(R.id.txtTagsPost)


        btnModif.setOnClickListener(this)
        likebtn.setOnClickListener(this)
        btnback.setOnClickListener(this)
        btnLeftPost.setOnClickListener(this)
        btnRightPost.setOnClickListener(this)
        imageProfilePost.setOnClickListener(this)

        if(loggedUser.getUser().email.equals(post.email)){
            btnModif.visibility = View.VISIBLE
            TitlePostPubication.setText(R.string.HeaderEditPost)
        }

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
        loading.isDismiss()

    }

    private fun getTags(){
        if(RestEngine.hasInternetConnection(this)){
            val service: ServiceTag =  RestEngine.getRestEngine().create(ServiceTag::class.java)
            val result: Call<List<Tag>> = service.getTagPost(post.id_publication!!)

            result.enqueue(object: Callback<List<Tag>> {
                override fun onFailure(call: Call<List<Tag>>, t: Throwable) {
                    loading.isDismiss()
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
        }else{
            val item = Tag().TagSQLite().GetPostTag(post.id_publication!!)
            if(item[0].id_tag != null){
                post.tags = item.toMutableList()
            }

            getAlbums()
        }

    }

    private fun getAlbums(){
        if(RestEngine.hasInternetConnection(this)){
            val service: ServiceAlbum =  RestEngine.getRestEngine().create(ServiceAlbum::class.java)
            val result: Call<List<Album>> = service.getAlbumPost(post.id_publication!!)

            result.enqueue(object: Callback<List<Album>> {
                override fun onFailure(call: Call<List<Album>>, t: Throwable) {
                    loading.isDismiss()
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
        }else{
            val item = Album().AlbumSQLite().GetAlbumPost(post.id_publication!!)
            if(item[0].id_album != null){
                post.albums = item.toMutableList()
            }
            searchLikedPost()
        }

    }

    private fun searchLikedPost(){
        if(RestEngine.hasInternetConnection(this)){
            val service: ServicePost =  RestEngine.getRestEngine().create(ServicePost::class.java)
            val result: Call<Int> = service.getLikedPost(post.id_publication!!, loggedUser.getUser().email!!)

            result.enqueue(object: Callback<Int> {
                override fun onFailure(call: Call<Int>, t: Throwable) {
                    loading.isDismiss()
                    Toast.makeText(this@PostActivity,t.message.toString(), Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    val item =  response.body()
                    if(item == 0) like = false
                    else if(item == 1) like = true
                    if(like){
                        likebtn.setAnimation(R.raw.apple_event)
                        likebtn.playAnimation()
                        likebtn.contentDescription = getString(R.string.DislikeButton)
                    }


                    fillPage()
                }
            })
        }else{
            val item = LikedPublication().LikedPublicationSQLite().GetUserLike(post.id_publication!!, loggedUser.getUser().email!!)
            if(item.id_liked_publications == null) like = false
            else like = true
            if(like){
                likebtn.setAnimation(R.raw.apple_event)
                likebtn.playAnimation()
                likebtn.contentDescription = getString(R.string.DislikeButton)
            }


            fillPage()
        }

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

    private fun getUserofPost(){
        if(RestEngine.hasInternetConnection(this)){
            val service: ServiceUser =  RestEngine.getRestEngine().create(ServiceUser::class.java)
            val result: Call<User> = service.getUser(post.email!!)

            result.enqueue(object: Callback<User> {
                override fun onFailure(call: Call<User>, t: Throwable) {
                    loading.isDismiss()
                    Toast.makeText(this@PostActivity,t.message.toString(), Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    val item =  response.body()

                    loadUserDataDialog(item!!)

                }
            })
        }else{
            val item = User().UserSQLite().GetUser(post.email!!)
            loadUserDataDialog(item)

        }
    }

    private fun loadUserDataDialog(item:User){
        val strImage:String =  item!!.image!!.replace("data:image/png;base64,","")
        val byteArray =  Base64.getDecoder().decode(strImage)
        viewUserPost.findViewById<CircleImageView>(R.id.ivImageUserShowPost).setImageBitmap(ImageUtils.getBitMapFromByteArray(byteArray))
        viewUserPost.findViewById<TextView>(R.id.tvNameUserShowPost).setText(item.fullname)
        if(!item.phone.isNullOrEmpty())
            viewUserPost.findViewById<TextView>(R.id.tvPhoneUserShowPost).setText(item.phone.toString())
        else
            viewUserPost.findViewById<TextView>(R.id.tvPhoneUserShowPost).setText(R.string.emptyPhone)
        if(!item.description.isNullOrEmpty())
            viewUserPost.findViewById<TextView>(R.id.tvDescUserShowPost).setText(item.description)
        else
            viewUserPost.findViewById<TextView>(R.id.tvDescUserShowPost).setText(R.string.emptyDesc)

        getUserPosts()
    }

    private fun getUserPosts(){
        if(RestEngine.hasInternetConnection(this)){
            val service: ServicePost =  RestEngine.getRestEngine().create(ServicePost::class.java)
            val result: Call<List<Publication>> = service.getUserPosts(post.email!!)

            result.enqueue(object: Callback<List<Publication>> {
                override fun onFailure(call: Call<List<Publication>>, t: Throwable) {
                    loading.isDismiss()
                    Toast.makeText(this@PostActivity,t.message.toString(), Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<List<Publication>>, response: Response<List<Publication>>) {
                    val item =  response.body()
                    if (item != null) {
                        for(p in item){
                            posts.add(p)
                        }
                    }
                    postAdapter!!.notifyDataSetChanged()
                    loading.isDismiss()

                }
            })
        }else{
            val item = Publication().PublicationSQLite().GetPostImageUser(post.email!!)
            for(p in item){
                posts.add(p)
            }
            postAdapter!!.notifyDataSetChanged()
            loading.isDismiss()

        }
    }

}