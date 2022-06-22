package com.example.googlevisionqrcodescanner.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.Camera
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.googlevisionqrcodescanner.*
import com.example.googlevisionqrcodescanner.databinding.ActivityMain2Binding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors


class MainActivity2 : AppCompatActivity() {


    private var croppedBmp: Bitmap? = null
    private var check1: Boolean = true
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var camera: Camera? = null
    private var cameraView: PreviewView? = null
    private var camBtn: ImageView? = null
    private var lightBtn: ImageView? = null
    private var galleryBtn: ImageView? = null
    private var zoomBar: SeekBar? = null
    private var overlay: ScannerOverlay? = null
    private var isFlashOff = true
    lateinit var binding: ActivityMain2Binding
    var defaultZoomValue = 30
    private val CAMERA_PERMISSION_REQUEST_CODE = 1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        cameraView = binding.cameraView
        zoomBar = binding.zoomSeekbar
        camBtn = binding.cameraViewChangeBtn
        lightBtn = binding.lightBtn
        galleryBtn = binding.fromStorageBtn
        overlay = ScannerOverlay(this)
        overlay = binding.overshoot
        overlay!!.setBarColor(Color.GREEN)

        if (hasCameraPermission())
        {
            bindCameraUseCases()
        } else {
            requestPermission()
        }


        onCLickListeners()


