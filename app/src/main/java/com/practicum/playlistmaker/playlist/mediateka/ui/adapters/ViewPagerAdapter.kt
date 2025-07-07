package com.practicum.playlistmaker.playlist.mediateka.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.playlist.mediateka.ui.fragments.FavoritesFragment
import com.practicum.playlistmaker.playlist.mediateka.ui.fragments.PlaylistsFragment

class ViewPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private val fragments = listOf(
        FavoritesFragment.newInstance(),
        PlaylistsFragment.newInstance()
    )

    private val titles = listOf(
        fragment.getString(R.string.favorites_title),
        fragment.getString(R.string.playlists_title)
    )

    fun getPageTitle(position: Int): String = titles[position]

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}