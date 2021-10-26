package com.fcfm.photopet.controller.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.photopet.R
import com.fcfm.photopet.controller.Fragment.Fragment_Inicio
import com.fcfm.photopet.model.Publication
import com.fcfm.photopet.utils.ImageUtils

class HomeRecyclerAdapter (private val context: Fragment_Inicio,var posts: MutableList<Publication>, private val itemClickListener: OnPostClickListenener): RecyclerView.Adapter<HomeRecyclerAdapter.HomeRecyclerViewHolder>()
    {
    inner class HomeRecyclerViewHolder(View: View): RecyclerView.ViewHolder(View){

    }

    interface OnPostClickListenener{
        //fun onitemHold(toString: String)
        fun onitemClick(post: Publication)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerViewHolder {
        val vistaMuro = LayoutInflater.from(parent.context).inflate(R.layout.card_home, parent, false)

        return HomeRecyclerViewHolder(vistaMuro)
    }

    override fun onBindViewHolder(holder: HomeRecyclerViewHolder, position: Int) {
        holder.itemView.setOnClickListener { itemClickListener.onitemClick((posts[position])) }
        val description = holder.itemView.findViewById<TextView>(R.id.textHomecard)
        val image = holder.itemView.findViewById<ImageView>(R.id.imageHomecard)

        description.text = posts[position].description
        //image.text = posts[position].intIdImage
        if(posts[position].imgArray == null){
            image.setImageResource(posts[position].intIdImage!!)
        }else{
            image.setImageBitmap(ImageUtils.getBitMapFromByteArray(posts[position].imgArray!!))
        }
        //Picasso.get().load(listaPublicacion[position].foto).into(Muro_Img_Perfil)

    }

}