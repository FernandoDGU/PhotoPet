package com.fcfm.photopet.controller.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fcfm.photopet.R
import com.fcfm.photopet.controller.CreatePostActivity
import com.fcfm.photopet.model.Publication
import com.fcfm.photopet.model.Tag
import kotlinx.android.synthetic.main.drawer_taglist.view.*

class TagListRecyclerAdapter(private val context: CreatePostActivity, var tags: MutableList<Tag>, private val itemClickListener: OnTagClickListenener, private val deleteClickListener: OnDeleteClickListener): RecyclerView.Adapter<TagListRecyclerAdapter.TagListViewHolder>() {

    inner class TagListViewHolder(View: View): RecyclerView.ViewHolder(View){

    }


    interface OnTagClickListenener{
        //fun onitemHold(toString: String)
        fun onitemClick(tag: Tag)
    }
    interface OnDeleteClickListener{
        //fun onitemHold(toString: String)
        fun ondeleteitemClick(tag: Tag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagListViewHolder {
        val viewRecycler = LayoutInflater.from(parent.context).inflate(R.layout.drawer_taglist, parent, false)

        return TagListViewHolder(viewRecycler)
    }

    override fun onBindViewHolder(holder: TagListViewHolder, position: Int) {
        holder.itemView.setOnClickListener { itemClickListener.onitemClick((tags[position])) }
        holder.itemView.btn_delete_tag.setOnClickListener { deleteClickListener.ondeleteitemClick(tags[position]) }
        val tagname = holder.itemView.findViewById<TextView>(R.id.txt_tagname)

        tagname.text = tags[position].tagname
    }

    override fun getItemCount(): Int {
        return tags.size
    }
}