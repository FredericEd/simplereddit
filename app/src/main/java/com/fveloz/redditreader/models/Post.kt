package com.fveloz.redditreader.models

import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

data class Post (
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val subreddit: String,
    var url: String,
    val permalink: String,
    val date: Calendar,
    val num_comments: Int,
    val ups: Int,
    val downs: Int) {

    constructor(json: JSONObject) : this(
        json.getString("id"),
        if (json.has("title")) json.getString("title") else "",
        if (json.has("selftext")) json.getString("selftext") else "",
        if (json.has("author")) json.getString("author") else "",
        if (json.has("subreddit")) json.getString("subreddit") else "",
        if (json.has("url_overridden_by_dest")) json.getString("url_overridden_by_dest") else "",
        if (json.has("permalink")) "https://www.reddit.com/" + json.getString("permalink") else "",
        Calendar.getInstance(),
        if (json.has("num_comments")) json.getInt("num_comments") else 0,
        if (json.has("ups")) json.getInt("ups") else 0,
        if (json.has("downs")) json.getInt("downs") else 0) {
            if (json.has("created_utc")) {
                val dt = Date(json.getLong("created_utc") * 1000)
                this.date.time = dt
            }
            if (this.url.isBlank() && json.has("selftext")) {
                val urlList = getURLsFromString(json.getString("selftext"))
                if (urlList.isNotEmpty()) this.url = urlList[0]
            }
    }

    private fun getURLsFromString(text: String): List<String> {
        val containedUrls: MutableList<String> = ArrayList()
        val urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)"
        val pattern: Pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE)
        val urlMatcher: Matcher = pattern.matcher(text)
        while (urlMatcher.find()) {
            containedUrls.add(
                text.substring(
                    urlMatcher.start(0),
                    urlMatcher.end(0)
                )
            )
        }
        return containedUrls
    }
}