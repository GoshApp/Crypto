package com.goshapp.geoguessrgame.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goshapp.geoguessrgame.data.repository.PostsRepository
import com.goshapp.geoguessrgame.model.Post
import com.goshapp.geoguessrgame.model.State
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * ViewModel for [MainActivity]
 */
@ExperimentalCoroutinesApi
class MainViewModel @ViewModelInject constructor(private val postsRepository: PostsRepository) :
    ViewModel() {

    private val _postsLiveData = MutableLiveData<State<List<Post>>>()

    val postsLiveData: LiveData<State<List<Post>>>
        get() = _postsLiveData

    fun getPosts() {
        viewModelScope.launch {
            postsRepository.getAllPosts().collect {
                _postsLiveData.value = it
            }
        }
    }
}