  /*      cameraView!!.previewStreamState.observe(this) { value ->
            if (value.equals(PreviewView.StreamState.STREAMING)) {

            }
        }*/

    }


    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun onCLickListeners() {
        if (hasCameraPermission()) {
            zoomBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    defaultZoomValue = progress
                    camera!!.cameraInfo.exposureState.exposureCompensationStep.toFloat() * 50
                    camera!!.cameraControl.setLinearZoom(defaultZoomValue / 100.toFloat())

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })
        }

        camBtn!!.setOnClickListener {
            toggleFrontBackCamera()
        }

        lightBtn!!.setOnClickListener {
            isFlashOff = if (isFlashOff) {
                if (camera!!.cameraInfo.hasFlashUnit()) {
                    camera!!.cameraControl.enableTorch(true)
                }
                false
            } else {
                if (camera!!.cameraInfo.hasFlashUnit()) {
                    camera!!.cameraControl.enableTorch(false)
                }
                true
            }
        }

        galleryBtn!!.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            resultLauncher.launch(photoPickerIntent)
           /* startActivityForResult(photoPickerIntent, 1000)*/
        }

        cameraView!!.setOnTouchListener { _, event ->
            val actionMasked = event.actionMasked // Or action
            if (actionMasked != MotionEvent.ACTION_DOWN) {
                return@setOnTouchListener false
            }
            val x = event.x
            val y = event.y
            val factory = cameraView!!.meteringPointFactory
            val point = factory.createPoint(x, y)
            val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                .addPoint(point, FocusMeteringAction.FLAG_AE)
                .addPoint(point, FocusMeteringAction.FLAG_AWB)
                .build()

            val future = camera!!.cameraControl.startFocusAndMetering(action)
            future.addListener(
                {
                    try {
                        val result = future.get()
                        Log.d("tag", "Focus Success: ${result.isFocusSuccessful}")
                    } catch (e: ExecutionException) {
                        Log.e("tag", "Focus failed", e)
                    } catch (e: InterruptedException) {
                        Log.e("tag", "Focus interrupted", e)
                    }
                }, Executors.newSingleThreadExecutor()
            )
            return@setOnTouchListener true
        }


    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            scanImage(data)

        }
    }

    private fun scanImage(data: Intent?) {
        val imageUri: Uri = data!!.data!!
        val imageStream = contentResolver.openInputStream(imageUri)
        val selectedImage = BitmapFactory.decodeStream(imageStream)
        val compressImg = Bitmap.createScaledBitmap(selectedImage, 160, 160, true)
        val image = InputImage.fromBitmap(selectedImage, 0)
        val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
            Barcode.FORMAT_ALL_FORMATS
        ).build()
        val scanner = BarcodeScanning.getClient(options)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val intent = Intent()
                CoroutineScope(Dispatchers.IO).launch {
                    for (barcode in barcodes) {
                        when (barcode.valueType) {
                            Barcode.TYPE_WIFI -> {
                                val ssid = barcode.wifi!!.ssid
                                val password = barcode.wifi!!.password
                                val type = barcode.wifi!!.encryptionType
                                withContext(Dispatchers.Main) {
                                    intent.putExtra(
                                        "barcodeWifi",
                                        WifiModel(ssid, password, type, compressImg)
                                    )
                                    setResult(RESULT_OK, intent)
                                    if (check1) {
                                        Toast.makeText(
                                            this@MainActivity2,
                                            "::$ssid,$password,$type",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        check1 = false
                                        Log.d("scanFromGalleryTAG", "scanImage: " + "WIFI")
                                        finish()
                                    }
                                }
                            }
                            Barcode.TYPE_URL -> {
                                val title = barcode.url!!.title
                                val url = barcode.url!!.url
                                withContext(Dispatchers.Main) {
                                    intent.putExtra("barcodeUrl", UrlModel(title, url, compressImg))
                                    setResult(RESULT_OK, intent)

                                    if (check1) {
                                        Toast.makeText(
                                            this@MainActivity2,
                                            "::$title,$url",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        check1 = false
                                        Log.d("scanFromGalleryTAG", "scanImage: " + "URL")
                                        finish()
                                    }
                                }
                            }
                            else -> {
                                val txt = barcode.displayValue
                                withContext(Dispatchers.Main) {
                                    intent.putExtra("barcodeTxt", textModel(txt, compressImg))
                                    setResult(RESULT_OK, intent)
                                    if (check1) {
                                        Toast.makeText(
                                            this@MainActivity2,
                                            "::$txt",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        check1 = false
                                        Log.d("scanFromGalleryTAG", "scanImage: " + "OTHERS")
                                        finish()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Image is Not Proper Code", Toast.LENGTH_SHORT).show()
            }


    }


    private fun hasCameraPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            bindCameraUseCases()
        } else {
            Toast.makeText(
                this,
                "Camera permission required",
                Toast.LENGTH_LONG
            ).show()
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    @SuppressLint("RestrictedApi", "UnsafeOptInUsageError")
    private fun bindCameraUseCases() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val previewUseCase = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cameraView!!.surfaceProvider)
                }
            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()


            val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS
            ).build()

            val scanner = BarcodeScanning.getClient(options)

            val analysisUseCase = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(Size(1280, 720))
                .build()
            

            analysisUseCase.setAnalyzer(Executors.newSingleThreadExecutor()) {
                    processImageProxy(scanner, it)
            }

            cameraView!!.keepScreenOn

            cameraProvider.unbindAll()

            try {
                camera = cameraProvider!!.bindToLifecycle(
                    this,
                    cameraSelector,
                    previewUseCase,
                    analysisUseCase
                )

             /*   val exposureState = camera!!.cameraInfo.exposureState
                Log.d("AWBTAG", "bindCameraUseCases: "+"AWB"+exposureState.exposureCompensationIndex+","+exposureState.exposureCompensationRange)

                if (!exposureState.isExposureCompensationSupported) return@addListener
                val range = exposureState.exposureCompensationRange
                if (range.contains(3)){
                    camera!!.cameraControl.setExposureCompensationIndex(3)
                    Log.d("AWBTAG", "bindCameraUseCases: "+"AWB Start")
                }
                camera!!.cameraInfo.exposureState.exposureCompensationStep.toFloat() * 50
*/

       /*         camera!!.cameraControl.setLinearZoom(20/ 100.toFloat())
                camera!!.cameraControl.setExposureCompensationIndex(4)
                camera!!.cameraInfo.exposureState.exposureCompensationStep.toFloat() * 50*/


                camera!!.cameraControl.setLinearZoom(15/ 100.toFloat())
                val x: Int = cameraView!!.width/2
                val y: Int = cameraView!!.height/ 2
                val factory = cameraView!!.meteringPointFactory
                val point = factory.createPoint(x.toFloat(), y.toFloat())
                val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                    .addPoint(point, FocusMeteringAction.FLAG_AE)
                    .addPoint(point, FocusMeteringAction.FLAG_AWB)
                    .build()

                val future = camera!!.cameraControl.startFocusAndMetering(action)
                future.addListener(
                    {
                        try {
                            val result = future.get()
                            Log.d("tag", "Focus Success: ${result.isFocusSuccessful}")
                        } catch (e: ExecutionException) {
                            Log.e("tag", "Focus failed", e)
                        } catch (e: InterruptedException) {
                            Log.e("tag", "Focus interrupted", e)
                        }
                    }, Executors.newSingleThreadExecutor()
                )

            } catch (illegalStateException: IllegalStateException) {
                Log.e("TAG", illegalStateException.message.orEmpty())
            } catch (illegalArgumentException: IllegalArgumentException) {
                Log.e("TAG", illegalArgumentException.message.orEmpty())
            }

        }, ContextCompat.getMainExecutor(this))
    }



    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        imageProxy.image?.let {
            val ori: Bitmap = imageProxy.toBitmap()!!
            val img = InputImage.fromMediaImage(imageProxy.image!!, 0)
            barcodeScanner.process(img)
                .addOnSuccessListener { barcodeList ->
                    val intent = Intent()
                    CoroutineScope(Dispatchers.IO).launch {
                        for (barcode in barcodeList) {
                            when (barcode.valueType) {
                                Barcode.TYPE_WIFI -> {
                                    val bbox = barcode.boundingBox
                                    if (bbox!!.left>=30 &&bbox.top>=30 && bbox.left + bbox.width()+120 <= ori.width && bbox.top+bbox.height()+120<=ori.height) {
                                        croppedBmp= Bitmap.createBitmap(
                                           ori,
                                           bbox.left-30,
                                           bbox.top-30,
                                           bbox.width()+60,
                                           bbox.height()+60
                                       )

                                        val ssid = barcode.wifi!!.ssid
                                        val password = barcode.wifi!!.password
                                        val type = barcode.wifi!!.encryptionType
                                        withContext(Dispatchers.Main) {
                                            if (check1) {
                                                Toast.makeText(
                                                    this@MainActivity2,
                                                    "::$ssid,$password,$type",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                check1 = false
                                            }
                                            intent.putExtra(
                                                "barcodeWifi",
                                                WifiModel(ssid, password, type,Bitmap.createScaledBitmap(croppedBmp!!, 200, 200, true))
                                            )
                                            setResult(RESULT_OK, intent)
                                            finish()
                                        }

                                    }

                                }
                                Barcode.TYPE_URL -> {
                                    val bbox = barcode.boundingBox
                                    if (bbox!!.left>=30 &&bbox.top>=30 && bbox.left + bbox.width()+120 <= ori.width && bbox.top+bbox.height()+120<=ori.height) {
                                        croppedBmp = Bitmap.createBitmap(
                                           ori,
                                           bbox.left-30,
                                           bbox.top-30,
                                           bbox.width()+60,
                                           bbox.height()+60
                                       )
                                        val title = barcode.url!!.title
                                        val url = barcode.url!!.url
                                        withContext(Dispatchers.Main) {
                                            if (check1) {
                                                Toast.makeText(
                                                    this@MainActivity2,
                                                    "::$title,$url",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                check1 = false
                                            }
                                            intent.putExtra("barcodeUrl", UrlModel(title, url, Bitmap.createScaledBitmap(croppedBmp!!, 200, 200, true)))
                                            setResult(RESULT_OK, intent)
                                            finish()
                                        }

                                    }

                                }
                                else -> {
                                    val bbox = barcode.boundingBox
                                    if(bbox!!.left>=30 &&bbox.top>=30 && bbox.left + bbox.width()+120 <= ori.width && bbox.top+bbox.height()+120<=ori.height){
                                        croppedBmp = Bitmap.createBitmap(
                                            ori,
                                            bbox.left-30,
                                            bbox.top-30,
                                            bbox.width()+60,
                                            bbox.height()+60
                                        )

                                        val txt = barcode.displayValue
                                        withContext(Dispatchers.Main) {
                                            if (check1) {
                                                Toast.makeText(
                                                    this@MainActivity2,
                                                    "::$txt",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                check1 = false
                                            }
                                            intent.putExtra("barcodeTxt", textModel(txt, Bitmap.createScaledBitmap(croppedBmp!!, 300, 300, true)))
                                            setResult(RESULT_OK, intent)
                                            finish()
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
                .addOnFailureListener {
                    Log.e("ImgTAG", it.message.orEmpty())
                }.addOnCompleteListener {
                    imageProxy.image?.close()
                    imageProxy.close()
                }
        }
    }



    private fun toggleFrontBackCamera() {
        if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
            lensFacing = CameraSelector.LENS_FACING_BACK
            Log.d("cameraFacingTAG", "toggleFrontBackCamera: " + "back")
        } else {
            lensFacing = CameraSelector.LENS_FACING_FRONT
            Log.d("cameraFacingTAG", "toggleFrontBackCamera: " + "front")
        }
        bindCameraUseCases()
    }


    private fun ImageProxy.toBitmap(): Bitmap? {
        val nv21 = yuv420888ToNv21(this)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        return yuvImage.toBitmap()
    }

    private fun YuvImage.toBitmap(): Bitmap? {
        val out = ByteArrayOutputStream()
        if (!compressToJpeg(Rect(0, 0, width, height), 20, out))
            return null
        val imageBytes: ByteArray = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun yuv420888ToNv21(image: ImageProxy): ByteArray {
        val pixelCount = image.cropRect.width() * image.cropRect.height()
        val pixelSizeBits = ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888)
        val outputBuffer = ByteArray(pixelCount * pixelSizeBits / 8)
        imageToByteBuffer(image, outputBuffer, pixelCount)
        return outputBuffer
    }

    private fun imageToByteBuffer(image: ImageProxy, outputBuffer: ByteArray, pixelCount: Int) {
        assert(image.format == ImageFormat.YUV_420_888)

        val imageCrop = image.cropRect
        val imagePlanes = image.planes

        imagePlanes.forEachIndexed { planeIndex, plane ->
            val outputStride: Int
            var outputOffset: Int

            when (planeIndex) {
                0 -> {
                    outputStride = 1
                    outputOffset = 0
                }
                1 -> {
                    outputStride = 2
                    outputOffset = pixelCount + 1
                }
                2 -> {
                    outputStride = 2
                    outputOffset = pixelCount
                }
                else -> {
                    return@forEachIndexed
                }
            }

            val planeBuffer = plane.buffer
            val rowStride = plane.rowStride
            val pixelStride = plane.pixelStride
            val planeCrop = if (planeIndex == 0) {
                imageCrop
            } else {
                Rect(
                    imageCrop.left / 2,
                    imageCrop.top / 2,
                    imageCrop.right / 2,
                    imageCrop.bottom / 2
                )
            }

            val planeWidth = planeCrop.width()
            val planeHeight = planeCrop.height()
            val rowBuffer = ByteArray(plane.rowStride)

            val rowLength = if (pixelStride == 1 && outputStride == 1) {
                planeWidth
            } else {

                (planeWidth - 1) * pixelStride + 1
            }

            for (row in 0 until planeHeight) {
                planeBuffer.position(
                    (row + planeCrop.top) * rowStride + planeCrop.left * pixelStride
                )
                if (pixelStride == 1 && outputStride == 1) {

                    planeBuffer.get(outputBuffer, outputOffset, rowLength)
                    outputOffset += rowLength
                } else {
                    planeBuffer.get(rowBuffer, 0, rowLength)
                    for (col in 0 until planeWidth) {
                        outputBuffer[outputOffset] = rowBuffer[col * pixelStride]
                        outputOffset += outputStride
                    }
                }
            }
        }
    }






}

