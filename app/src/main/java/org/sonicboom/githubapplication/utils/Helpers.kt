package org.sonicboom.githubapplication.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.sonicboom.githubapplication.constant.ApiStatus


@SuppressLint("SetTextI18n")
fun stateUtilWithEmptyView(
    context: Context,
    apiStatus: ApiStatus,
    progressBar: ProgressBar,
    recyclerView: RecyclerView,
    tvEmptyView: TextView,
    imEmptyView: ImageView,
    layoutEmptyView: View,
    textEmptyView: String,
    imageEmpty: Int,
    lottie: View
) {
    Log.d("STATUS_HISTORY", apiStatus.toString())
    when (apiStatus) {
        ApiStatus.SUCCESS -> {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            lottie.visibility = View.GONE

        }
        ApiStatus.LOADING -> {
            progressBar.visibility = View.VISIBLE
            lottie.visibility = View.GONE

        }
        ApiStatus.EMPTY -> {
            tvEmptyView.text = textEmptyView
            imEmptyView.setImageResource(imageEmpty)
            layoutEmptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE
            lottie.visibility = View.GONE

        }
        ApiStatus.EMPTY_BEFORE -> {
            progressBar.visibility = View.GONE
            lottie.visibility = View.GONE

        }
        ApiStatus.EMPTY_AFTER -> {
            progressBar.visibility = View.GONE
            lottie.visibility = View.GONE

        }
        ApiStatus.LOADED -> {
            layoutEmptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            lottie.visibility = View.GONE

        }
        ApiStatus.FAILED -> {
            progressBar.visibility = View.GONE
            layoutEmptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            tvEmptyView.text = "Jaringan Bermasalah atau Anda Terkena Limit Github API"
            imEmptyView.setImageResource(imageEmpty)
            imEmptyView.visibility = View.GONE
            lottie.visibility = View.VISIBLE
        }
        ApiStatus.FAILED_API -> {
            progressBar.visibility = View.GONE
            layoutEmptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            tvEmptyView.text = "Anda Terkena Limit Github API"
            imEmptyView.setImageResource(imageEmpty)
            imEmptyView.visibility = View.GONE
            lottie.visibility = View.VISIBLE
        }
    }
}