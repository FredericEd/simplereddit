package com.fveloz.redditreader.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.fveloz.redditreader.R
import com.fveloz.redditreader.models.Post
import java.lang.Exception
import java.text.SimpleDateFormat

class PostsAdapter  (private var postList: List<Post>, private val observer: BasicUIObserver<Post>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_VIEW_TYPE_LOAD_MORE = 0
    val ITEM_VIEW_TYPE_POST = 1
    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_POST -> PostHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false), observer)
            else -> ProgressViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.progress_item, parent, false))
        }
    }

    override fun onBindViewHolder(genericHolder: RecyclerView.ViewHolder, position: Int) {
        if (genericHolder is ProgressViewHolder || position >= postList.size ) return
        val holder = genericHolder as PostHolder
        val post = postList[position]
        holder.fillFields(post)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (holder is ProgressViewHolder) observer.onApproachingEnd()
    }

    override fun getItemCount(): Int {
        return postList.size + 1
    }

    fun setData(postList: List<Post>) {
        this.postList = postList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        try {
            return if (position >= postList.size) ITEM_VIEW_TYPE_LOAD_MORE else ITEM_VIEW_TYPE_POST
        } catch (e: Exception) {
            return ITEM_VIEW_TYPE_LOAD_MORE
        }
    }
}

class PostHolder(val view: View, val observer: BasicUIObserver<Post>):
    RecyclerView.ViewHolder(view) {

    private val textTitle: TextView = view.findViewById(R.id.textTitle)
    private val textContent: TextView = view.findViewById(R.id.textContent)
    private val textAuthor: TextView = view.findViewById(R.id.textAuthor)
    private val textDate: TextView = view.findViewById(R.id.textDate)
    private val textComments: TextView = view.findViewById(R.id.textComments)
    private val textUps: TextView = view.findViewById(R.id.textUps)
    private val textDowns: TextView = view.findViewById(R.id.textDowns)
    private val imgShare: ImageView = view.findViewById(R.id.imgShare)

    fun fillFields(post: Post) {
        textTitle.text = post.title
        textContent.text = post.content
        textAuthor.text = post.author
        textDate.text = SimpleDateFormat("MMMM d HH:mm").format(post.date.time)
        textComments.text = post.num_comments.toString()
        textUps.text = post.ups.toString()
        textDowns.text = post.downs.toString()
        textContent.isVisible = post.content.isNotBlank()
        view.setOnClickListener {
            observer.onElementClicked(post)
        }
        imgShare.setOnClickListener{
            observer.onShareClicked(post, imgShare)
        }
    }
}
