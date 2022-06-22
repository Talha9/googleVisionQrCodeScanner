package com.example.googlevisionqrcodescanner.Fragments.Mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CreateQRFactory(var mQRRepository: CreateQRRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CreateQRViewModel::class.java)) {
            CreateQRViewModel(this.mQRRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}