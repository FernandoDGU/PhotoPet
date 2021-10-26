package com.fcfm.photopet.controller.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.photopet.R
import com.fcfm.photopet.model.Publication
import com.fcfm.photopet.utils.ImageUtils

class PostListRecyclerAdapter(val context: Context?, var posts: MutableList<Publication>): RecyclerView.Adapter<PostListRecyclerAdapter.ViewHolder>(),
    Filterable {

    private  val layoutInflater =  LayoutInflater.from(context)
    private val fullAlbums =  posts

    //se hace cargo de los graficos
    inner class  ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val txtAuthor = itemView?.findViewById<TextView>(R.id.txtAuthor)
        val txtDescription =  itemView?.findViewById<TextView>(R.id.txtDesc)
        val imgAlbumCard =  itemView?.findViewById<ImageView>(R.id.imagePost)
        var albumPosition:Int =  0

        init{
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {

            when(v!!.id){
                R.id.FrameLayoutPostList->{
                    Toast.makeText(context, this.albumPosition.toString(), Toast.LENGTH_SHORT).show()
                    //Lanzamos el intent para abrir el detall
                    //val  activityIntent =  Intent(context,AlbumActivity::class.java)
                    //activityIntent.putExtra(ALBUM_POSITION,this.albumPosition)
                    //context.startActivity(activityIntent)
                }
            }

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =  this.layoutInflater.inflate(R.layout.card_publications,parent,false)
        return  ViewHolder(itemView)
    }


    override fun getItemCount(): Int  =  this.posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val post =  this.posts[position]
        holder.txtAuthor.text =  post.email
        holder.txtDescription.setText(post.description)
        holder.albumPosition =  position
        //holder.imgAlbumCard.setImageBitmap(ImageUtilities.getBitMapFromByteArray(album.imgArray!!))

        if(post.imgArray == null){
            holder.imgAlbumCard.setImageResource(post.intIdImage!!)
        }else{
            holder.imgAlbumCard.setImageBitmap(ImageUtils.getBitMapFromByteArray(post.imgArray!!))
        }

    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(charSequence: CharSequence?): FilterResults {

                //Obtenemos la cadena
                val filterResults = Filter.FilterResults()
                filterResults.values =  if (charSequence == null || charSequence.isEmpty()){

                    fullAlbums

                }else{
                    val queryString = charSequence.toString().lowercase()



                    posts.filter { post ->

                        post.description!!.lowercase().contains(queryString) //|| post.strDescription!!.toLowerCase().contains(queryString)
                    }
                }

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                posts =  results?.values as MutableList<Publication>
                notifyDataSetChanged()
            }

        }
    }


}