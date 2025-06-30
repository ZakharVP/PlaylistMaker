package com.practicum.playlistmaker.playlist.mediateka.ui.views

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediatekaBinding
import com.practicum.playlistmaker.playlist.mediateka.ui.adapters.ViewPagerAdapter
import com.practicum.playlistmaker.playlist.mediateka.ui.fragments.FavoritesFragment
import com.practicum.playlistmaker.playlist.mediateka.ui.fragments.PlaylistsFragment
import com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels.MediatekaViewModel

class MediatekaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediatekaBinding
    private val viewModel: MediatekaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediatekaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupViewPager()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolBarMediateka.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        adapter.addFragment(FavoritesFragment.newInstance(), getString(R.string.favorites_title))
        adapter.addFragment(PlaylistsFragment.newInstance(), getString(R.string.playlists_title))

        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }
}