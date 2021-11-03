
package com.goshapp.geoguessrgame.ui.details

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.goshapp.geoguessrgame.data.repository.PostsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * ViewModel for [PostDetailsActivity]
 */
@ExperimentalCoroutinesApi
class PostDetailsViewModel @ViewModelInject constructor(
    private val postsRepository: PostsRepository
) : ViewModel() {

    fun getPost(id: Int) = postsRepository.getPostById(id).asLiveData()
}
