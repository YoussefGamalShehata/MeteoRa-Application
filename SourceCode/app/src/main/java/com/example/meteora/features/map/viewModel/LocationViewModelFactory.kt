    package com.example.meteora.features.map.viewModel

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.ViewModelProvider
    import com.example.meteora.db.local.LocalDataSourceImpl
    import com.example.meteora.db.repository.RepositoryImpl
    import com.example.meteora.network.RemoteDataSourceImpl

    class LocationViewModelFactory(private val repositoryImpl: RepositoryImpl) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
                return LocationViewModel(repositoryImpl) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }