package com.fcfm.photopet.controller.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.photopet.R
import com.fcfm.photopet.controller.PostActivity
import com.fcfm.photopet.model.Publication
import com.fcfm.photopet.utils.ImageUtils
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class PostListRecyclerAdapter(val context: Context?, var posts: MutableList<Publication>, val activity: Activity?): RecyclerView.Adapter<PostListRecyclerAdapter.ViewHolder>(),
    Filterable {

    private  val layoutInflater =  LayoutInflater.from(context)
    private val fullAlbums =  posts

    //se hace cargo de los graficos
    inner class  ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val txtAuthor = itemView?.findViewById<TextView>(R.id.txtAuthor)
        val txtDescription =  itemView?.findViewById<TextView>(R.id.txtDesc)
        val imgAlbumCard =  itemView?.findViewById<ImageView>(R.id.imagePost)
        val ivAuthor =  itemView?.findViewById<CircleImageView>(R.id.iv_author)
        var postPosition:Int =  0

        init{
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {

            when(v!!.id){
                R.id.FrameLayoutPostList->{
                    //Toast.makeText(context, this.albumPosition.toString(), Toast.LENGTH_SHORT).show()
                    //Lanzamos el intent para abrir el detall
                    //val  activityIntent =  Intent(context,AlbumActivity::class.java)
                    //activityIntent.putExtra(ALBUM_POSITION,this.albumPosition)
                    //context.startActivity(activityIntent)
                    val intent = Intent(context, PostActivity::class.java)
                    intent.putExtra("post", posts[postPosition])
                    context!!.startActivity(intent)
                    activity?.overridePendingTransition(0, 0)
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

            val post = this.posts[position]
            holder.txtAuthor.text = post.author
            holder.txtDescription.setText(post.description)
            holder.postPosition = position
            //holder.imgAlbumCard.setImageBitmap(ImageUtilities.getBitMapFromByteArray(album.imgArray!!))

            var strImage: String = posts[position].imgArray!!.replace("data:image/png;base64,", "")
            var byteArray = Base64.getDecoder().decode(strImage)
            holder.imgAlbumCard.setImageBitmap(ImageUtils.getBitMapFromByteArray(byteArray))
            strImage= posts[position].authorImage!!.replace("data:image/png;base64,", "")
            byteArray = Base64.getDecoder().decode(strImage)
            holder.ivAuthor.setImageBitmap(ImageUtils.getBitMapFromByteArray(byteArray))

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