package com.tobaxiom.placebo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tobaxiom.placebo.data.StreaksRepository

class StreaksViewModelFactory(private val repository: StreaksRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StreaksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StreaksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
