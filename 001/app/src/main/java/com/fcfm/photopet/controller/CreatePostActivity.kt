package com.fcfm.photopet.controller

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.photopet.R
import com.fcfm.photopet.controller.Adapter.TagListRecyclerAdapter
import com.fcfm.photopet.model.Album
import com.fcfm.photopet.model.Tag
import com.fcfm.photopet.utils.loggedUser
import kotlinx.android.synthetic.main.activity_crearpublicacion.*
import kotlinx.android.synthetic.main.activity_crearpublicacion.view.*
import kotlinx.android.synthetic.main.activity_registro.*
import kotlinx.android.synthetic.main.dialog_tags_publication.*
import kotlinx.android.synthetic.main.dialog_tags_publication.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import com.fcfm.photopet.model.Publication
import java.util.*
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.fcfm.photopet.controller.Fragment.Fragment_Perfil
import com.fcfm.photopet.utils.ImageUtils
import com.fcfm.photopet.utils.LoadingDialog
import com.fcfm.photopet.utils.UserApplication
import com.fcfm.photopet.utils.retrofit.*
import com.google.android.material.textfield.MaterialAutoCompleteTextView


class CreatePostActivity: AppCompatActivity(),View.OnFocusChangeListener, View.OnClickListener, TagListRecyclerAdapter.OnTagClickListenener, TagListRecyclerAdapter.OnDeleteClickListener {
    lateinit var viewTagsPost: View
    lateinit var windowTagsPost: Dialog
    lateinit var autoCompleteTag: MaterialAutoCompleteTextView
    lateinit var autoTagListAdapter: ArrayAdapter<Tag>
    var imagePos:Int = 0
    var tagList: MutableList<Tag> = mutableListOf()
    var albumList: MutableList<Album> = mutableListOf()
    var selectedTagList: MutableList<Tag> = mutableListOf()
    var post = Publication()
    private var tagListAdapter = TagListRecyclerAdapter(this, selectedTagList,this, this)
    private lateinit var loading : LoadingDialog.ActivityLD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crearpublicacion)
        supportActionBar?.hide()
        viewTagsPost = layoutInflater.inflate(R.layout.dialog_tags_publication, null);
        windowTagsPost = Dialog(this)
        btnPublicar.setOnClickListener(this)
        btnTagsPost.setOnClickListener(this)
        BtnbackPostC.setOnClickListener(this)
        floatbtnAddImage.setOnClickListener(this)
        btnLeft.setOnClickListener(this)
        btnRight.setOnClickListener(this)
        btnDeleteImage.setOnClickListener(this)
        btnGuardarDesc.setOnClickListener(this)

        textDescImage.onFocusChangeListener = this

        loading = LoadingDialog().ActivityLD(this)

        if(intent.extras != null){
            loading.startLoading()
            btnPublicar.visibility = View.GONE
            btnModif.visibility = View.VISIBLE
            btnDeletePost.visibility = View.VISIBLE
            post = intent.getSerializableExtra("post") as Publication
            btnModif.setOnClickListener(this)
            btnDeletePost.setOnClickListener(this)
            getTags()


        }

    }

    override fun onClick(v: View?) {
        if(v!=null){
            when (v.id){
                R.id.btnPublicar ->{
                    if(RestEngine.hasInternetConnection(this)){
                        val postDesc = textDescCreatePost.text.toString();
                        if(postDesc.isEmpty()){
                            Toast.makeText(this, R.string.postDescEmptyErr, Toast.LENGTH_SHORT).show()
                            return
                        }else if(albumList.isEmpty()){
                            Toast.makeText(this, R.string.postEmptyErr, Toast.LENGTH_SHORT).show()
                            return
                        }else {
                            for(a in albumList){
                                val encodedString: String =  Base64.getEncoder().encodeToString(a.image)
                                val strEncodeImage: String = "data:image/png;base64," + encodedString
                                a.imageString = strEncodeImage;
                                if(a.description.isNullOrEmpty()){
                                    //a.description = R.string.emptyDescImage.toString()
                                    a.description = ""
                                }
                            }
                            post.description = postDesc
                            post.albums = albumList
                            post.tags = selectedTagList
                            post.email = loggedUser.getUser().email

                            val service: ServicePost =  RestEngine.getRestEngine().create(ServicePost::class.java)
                            val result: Call<RetrofitMessage> = service.insertPost(post)

                            result.enqueue(object: Callback<RetrofitMessage> {
                                override fun onFailure(call: Call<RetrofitMessage>, t: Throwable) {

                                    Toast.makeText(this@CreatePostActivity,t.message.toString(),Toast.LENGTH_LONG).show()
                                }

                                override fun onResponse(call: Call<RetrofitMessage>, response: Response<RetrofitMessage>) {
                                    val item =  response.body()
                                    when(item!!.message){
                                        "ok" -> {
                                            showPost()

                                        }
                                        else -> {
                                            Toast.makeText(this@CreatePostActivity, item.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                }
                            })

                        }
                    }else{
                        Toast.makeText(this, R.string.InternetErr, Toast.LENGTH_SHORT).show()
                    }

                    //showPost()
                }
                R.id.btnModif -> {
                    if(RestEngine.hasInternetConnection(this)){
                        val postDesc = textDescCreatePost.text.toString();
                        if(postDesc.isEmpty()){
                            Toast.makeText(this, R.string.postDescEmptyErr, Toast.LENGTH_SHORT).show()
                            return
                        }else if(albumList.isEmpty()){
                            Toast.makeText(this, R.string.postEmptyErr, Toast.LENGTH_SHORT).show()
                            return
                        }else {
                            for(a in albumList){
                                val encodedString: String =  Base64.getEncoder().encodeToString(a.image)
                                val strEncodeImage: String = "data:image/png;base64," + encodedString
                                a.imageString = strEncodeImage;
                                if(a.description.isNullOrEmpty()){
                                    //a.description = R.string.emptyDescImage.toString()
                                    a.description = ""
                                }
                            }
                            post.description = postDesc
                            post.albums = albumList
                            post.tags = selectedTagList
                            post.email = loggedUser.getUser().email

                            val service: ServicePost =  RestEngine.getRestEngine().create(ServicePost::class.java)
                            val result: Call<RetrofitMessage> = service.updatePost(post)

                            result.enqueue(object: Callback<RetrofitMessage> {
                                override fun onFailure(call: Call<RetrofitMessage>, t: Throwable) {

                                    Toast.makeText(this@CreatePostActivity,t.message.toString(),Toast.LENGTH_LONG).show()
                                }

                                override fun onResponse(call: Call<RetrofitMessage>, response: Response<RetrofitMessage>) {
                                    val item =  response.body()
                                    when(item!!.message){
                                        "ok" -> {
                                            showPost()
                                        }
                                        else -> {
                                            Toast.makeText(this@CreatePostActivity, item.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                }
                            })
                        }
                    }else{
                        Toast.makeText(this, R.string.InternetErr, Toast.LENGTH_SHORT).show()
                    }

                }
                R.id.btnDeletePost->{
                    if(RestEngine.hasInternetConnection(this)){
                        deleteDialog()
                    }else{
                        Toast.makeText(this, R.string.InternetErr, Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.btnTagsPost ->{
                    windowTagsPost.setContentView(viewTagsPost)
                    autoCompleteTag = viewTagsPost.findViewById<MaterialAutoCompleteTextView>(R.id.autotv_Tag)


                    autoCompleteTag.setOnItemClickListener { parent, view, position, id ->
                        var selectedTag = parent!!.getItemAtPosition(position) as Tag
                        selectedTagList.add(selectedTag);
                        tagListAdapter.notifyDataSetChanged()
                        autoCompleteTag.text = null

                        tagList.remove(selectedTag)
                        autoTagListAdapter.remove(selectedTag)
                        autoCompleteTag.setAdapter(autoTagListAdapter)
                    }
                    val recycler_tagList = viewTagsPost.findViewById<RecyclerView>(R.id.rv_tagList)
                    recycler_tagList.layoutManager = LinearLayoutManager(this)
                    recycler_tagList.adapter = tagListAdapter



                    windowTagsPost.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                    val window = windowTagsPost.window
                    window!!.setGravity(Gravity.CENTER)
                    windowTagsPost.show()
                    viewTagsPost.btn_createtag.setOnClickListener(this)

                    fillTagList()
                }
                R.id.btn_createtag ->{
                    val inputText = autoCompleteTag.text.toString()

                    if(inputText.isEmpty())
                        return

                    if(RestEngine.hasInternetConnection(this)){
                        var alreadyExists = false
                        for (tag in selectedTagList){
                            if(tag.tagname!!.lowercase() == inputText.lowercase()){
                                alreadyExists = true;
                                break;
                            }
                        }
                        if(!alreadyExists){
                            for (tag in tagList){
                                if(tag.tagname!!.lowercase() == inputText.lowercase()){
                                    alreadyExists = true;
                                    break;
                                }
                            }
                        }

                        if(!alreadyExists){
                            insertTag(Tag(null, inputText))
                        }else{
                            Toast.makeText(this, R.string.tagNameErr, Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, R.string.InternetErr, Toast.LENGTH_SHORT).show()
                    }



                }
                R.id.floatbtnAddImage -> {
                    if(albumList.size != 10)
                        openGalleryForImages()
                    else
                        Toast.makeText(this, R.string.imagesOverloadErr, Toast.LENGTH_SHORT).show()
                }
                R.id.btnLeft ->{
                    if(albumList.isEmpty())
                        return

                    if(imagePos == 1){
                        imagePos = albumList.size
                    }else{
                        imagePos--
                    }
                    val index = imagePos - 1
                    val imageData = albumList[index].image
                    val bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData!!.size)
                    imagesCreatePost.setImageBitmap(bmp)
                    txtCount.text = "${imagePos}/10"
                    textDescImage.setText(albumList[index].description)

                }
                R.id.btnRight ->{
                    if(albumList.isEmpty())
                        return

                    if(imagePos == albumList.size){
                        imagePos = 1
                    }else{
                        imagePos++
                    }
                    val index = imagePos - 1
                    val imageData = albumList[index].image
                    val bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData!!.size)
                    imagesCreatePost.setImageBitmap(bmp)
                    txtCount.text = "${imagePos}/10"
                    textDescImage.setText(albumList[index].description)

                }
                R.id.btnDeleteImage ->{
                    if(albumList.isEmpty())
                        return

                    var index = imagePos - 1
                    if(albumList.size == 1){
                        imagesCreatePost.setImageResource(R.drawable.picture) //PONER IMAGEN DEFAULT
                        albumList.removeAt(index)
                        imagePos = 0
                        textDescImage.text = null
                    } else{
                        albumList.removeAt(index)
                        if(imagePos == albumList.size + 1){
                            imagePos--
                            index = imagePos - 1
                        }

                        val imageData = albumList[index].image
                        val bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData!!.size)
                        imagesCreatePost.setImageBitmap(bmp)

                        txtCount.text = "${imagePos}/10"
                        textDescImage.setText(albumList[index].description)

                    }
                }
                R.id.btnGuardarDesc -> {
                    val descriptionImage = textDescImage.text.toString() //Botón guardar descripción
                    if(descriptionImage.isEmpty() || imagePos == 0)
                        return
                    val index = imagePos - 1;
                    albumList[index].description = descriptionImage
                    Toast.makeText(this, "Descripción guardada", Toast.LENGTH_SHORT).show()
                }

                R.id.BtnbackPostC ->{
                    onBackPressed()
                }
            }
        }
    }

    private fun insertTag(tag: Tag){
        val service: ServiceTag =  RestEngine.getRestEngine().create(ServiceTag::class.java)
        val result: Call<RetrofitMessage> = service.insertTag(tag)

        result.enqueue(object: Callback<RetrofitMessage> {
            override fun onFailure(call: Call<RetrofitMessage>, t: Throwable) {

                Toast.makeText(this@CreatePostActivity,t.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<RetrofitMessage>, response: Response<RetrofitMessage>) {
                val item =  response.body()
                when(item!!.message){
                    "ok" -> {
                        selectedTagList.add(tag)
                        tagListAdapter.notifyDataSetChanged()
                        autoCompleteTag.text = null
                        fillTagList()
                    }
                    else -> {
                        Toast.makeText(this@CreatePostActivity, item.message, Toast.LENGTH_SHORT).show()
                    }
                }

            }
        })
    }


    private fun showPost(){
        val intent = Intent(this, FragmentsActivity::class.java)
        startActivity(intent)
    }

    private fun fillTagList(){
        tagList.clear()
        val service: ServiceTag =  RestEngine.getRestEngine().create(ServiceTag::class.java)
        val result: Call<List<Tag>> = service.getTags()

        result.enqueue(object: Callback<List<Tag>> {
            override fun onFailure(call: Call<List<Tag>>, t: Throwable) {
                //loading.isDismiss()
                Toast.makeText(this@CreatePostActivity,t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Tag>>, response: Response<List<Tag>>) {
                val item =  response.body()
                if(item!![0].id_tag != null){

                    tagList = item.toMutableList()

                    for(tag in selectedTagList){
                        for(innerTag in tagList){
                            if(innerTag.tagname!!.lowercase() == tag.tagname!!.lowercase()){
                                if(tag.id_tag == null){
                                    selectedTagList.remove(tag)
                                    selectedTagList.add(innerTag)
                                }
                                tagList.remove(innerTag)
                                break
                            }

                        }
                    }
                    autoTagListAdapter = ArrayAdapter(viewTagsPost.context, android.R.layout.simple_list_item_1, tagList)
                    autoCompleteTag.setAdapter(autoTagListAdapter)
                }

            }
        })


    }

    override fun onitemClick(tag: Tag) {
        Toast.makeText(this, tag.tagname, Toast.LENGTH_SHORT).show()
    }

    override fun ondeleteitemClick(tag: Tag) {
        selectedTagList.remove(tag)
        tagListAdapter.notifyDataSetChanged()
        tagList.add(tag)
        autoTagListAdapter.add(tag)
        autoCompleteTag.setAdapter(autoTagListAdapter)
    }

    private fun openGalleryForImages(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, 200);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 200){
            var album:Album
            var photo: Bitmap? = null
            var stream:ByteArrayOutputStream
            
            if (data?.getClipData() != null){ //Múltiples imágenes
                for (i in 0 until data.clipData!!.itemCount){
                    album = Album()
                    photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.clipData!!.getItemAt(i).uri)
                    stream = ByteArrayOutputStream()
                    photo.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                    album.image = stream.toByteArray()

                    albumList.add(album);
                }
                imagePos = albumList.size
                txtCount.text = "${imagePos}/10"
                imagesCreatePost.setImageBitmap(photo)
                textDescImage.text = null

            }else{ //Una imagen
                album = Album()
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data!!.data)
                stream = ByteArrayOutputStream()
                photo.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                album.image = stream.toByteArray()

                albumList.add(album)
                imagePos = albumList.size
                txtCount.text = "${imagePos}/10"
                imagesCreatePost.setImageBitmap(photo)
                textDescImage.text = null
            }
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if(!hasFocus){
            //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            val descriptionImage = textDescImage.text.toString() //Botón guardar descripción
            if(descriptionImage.isEmpty() || imagePos == 0)
                return
            val index = imagePos - 1;
            albumList[index].description = descriptionImage
        }


    }

    private fun getTags(){
        val service: ServiceTag =  RestEngine.getRestEngine().create(ServiceTag::class.java)
        val result: Call<List<Tag>> = service.getTagPost(post.id_publication!!)

        result.enqueue(object: Callback<List<Tag>> {
            override fun onFailure(call: Call<List<Tag>>, t: Throwable) {
                loading.isDismiss()
                Toast.makeText(this@CreatePostActivity,t.message.toString(), Toast.LENGTH_LONG).show()
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
                loading.isDismiss()
                Toast.makeText(this@CreatePostActivity,t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Album>>, response: Response<List<Album>>) {
                val item =  response.body()
                if(item!![0].id_album != null){
                    for(a in item){
                        var strImage:String =  a.imageString!!.replace("data:image/png;base64,","")
                        var byteArray =  Base64.getDecoder().decode(strImage)
                        a.image = byteArray
                        albumList.add(a)
                    }
                    //albumList = item.toMutableList()
                }
                loadData()
            }
        })
    }

    private fun loadData(){
        val imageData = albumList[0].image
        val bmp = BitmapFactory.decodeByteArray(imageData, 0, imageData!!.size)
        imagesCreatePost.setImageBitmap(bmp)

        textDescCreatePost.setText(post.description)
        if(albumList[0].description!!.isNotEmpty()){
            textDescImage.setText(albumList[0].description!!)
        }

        imagePos = 1;
        txtCount.text = "${imagePos}/10"

        for(t in post.tags!!){
            selectedTagList.add(t)
        }

        tagListAdapter.notifyDataSetChanged()
        loading.isDismiss()
    }

    private fun deleteDialog() {

        val simpleDialog =  AlertDialog.Builder(this)
            .setTitle(getString(R.string.deletePost))
            .setMessage(getString(R.string.dialogDeletePostMessage))
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton(getString(R.string.dialogYes)){ _,_ ->
                deletePost()

            }
            .setNegativeButton(getString(R.string.dialogNo)){_,_->


            }.create()

        simpleDialog.show()
    }

    private fun deletePost(){
        loading.startLoading()
        val service: ServicePost =  RestEngine.getRestEngine().create(ServicePost::class.java)
        val result: Call<RetrofitMessage> = service.deletePost(post.id_publication!!)

        result.enqueue(object: Callback<RetrofitMessage> {
            override fun onFailure(call: Call<RetrofitMessage>, t: Throwable) {
                loading.isDismiss()
                Toast.makeText(this@CreatePostActivity,t.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<RetrofitMessage>, response: Response<RetrofitMessage>) {
                val item =  response.body()
                when(item!!.message){
                    "ok" -> {
                        loading.isDismiss()
                        val intent = Intent(this@CreatePostActivity, FragmentsActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    else -> {
                        loading.isDismiss()
                        Toast.makeText(this@CreatePostActivity,item.message,Toast.LENGTH_LONG).show()
                    }
                }

            }
        })
    }

}