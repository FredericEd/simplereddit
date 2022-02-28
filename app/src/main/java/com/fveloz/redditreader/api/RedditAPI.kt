package com.fveloz.redditreader.api

import com.android.volley.DefaultRetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.fveloz.redditreader.R
import com.fveloz.redditreader.ReaderApplication
import com.fveloz.redditreader.models.Comment
import com.fveloz.redditreader.models.Post
import com.fveloz.redditreader.utilities.NetworkUtils
import com.fveloz.redditreader.utilities.ResultState
import com.fveloz.redditreader.utilities.VolleySingleton
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RedditAPI {
    companion object{

        suspend fun getPostsBySubreddit(subreddit: String, endpoint: String, offset: String = "") = suspendCoroutine<ResultState<ArrayList<Post>>>{ cont ->
            if (NetworkUtils.isConnected()) {
                println("https://www.reddit.com/r/$subreddit/$endpoint.json?after=$offset")
                val stringRequest = object : StringRequest(Method.GET, "https://www.reddit.com/r/$subreddit/$endpoint.json?after=$offset",
                    { response ->
                        try {
                            val dataList = ArrayList<Post>()
                            val json = JSONObject(response)
                            println(response)
                            for (i in 0 until json.getJSONObject("data").getJSONArray("children").length()) {
                                val singleJson = json.getJSONObject("data").getJSONArray("children").getJSONObject(i).getJSONObject("data")
                                dataList.add(Post(singleJson))
                            }
                            if (dataList.isEmpty()) cont.resume(ResultState.Empty())
                            else cont.resume(ResultState.Success(dataList))
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            cont.resume(ResultState.Error(ReaderApplication.getAppContext().getString(R.string.general_error)))
                        }
                    },
                    {
                        try {
                            if (it.networkResponse.statusCode == 404) cont.resume(ResultState.Empty())
                            else cont.resume(
                                ResultState.Error(
                                    ReaderApplication.getAppContext()
                                        .getString(R.string.general_error)
                                )
                            )
                        } catch (e: java.lang.Exception) {
                            cont.resume(ResultState.Error(ReaderApplication.getAppContext()
                                .getString(R.string.general_error)))
                        }
                    }
                ){}
                stringRequest.retryPolicy = DefaultRetryPolicy(180000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                VolleySingleton.getInstance(ReaderApplication.getAppContext()).addToRequestQueue(stringRequest)
            } else cont.resume(ResultState.Error(ReaderApplication.getAppContext().getString(R.string.no_connection)))
        }

        suspend fun getCommentsByPost(subreddit: String, id: String) = suspendCoroutine<ResultState<ArrayList<Comment>>>{ cont ->
            if (NetworkUtils.isConnected()) {
                val stringRequest = object : StringRequest(Method.GET, "https://www.reddit.com/r/$subreddit/comments/$id.json",
                    { response ->
                        try {
                            val dataList = ArrayList<Comment>()
                            val json = JSONArray(response)
                            println(response)
                            for (i in 0 until json.getJSONObject(1).getJSONObject("data").getJSONArray("children").length()) {
                                val singleJson = json.getJSONObject(1).getJSONObject("data").getJSONArray("children").getJSONObject(i).getJSONObject("data")
                                dataList.add(Comment(singleJson))
                            }
                            if (dataList.isEmpty()) cont.resume(ResultState.Empty())
                            else cont.resume(ResultState.Success(dataList))
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            cont.resume(ResultState.Error(ReaderApplication.getAppContext().getString(R.string.general_error)))
                        }
                    },
                    {
                        try {
                            if (it.networkResponse.statusCode == 404) cont.resume(ResultState.Empty())
                            else cont.resume(
                                ResultState.Error(
                                    ReaderApplication.getAppContext()
                                        .getString(R.string.general_error)
                                )
                            )
                        } catch (e: java.lang.Exception) {
                            cont.resume(ResultState.Error(ReaderApplication.getAppContext()
                                .getString(R.string.general_error)))
                        }
                    }
                ){}
                stringRequest.retryPolicy = DefaultRetryPolicy(180000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                VolleySingleton.getInstance(ReaderApplication.getAppContext()).addToRequestQueue(stringRequest)
            } else cont.resume(ResultState.Error(ReaderApplication.getAppContext().getString(R.string.no_connection)))
        }
    }
}
