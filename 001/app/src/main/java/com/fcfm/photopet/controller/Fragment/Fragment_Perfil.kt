package com.fcfm.photopet.controller.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.photopet.R
import com.fcfm.photopet.controller.Adapter.PostListRecyclerAdapter
import com.fcfm.photopet.controller.RegisterActivity
import com.fcfm.photopet.model.Publication
import com.fcfm.photopet.model.User
import com.fcfm.photopet.utils.ImageUtils
import com.fcfm.photopet.utils.loggedUser
import com.fcfm.photopet.utils.retrofit.RestEngine
import com.fcfm.photopet.utils.retrofit.ServicePost
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_registro.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class Fragment_Perfil: Fragment(), View.OnClickListener{
    private lateinit var rootView: View
    private lateinit var editBtn: Button
    private lateinit var profileImg: CircleImageView
    private lateinit var profileName: TextView
    private lateinit var profileDescription: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profilePhone: TextView
    private lateinit var Myposts: RadioButton
    private lateinit var Mylikes: RadioButton

    private lateinit var actualUser: User

    private var postAdapter: PostListRecyclerAdapter? = null

    private var posts: MutableList<Publication> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.fragment_perfil, container, false)
        val recyclerPostSearch = rootView.findViewById<RecyclerView>(R.id.rvProfileCards)
        recyclerPostSearch.layoutManager =  LinearLayoutManager(context)
        editBtn = rootView.findViewById<Button>(R.id.btnEdit)
        Myposts = rootView.findViewById<RadioButton>(R.id.Myposts)
        Mylikes = rootView.findViewById<RadioButton>(R.id.Mylikes)
        actualUser = loggedUser.getUser()
        loadData()

        editBtn.setOnClickListener(this)
        Myposts.setOnClickListener(this)
        Mylikes.setOnClickListener(this)

        this.fillPostList(true)
        this.postAdapter =  PostListRecyclerAdapter(context, posts, activity)
        recyclerPostSearch.adapter = this.postAdapter

        return rootView
    }

    private fun fillPostList(order:Boolean){//1 My posts, 0 My likes
        val service: ServicePost =  RestEngine.getRestEngine().create(ServicePost::class.java)
        val result: Call<List<Publication>> = if(order)
            service.getUserPosts(loggedUser.getUser().email!!)
        else
            service.getUserLikedPosts(loggedUser.getUser().email!!, 1)

        result.enqueue(object: Callback<List<Publication>> {
            override fun onFailure(call: Call<List<Publication>>, t: Throwable) {
                //loading.isDismiss()
                Toast.makeText(context,t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Publication>>, response: Response<List<Publication>>) {
                posts.clear()
                val item =  response.body()
                if(item!![0].id_publication != null){
                    for(p in item!!){
                        posts.add(p)
                    }
                    postAdapter!!.notifyDataSetChanged()
                }

            }
        })
    }

    override fun onClick(v: View?) {
        if(v!=null){
            when (v.id){
                R.id.btnEdit ->{
                    showModifyUser()
                }
                R.id.Myposts ->{
                    this.fillPostList(true)
                }
                R.id.Mylikes ->{
                    this.fillPostList(false)
                }

            }
        }
    }

    private fun showModifyUser(){
        val intent = Intent(context, RegisterActivity::class.java)
        intent.putExtra("destiny", "modify");
        startActivity(intent)
        activity?.overridePendingTransition(0, 0)
    }

    private fun loadData(){
        profileImg = rootView.findViewById<CircleImageView>(R.id.imageProfilePost)
        profileName = rootView.findViewById<TextView>(R.id.txtNameProfile)
        profileDescription = rootView.findViewById<TextView>(R.id.txtDescProfile)
        profileEmail = rootView.findViewById<TextView>(R.id.txtEmailProfile)
        profilePhone = rootView.findViewById<TextView>(R.id.txtNumberProfile)
        loadUserData()

    }

    private fun loadUserData(){
        val strImage:String =  actualUser.image!!.replace("data:image/png;base64,","")
        val byteArray =  Base64.getDecoder().decode(strImage)
        profileImg.setImageBitmap(ImageUtils.getBitMapFromByteArray(byteArray))
        profileName.setText(actualUser.fullname)
        profileEmail.setText(actualUser.email)
        if(!actualUser.phone.isNullOrEmpty()) profilePhone.setText(actualUser.phone) else profilePhone.setText(R.string.emptyPhone)
        if(!actualUser.description.isNullOrEmpty()) profileDescription.setText(actualUser.description) else profileDescription.setText(R.string.emptyDesc)
    }

    override fun onResume() {
        super.onResume()
        actualUser = loggedUser.getUser()
        loadUserData()
    }


}