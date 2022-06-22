package com.example.googlevisionqrcodescanner.Fragments.Mvvm

import androidx.lifecycle.MutableLiveData
import com.example.googlevisionqrcodescanner.Helpers.CreateQRFormates
import com.example.googlevisionqrcodescanner.Models.QRFormatesModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CreateQRRepository {
    val mQRMainFormateLiveData = MutableLiveData<ArrayList<QRFormatesModel>>()
    val mQROtherFormateLiveData = MutableLiveData<ArrayList<QRFormatesModel>>()

    fun fetchMainQRFormates() {
        var list: ArrayList<QRFormatesModel>
        GlobalScope.launch {
            list=CreateQRFormates.fillQRMainFormate()
            withContext(Dispatchers.Main){
                mQRMainFormateLiveData.value=list
            }
        }

    }

    fun fetchOtherQRLiveData(){
        var list: ArrayList<QRFormatesModel>
        GlobalScope.launch {
            list=CreateQRFormates.fillQROtherFormate()
            withContext(Dispatchers.Main){
                mQROtherFormateLiveData.value=list
            }
        }

    }

}