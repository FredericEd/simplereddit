package com.fveloz.redditreader

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fveloz.redditreader.adapters.BasicUIObserver
import com.fveloz.redditreader.adapters.PostsAdapter
import com.fveloz.redditreader.databinding.ActivityRecyclerBinding
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.fveloz.redditreader.models.Post
import com.fveloz.redditreader.utilities.NetworkUtils
import com.fveloz.redditreader.utilities.ResultState
import com.fveloz.redditreader.viewmodels.GeneralViewModel
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class MainPageActivity : AppCompatActivity(), BasicUIObserver<Post> {


    private var _binding: ActivityRecyclerBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : PostsAdapter
    private val viewModel: GeneralViewModel by viewModels()

    private var postList: ArrayList<Post> = arrayListOf()
    private var endpoint = "new"
    private var subreddit = ReaderApplication.getSubredditName()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PostsAdapter(postList, this@MainPageActivity)
        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainPageActivity)
        binding.recyclerView.adapter = adapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (!NetworkUtils.isConnected(this@MainPageActivity)) {
                Toast.makeText(this@MainPageActivity, R.string.no_connection, Toast.LENGTH_LONG).show()
                binding.swipeRefreshLayout.isRefreshing = false
            } else {
                binding.swipeRefreshLayout.isRefreshing = true
                resetAndLoad()
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.isLoading.collect{
                binding.progressView.isVisible = it
                binding.contentView.isVisible = !it
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.postList.collect {
                binding.swipeRefreshLayout.isRefreshing = false
                if (it.size > 0) {
                    postList = it
                    adapter.setData(postList)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.getPostListState.collect{
                binding.swipeRefreshLayout.isRefreshing = false
                when (it) {
                    is ResultState.Empty ->{
                        binding.textEmpty.text = resources.getString(R.string.no_data)
                        binding.layEmpty.isVisible = true
                        binding.swipeRefreshLayout.isVisible = true
                    }
                    is  ResultState.Error ->{
                        binding.textEmpty.text = it.message
                        binding.layEmpty.isVisible = true
                        binding.swipeRefreshLayout.isVisible = false
                    }
                    is ResultState.Success -> {
                        binding.layEmpty.isVisible = false
                        binding.swipeRefreshLayout.isVisible = true
                    }
                }
            }
        }
        resetAndLoad()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuChange) {
            val builder = AlertDialog.Builder(this@MainPageActivity)
            val inflater = layoutInflater
            builder.setTitle(R.string.subreddit_edit)
            val dialogLayout = inflater.inflate(R.layout.alert_subreddit, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.editSubreddit)
            editText.setText(subreddit)
            builder.setView(dialogLayout)
            builder.setPositiveButton(R.string.ok) { dialogInterface, i ->
                try {
                    if (editText.text.toString().isNotBlank()) {
                        subreddit = editText.text.toString()
                        resetAndLoad()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@MainPageActivity, R.string.general_error, Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            builder.show()
        } else {
            endpoint = when (item.itemId) {
                R.id.menuNew -> "new"
                else -> "hot"
            }
            resetAndLoad()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onElementClicked(post: Post) {
        val i = Intent(this@MainPageActivity, SinglePostActivity::class.java)
        i.putExtra("data", Gson().toJson(post))
        startActivity(i)
    }

    override fun onShareClicked(post: Post, view: View) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, "${post.title} ${post.permalink}")
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share)))

        val animation = AnimationUtils.loadAnimation(this@MainPageActivity, R.anim.alpha)
        view.startAnimation(animation)
    }

    private fun resetAndLoad() {
        viewModel.getPostsByReddit(subreddit, endpoint)
    }

    override fun onApproachingEnd() {
        if (!NetworkUtils.isConnected(this@MainPageActivity)) {
            Toast.makeText(this@MainPageActivity, R.string.no_connection, Toast.LENGTH_LONG).show()
        } else viewModel.getPostsByReddit(subreddit, endpoint, postList[postList.size - 1].id)
    }
}