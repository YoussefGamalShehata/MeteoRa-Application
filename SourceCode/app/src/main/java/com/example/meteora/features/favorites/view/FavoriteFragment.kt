package com.example.meteora.features.favorites.view

import FavoritesAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meteora.R
import com.example.meteora.db.local.LocalDataSourceImpl
import com.example.meteora.db.repository.RepositoryImpl
import com.example.meteora.features.favorites.viewModel.FavoriteViewModel
import com.example.meteora.features.favorites.viewModel.FavoriteViewModelFactory
import com.example.meteora.network.ApiClient
import com.example.meteora.network.RemoteDataSourceImpl
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {
    private lateinit var viewModel: FavoriteViewModel
    private val favoritesAdapter = FavoritesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = FavoriteViewModelFactory(
            RepositoryImpl(
                RemoteDataSourceImpl.getInstance(ApiClient.retrofit),
                LocalDataSourceImpl(requireContext())
            )
        )
        viewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.favorites_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = favoritesAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favorites.collectLatest { favorites ->
                favoritesAdapter.submitList(favorites)
            }
        }
    }
}
