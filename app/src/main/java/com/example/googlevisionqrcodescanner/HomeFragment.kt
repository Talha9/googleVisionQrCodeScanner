package com.example.googlevisionqrcodescanner

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import com.bumptech.glide.Glide
import com.example.googlevisionqrcodescanner.Activities.MainActivity2
import com.example.googlevisionqrcodescanner.databinding.FragmentHomeBinding
import java.io.File
import java.io.FileWriter

class HomeFragment : Fragment() {
    lateinit var mContext: Context
    private var clipManager: ClipboardManager? = null
    private var url: String? = ""
    private var title: String? = ""
    private var body: String? = ""
    lateinit var binding: FragmentHomeBinding
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mContext = activity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        container!!.removeAllViews()
        onCLickListeners()
        initialization()


        return binding.root
    }

    private fun initialization() {
        clipManager = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private fun onCLickListeners() {
        binding.scanbtn.setOnClickListener {
            binding.openBtn.visibility = View.GONE
            binding.shareBtn.visibility = View.GONE
            binding.copyBtn.visibility = View.GONE
            binding.saveBtn.visibility = View.GONE
            val intent = Intent(mContext, MainActivity2::class.java)
            resultLauncher.launch(intent)

        }


        binding.openBtn.setOnClickListener {
            if (url != null) {
                gotoUrl(url!!)
            }
        }
        binding.shareBtn.setOnClickListener {
            share(title!!, body!! + url)
        }
        binding.copyBtn.setOnClickListener {
            val str = title + "\n" + body + url
            clipManager!!.text = str
            Toast.makeText(mContext, "Copied To Clipboard", Toast.LENGTH_SHORT).show()
        }
        binding.saveBtn.setOnClickListener {
            val textToSaveString = title + "\n" + body + url
            writeToFile(title, textToSaveString)
        }

    }



    private fun writeToFile(title: String?, data: String) {
        val file = File(mContext.filesDir, "text")
        if (!file.exists()) {
            file.mkdir()
        }
        val gpxfile = File(file, "QR $title")
        val writer = FileWriter(gpxfile)
        writer.append(data)
        writer.flush()
        writer.close()
        Toast.makeText(mContext, "Saved your text", Toast.LENGTH_LONG).show()
    }


    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                if (data.getParcelableExtra<WifiModel>("barcodeWifi") != null) {
                    val barcode = data.getParcelableExtra<WifiModel>("barcodeWifi")
                    Glide.with(mContext).load(barcode!!.img).into(binding.imgV)
                    binding.result.post { binding.result.text = barcode!!.ssid + "\n" + barcode.password + "\n" + barcode.type.toString() }
                    url = ""
                    title = "ssid: " + barcode.ssid
                    body = "Password: " + barcode.password + "\n" + "Type: " + barcode.type
                    binding.shareBtn.visibility = View.VISIBLE
                    binding.copyBtn.visibility = View.VISIBLE
                    binding.saveBtn.visibility = View.VISIBLE
                } else if (data.getParcelableExtra<UrlModel>("barcodeUrl") != null) {
                    val barcode = data.getParcelableExtra<UrlModel>("barcodeUrl")
                    Glide.with(mContext).load(barcode!!.img).into(binding.imgV)
                    binding.result.post {
                        binding.result.text = barcode!!.title + "\n" + barcode.url
                    }
                    url = barcode.url
                    title = barcode.title
                    body = ""
                    binding.openBtn.visibility = View.VISIBLE
                    binding.shareBtn.visibility = View.VISIBLE
                    binding.copyBtn.visibility = View.VISIBLE
                    binding.saveBtn.visibility = View.VISIBLE
                } else if (data.getParcelableExtra<textModel>("barcodeTxt") != null) {
                    val barcode = data.getParcelableExtra<textModel>("barcodeTxt")
                    Glide.with(mContext).load(barcode!!.img).into(binding.imgV)
                    binding.result.post {
                        binding.result.text = barcode.txt
                    }
                    url = ""
                    title = ""
                    body = barcode.txt
                    binding.shareBtn.visibility = View.VISIBLE
                    binding.copyBtn.visibility = View.VISIBLE
                    binding.saveBtn.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun gotoUrl(s: String) {
        val uri = Uri.parse(s)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun share(headerText: String, bodyText: String) {
        ShareCompat.IntentBuilder.from(requireActivity())
            .setType("text/plain")
            .setChooserTitle(headerText)
            .setText(bodyText)
            .startChooser()
    }

}