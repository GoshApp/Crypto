package com.goshapp.geoguessrgame.ui.main.viewholder

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.goshapp.geoguessrgame.R
import com.goshapp.geoguessrgame.databinding.ItemPostBinding
import com.goshapp.geoguessrgame.model.Post
import com.goshapp.geoguessrgame.ui.main.adapter.PostListAdapter


/**
 * [RecyclerView.ViewHolder] implementation to inflate View for RecyclerView.
 * See [PostListAdapter]]
 */
class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post, onItemClicked: (Post, ImageView) -> Unit) {
        binding.postTitle.text = post.title
        binding.postAuthor.text = post.author
        binding.imageView.load(post.imageUrl) {
            placeholder(R.drawable.ic_photo)
            error(R.drawable.ic_broken_image)
        }

        binding.root.setOnClickListener {
            onItemClicked(post, binding.imageView)
        }
    }
}
