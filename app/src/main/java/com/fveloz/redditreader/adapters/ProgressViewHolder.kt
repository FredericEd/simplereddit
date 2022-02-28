package com.fveloz.redditreader.adapters

import android.view.View
import android.widget.ProgressBar
import com.fveloz.redditreader.R

class ProgressViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
    private val progressBar: ProgressBar = v.findViewById(R.id.progressBar)

    init {
        progressBar.isIndeterminate = true
    }
}