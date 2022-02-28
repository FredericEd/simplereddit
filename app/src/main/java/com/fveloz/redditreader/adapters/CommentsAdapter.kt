package com.fveloz.redditreader.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.fveloz.redditreader.R
import com.fveloz.redditreader.models.Comment
import java.text.SimpleDateFormat

class CommentsAdapter (private var commentList: List<Comment>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CommentHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false))
    }

    override fun onBindViewHolder(genericHolder: RecyclerView.ViewHolder, position: Int) {
        if ( position >= commentList.size ) return
        val holder = genericHolder as CommentHolder
        val comment = commentList[position]
        holder.fillFields(comment)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    fun setData(commentList: List<Comment>) {
        this.commentList = commentList
        notifyDataSetChanged()
    }
}

class CommentHolder(val view: View):
    RecyclerView.ViewHolder(view) {

    private val textContent: TextView = view.findViewById(R.id.textContent)
    private val textAuthor: TextView = view.findViewById(R.id.textAuthor)
    private val textDate: TextView = view.findViewById(R.id.textDate)
    private val textUps: TextView = view.findViewById(R.id.textUps)
    private val textDowns: TextView = view.findViewById(R.id.textDowns)

    fun fillFields(comment: Comment) {
        textContent.text = comment.content
        textAuthor.text = comment.author
        textDate.text = SimpleDateFormat("MMMM d HH:mm").format(comment.date.time)
        textUps.text = comment.ups.toString()
        textDowns.text = comment.downs.toString()
        textContent.isVisible = comment.content.isNotBlank()
    }
}
