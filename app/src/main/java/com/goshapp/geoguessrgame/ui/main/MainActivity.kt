

package com.goshapp.geoguessrgame.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.observe
import com.goshapp.geoguessrgame.R
import com.goshapp.geoguessrgame.databinding.ActivityMainBinding
import com.shreyaspatil.MaterialDialog.MaterialDialog
import com.goshapp.geoguessrgame.model.Post
import com.goshapp.geoguessrgame.model.State
import com.goshapp.geoguessrgame.ui.base.BaseActivity
import com.goshapp.geoguessrgame.ui.details.PostDetailsActivity
import com.goshapp.geoguessrgame.ui.main.adapter.PostListAdapter
import com.goshapp.geoguessrgame.utils.hide
import com.goshapp.geoguessrgame.utils.show
import dagger.hilt.android.AndroidEntryPoint
import com.goshapp.geoguessrgame.utils.NetworkUtils
import com.goshapp.geoguessrgame.utils.getColorRes
import com.goshapp.geoguessrgame.utils.showToast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.DataPoint


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override val mViewModel: MainViewModel by viewModels()

    private val mAdapter = PostListAdapter(this::onItemClicked)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme) // Set AppTheme before setting content view.

        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)

        // Initialize RecyclerView
        mViewBinding.postsRecyclerView.adapter = mAdapter

        initPosts()

        handleNetworkChanges()

        val series: LineGraphSeries<DataPoint> = LineGraphSeries(
            arrayOf(
                DataPoint(0.0, 1.0),
                DataPoint(1.0, 5.0),
                DataPoint(2.0, 3.0),
                DataPoint(3.0, 2.0),
                DataPoint(4.0, 6.0)
            )
        )
        mViewBinding.graph.addSeries(series)
    }

    private fun initPosts() {
        mViewModel.postsLiveData.observe(this) { state ->
            when (state) {
                is State.Loading -> showLoading(true)
                is State.Success -> {
                    if (state.data.isNotEmpty()) {
                        mAdapter.submitList(state.data.toMutableList())
                        showLoading(false)
                    }
                }
                is State.Error -> {
                    showToast(state.message)
                    showLoading(false)
                }
            }
        }

        mViewBinding.swipeRefreshLayout.setOnRefreshListener {
            getPosts()
        }

        // If State isn't `Success` then reload posts.
        if (mViewModel.postsLiveData.value !is State.Success) {
            getPosts()
        }
    }

    private fun getPosts() {
        mViewModel.getPosts()
    }

    private fun showLoading(isLoading: Boolean) {
        mViewBinding.swipeRefreshLayout.isRefreshing = isLoading
    }

    /**
     * Observe network changes i.e. Internet Connectivity
     */
    private fun handleNetworkChanges() {
        NetworkUtils.getNetworkLiveData(applicationContext).observe(this) { isConnected ->
            if (!isConnected) {
                mViewBinding.textViewNetworkStatus.text =
                    getString(R.string.text_no_connectivity)
                mViewBinding.networkStatusLayout.apply {
                    show()
                    setBackgroundColor(getColorRes(R.color.colorStatusNotConnected))
                }
            } else {
                if (mViewModel.postsLiveData.value is State.Error || mAdapter.itemCount == 0) {
                    getPosts()
                }
                mViewBinding.textViewNetworkStatus.text = getString(R.string.text_connectivity)
                mViewBinding.networkStatusLayout.apply {
                    setBackgroundColor(getColorRes(R.color.colorStatusConnected))

                    animate()
                        .alpha(1f)
                        .setStartDelay(ANIMATION_DURATION)
                        .setDuration(ANIMATION_DURATION)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                hide()
                            }
                        })
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_theme -> {
                // Get new mode.
                val mode =
                    if ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                        Configuration.UI_MODE_NIGHT_NO
                    ) {
                        AppCompatDelegate.MODE_NIGHT_YES
                    } else {
                        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                    }

                // Change UI Mode
                AppCompatDelegate.setDefaultNightMode(mode)
                true
            }

            else -> true
        }
    }

    override fun onBackPressed() {
        MaterialDialog.Builder(this)
            .setTitle(getString(R.string.exit_dialog_title))
            .setMessage(getString(R.string.exit_dialog_message))
            .setPositiveButton(getString(R.string.option_yes)) { dialogInterface, _ ->
                dialogInterface.dismiss()
                super.onBackPressed()
            }
            .setNegativeButton(getString(R.string.option_no)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .build()
            .show()
    }

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private fun onItemClicked(post: Post, imageView: ImageView) {
        val intent = Intent(this, PostDetailsActivity::class.java)
        intent.putExtra(PostDetailsActivity.POST_ID, post.id)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            imageView,
            imageView.transitionName
        )

        startActivity(intent, options.toBundle())
    }

    companion object {
        const val ANIMATION_DURATION = 1000.toLong()
    }
}
