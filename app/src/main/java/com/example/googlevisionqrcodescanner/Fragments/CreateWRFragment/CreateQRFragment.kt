package com.example.googlevisionqrcodescanner.Fragments.CreateWRFragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.googlevisionqrcodescanner.Adapters.CreateQRAdapter
import com.example.googlevisionqrcodescanner.Callbacks.QRFormateCallbacks
import com.example.googlevisionqrcodescanner.Fragments.Mvvm.CreateQRFactory
import com.example.googlevisionqrcodescanner.Fragments.Mvvm.CreateQRRepository
import com.example.googlevisionqrcodescanner.Fragments.Mvvm.CreateQRViewModel
import com.example.googlevisionqrcodescanner.Models.QRFormatesModel
import com.example.googlevisionqrcodescanner.databinding.FragmentCreateQRBinding

class CreateQRFragment : Fragment() {
    lateinit var binding: FragmentCreateQRBinding
    lateinit var mContext: Context
    var mCreateQRViewModel: CreateQRViewModel? = null
    private var mainlist = ArrayList<QRFormatesModel>()
    var otherlist = ArrayList<QRFormatesModel>()
    var mCreateQRAdapter: CreateQRAdapter? = null
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mContext = activity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateQRBinding.inflate(layoutInflater)

        initialization()




        return binding.root
    }

    private fun initialProcess() {
        mCreateQRViewModel!!.callForMainData()
        mCreateQRViewModel!!.mMainLiveData.observe(viewLifecycleOwner) {
            mainlist = it
            Log.d("TAGBarCodeCreateFormate", "initialProcess: $it")
            setUpMainAdapter(mainlist)
        }


        mCreateQRViewModel!!.callForOtherData()
        mCreateQRViewModel!!.mOtherLiveData.observe(viewLifecycleOwner) {
            otherlist = it
            setUpOtherAdapter(otherlist)
        }

    }

    private fun initialization() {
        val mCreateQRRepository = CreateQRRepository()
        mCreateQRViewModel = ViewModelProvider(
            this,
            CreateQRFactory(mCreateQRRepository)
        ).get(CreateQRViewModel::class.java)
        initialProcess()
    }

    private fun setUpMainAdapter(mainlist: ArrayList<QRFormatesModel>) {
        binding.createQRRecViewMain.layoutManager = LinearLayoutManager(mContext)
        mCreateQRAdapter = CreateQRAdapter(mContext, mainlist, object : QRFormateCallbacks {
            override fun onQRFormateClick(model: QRFormatesModel) {

            }
        })
        binding.createQRRecViewMain.adapter = mCreateQRAdapter
    }

    private fun setUpOtherAdapter(otherlist: ArrayList<QRFormatesModel>) {
        binding.createQRRecViewOthers.layoutManager = LinearLayoutManager(mContext)
        mCreateQRAdapter = CreateQRAdapter(mContext, otherlist, object : QRFormateCallbacks {
            override fun onQRFormateClick(model: QRFormatesModel) {

            }
        })
        binding.createQRRecViewOthers.adapter = mCreateQRAdapter
    }




}