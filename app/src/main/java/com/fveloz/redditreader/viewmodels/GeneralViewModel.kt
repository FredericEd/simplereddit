package com.fveloz.redditreader.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fveloz.redditreader.api.RedditAPI
import com.fveloz.redditreader.models.Comment
import com.fveloz.redditreader.models.Post
import com.fveloz.redditreader.utilities.ResultState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

@ExperimentalCoroutinesApi
class GeneralViewModel(application: Application) : AndroidViewModel(application) {

    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var _postList = MutableStateFlow<ArrayList<Post>>(ArrayList())
    val postList: StateFlow<ArrayList<Post>> = _postList
    private var _getPostListState = MutableStateFlow<ResultState<ArrayList<Post>>>(ResultState.Empty())
    val getPostListState: StateFlow<ResultState<ArrayList<Post>>> = _getPostListState

    private var _commentList = MutableStateFlow<ArrayList<Comment>>(ArrayList())
    val commentList: StateFlow<ArrayList<Comment>> = _commentList
    private var _getCommentListState = MutableStateFlow<ResultState<ArrayList<Comment>>>(ResultState.Empty())
    val getCommentListState: StateFlow<ResultState<ArrayList<Comment>>> = _getCommentListState

    fun getPostsByReddit(subreddit: String, endpoint: String, offset: String = "") = viewModelScope.launch{
        if (offset.isBlank()) _isLoading.value = true
        val resultState = RedditAPI.getPostsBySubreddit(subreddit, endpoint, if (offset.isNotBlank()) "t3_$offset" else offset)
        _getPostListState.value = resultState
        if (resultState is ResultState.Success<ArrayList<Post>>) {
            val temp =  ArrayList<Post>()
            if (offset.isNotBlank()) {
                _postList.value.forEach {
                    temp.add(it)
                }
            }
            resultState.data.forEach{
                temp.add(it)
            }
            _postList.value = temp
        }
        _isLoading.value = false
    }

    fun getCommentsByPost(subreddit: String, id: String) = viewModelScope.launch{
        _isLoading.value = true
        val resultState = RedditAPI.getCommentsByPost(subreddit, id)
        _getCommentListState.value = resultState
        if (resultState is ResultState.Success<ArrayList<Comment>>) {
            val temp =  ArrayList<Comment>()
            resultState.data.forEach{
                temp.add(it)
            }
            _commentList.value = temp
        }
        _isLoading.value = false
    }
}