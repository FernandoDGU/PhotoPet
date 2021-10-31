package com.fcfm.photopet.controller.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_registro.*
import java.util.*

class Fragment_Perfil: Fragment(), View.OnClickListener{
    private lateinit var rootView: View
    private lateinit var editBtn: Button
    private lateinit var profileImg: CircleImageView
    private lateinit var profileName: TextView
    private lateinit var profileDescription: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profilePhone: TextView

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
        actualUser = loggedUser.getUser()
        loadData()

        editBtn.setOnClickListener(this)

        this.fillPostList()
        this.postAdapter =  PostListRecyclerAdapter(context, posts)
        recyclerPostSearch.adapter = this.postAdapter

        return rootView
    }

    private fun fillPostList(){
        var post: Publication? = null
        post = Publication(0,"Buenas tardes", "sadarien@gmail.com", null, R.drawable.background001)
        posts?.add(post)

        post = Publication(1,"Buenas noches", "sadarien3@gmail.com", null, R.drawable.puppy)
        posts?.add(post)

        post = Publication(2,"Buenas mañanas", "sadarien5@gmail.com", null, R.drawable.puppy2)
        posts?.add(post)

        post = Publication(3,"Buenas buenas", "sadarie2n@gmail.com", null, R.drawable.puppy)
        posts?.add(post)

        post = Publication(4,"Buenas buenas buenas", "sadarien1@gmail.com", null, R.drawable.puppy2)
        posts?.add(post)

        post = Publication(4,"sdfgdsfgsdfg", "sadariesdfgsdfgn1@gmail.com", null, R.drawable.background001)
        posts?.add(post)

        post = Publication(4,"sdfgsdfgsdf", "sadariendsfgsdag1@gmail.com", null, R.drawable.puppy)
        posts?.add(post)
    }

    override fun onClick(v: View?) {
        if(v!=null){
            when (v!!.id){
                R.id.btnEdit ->{
                    showModifyUser()
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
        var byteArray:ByteArray? = null
        val strImage:String =  actualUser.image!!.replace("data:image/png;base64,","")
        byteArray =  Base64.getDecoder().decode(strImage)
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