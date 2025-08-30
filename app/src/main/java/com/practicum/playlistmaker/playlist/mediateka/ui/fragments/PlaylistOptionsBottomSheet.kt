package com.practicum.playlistmaker.playlist.mediateka.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.practicum.playlistmaker.R

class PlaylistOptionsBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.button_create_playlist).setOnClickListener {
            navigateToNewPlaylist()
            dismiss()
        }
    }

    override fun getTheme(): Int {
        return R.style.ThemeOverlay_App_BottomSheetDialog
    }

    private fun navigateToNewPlaylist() {
        try {
            findNavController().navigate(R.id.action_mediatekaFragment_to_newPlaylistFragment)
        } catch (e: Exception) {
            // Для отладки
            Toast.makeText(requireContext(), "Навигация к созданию плейлиста", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val TAG = "PlaylistOptionsBottomSheet"
    }
}