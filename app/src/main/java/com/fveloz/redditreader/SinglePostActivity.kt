package com.fveloz.redditreader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fveloz.redditreader.adapters.CommentsAdapter
import com.fveloz.redditreader.databinding.ActivityPostBinding
import com.fveloz.redditreader.models.Comment
import com.fveloz.redditreader.models.Post
import com.fveloz.redditreader.utilities.ResultState
import com.fveloz.redditreader.viewmodels.GeneralViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.lang.Exception
import java.text.SimpleDateFormat

@ExperimentalCoroutinesApi
class SinglePostActivity : AppCompatActivity() {

    private var _binding: ActivityPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : CommentsAdapter
    private val viewModel: GeneralViewModel by viewModels()

    private var commentList: ArrayList<Comment> = arrayListOf()
    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val typePost = object : TypeToken<Post>() {}.type
        post =  Gson().fromJson(intent.extras!!.getString("data"), typePost)

        adapter = CommentsAdapter(commentList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this@SinglePostActivity)
        binding.recyclerView.adapter = adapter

        lifecycleScope.launchWhenCreated {
            viewModel.isLoading.collect{
                binding.progressView.isVisible = it
                binding.contentView.isVisible = !it
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.commentList.collect {
                if (it.size > 0) {
                    commentList = it
                    adapter.setData(commentList)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.getCommentListState.collect{
                when (it) {
                    is ResultState.Empty ->{
                        binding.textEmpty.isVisible = true
                        binding.recyclerView.isVisible = false
                    }
                    is  ResultState.Error ->{
                        binding.textEmpty.text = it.message
                        binding.textEmpty.isVisible = true
                        binding.recyclerView.isVisible = false
                    }
                    is ResultState.Success -> {
                        binding.textEmpty.isVisible = false
                        binding.recyclerView.isVisible = true
                    }
                }
            }
        }
        fillData(post)
        viewModel.getCommentsByPost(post.subreddit, post.id)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fillData(post: Post) {
        binding.textTitle.text = post.title
        binding.textContent.text = post.content
        binding.textAuthor.text = post.author
        binding.textDate.text = SimpleDateFormat("MMMM d HH:mm").format(post.date.time)
        binding.textUps.text = post.ups.toString()
        binding.textDowns.text = post.downs.toString()
        binding.textContent.isVisible = post.content.isNotBlank()
        binding.btnBrowser.isVisible = post.url.isNotBlank()
        binding.imgShare.setOnClickListener{
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, "${post.title} ${post.permalink}")
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, getString(R.string.share)))

            val animation = AnimationUtils.loadAnimation(this@SinglePostActivity, R.anim.alpha)
            binding.imgShare.startAnimation(animation)
        }
    }

    fun onContinue(view: View){
        try {
            val i = Intent(Intent.ACTION_VIEW)
            println(post.url)
            i.data = Uri.parse(post.url)
            startActivity(i)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this@SinglePostActivity, getString(R.string.general_error), Toast.LENGTH_LONG).show()
        }
    }
}