package com.example.googlevisionqrcodescanner.Fragments.Mvvm

import androidx.lifecycle.ViewModel

class CreateQRViewModel(val mRepository: CreateQRRepository) : ViewModel() {
    val mMainLiveData= mRepository.mQRMainFormateLiveData
    val mOtherLiveData= mRepository.mQROtherFormateLiveData
    fun callForMainData(){
        mRepository.fetchMainQRFormates()
    }
    fun callForOtherData(){
        mRepository.fetchOtherQRLiveData()
    }
}