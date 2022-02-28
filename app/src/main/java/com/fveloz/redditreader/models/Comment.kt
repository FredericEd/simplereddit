package com.fveloz.redditreader.models

import org.json.JSONObject
import java.util.*

data class Comment (
    val id: String,
    val content: String,
    val author : String,
    val date: Calendar,
    val ups: Int,
    val downs: Int) {

    constructor(json: JSONObject) : this(
        json.getString("id"),
        if (json.has("body")) json.getString("body") else "",
        if (json.has("author")) json.getString("author") else "",
        Calendar.getInstance(),
        if (json.has("created_utc")) json.getInt("ups") else 0,
        if (json.has("downs")) json.getInt("downs") else 0) {
            if (json.has("created_utc")) {
                val dt = Date(json.getLong("created_utc") * 1000)
                this.date.time = dt
            }
    }
}