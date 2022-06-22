package com.example.googlevisionqrcodescanner.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.googlevisionqrcodescanner.Callbacks.QRFormateCallbacks
import com.example.googlevisionqrcodescanner.Models.QRFormatesModel
import com.example.googlevisionqrcodescanner.R

class CreateQRAdapter(
    var mContext: Context,
    var list: ArrayList<QRFormatesModel>,
    var callback: QRFormateCallbacks
) :
    RecyclerView.Adapter<CreateQRAdapter.CreateQRViewHlder>() {

    inner class CreateQRViewHlder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: ImageView? = null
        var txt: TextView? = null
        var btn: ConstraintLayout? = null

        init {
            img = itemView.findViewById(R.id.itemImg)
            txt = itemView.findViewById(R.id.itemtxt)
            btn = itemView.findViewById(R.id.item)
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateQRViewHlder {
        val v = LayoutInflater.from(mContext)
            .inflate(R.layout.single_qr_formate_item_design, parent, false)
        return CreateQRViewHlder(v)
    }

    override fun onBindViewHolder(holder: CreateQRViewHlder, position: Int) {
        val model = list[position]
        holder.txt!!.text = model.formateName
        try {
            Glide.with(mContext).load(model.img).into(holder.img!!)
        } catch (e: Exception) {
        }
        holder.btn!!.setOnClickListener {
            callback.onQRFormateClick(model)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}