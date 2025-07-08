package com.practicum.playlistmaker.playlist.mediateka.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.databinding.FragmentMediatekaBinding
import com.practicum.playlistmaker.playlist.mediateka.ui.adapters.ViewPagerAdapter
import com.practicum.playlistmaker.playlist.mediateka.ui.viewmodels.MediatekaViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MediatekaFragment : Fragment() {

    private var _binding: FragmentMediatekaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MediatekaViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediatekaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupViewPager()
    }

    private fun setupToolbar() {
        binding.toolBarMediateka.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)

        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MediatekaFragment()
    }

}