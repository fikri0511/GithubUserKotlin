package org.sonicboom.githubapplication.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView


class PagedAdapterUtil<T>(
    var layout: Int,
    var view: (Int, View, T) -> Unit,
    var handler: (Int, T) -> Unit
) : PagedListAdapter<T, PagedAdapterUtil.ViewHolder<T>>(DiffUtilCallback()) {

    class ViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: T, view: (Int, View, T) -> Unit) {
            view(adapterPosition, itemView, item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(this.layout, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(item, view)
            holder.itemView.setOnClickListener { handler(position, item) }
        }

    }
}