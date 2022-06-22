package com.example.googlevisionqrcodescanner.Helpers

import com.example.googlevisionqrcodescanner.Models.QRFormatesModel
import com.example.googlevisionqrcodescanner.R

class CreateQRFormates {
 companion object{
     fun fillQRMainFormate(): ArrayList<QRFormatesModel> {
         val list = ArrayList<QRFormatesModel>()
         list.add(QRFormatesModel("Content from Clipboard", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("URL", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("Text", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("Contact", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("Email", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("SMS", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("Geo", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("Phone", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("Calendar", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("Wifi", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("MyQR", R.drawable.ic_baseline_image_24))

         return list
     }

     fun fillQROtherFormate(): ArrayList<QRFormatesModel> {
         val list = ArrayList<QRFormatesModel>()
         list.add(QRFormatesModel("EAN_8", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("EAN_13", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("UPC_E", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("UPC_A", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("CODE_39", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("CODE_93", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("CODE_128", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("ITF", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("PDF_417", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("CODABAR", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("DATA_MATRIX", R.drawable.ic_baseline_image_24))
         list.add(QRFormatesModel("AZTEC", R.drawable.ic_baseline_image_24))

         return list
     }
 }
}