package com.dicoding.picodiploma.tresh20

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.tresh20.databinding.ActivityPredictBinding
import com.dicoding.picodiploma.tresh20.ml.RnRv3model
import com.dicoding.picodiploma.tresh20.model.SharedViewModel
import com.dicoding.picodiploma.tresh20.model.ViewModelFactory
import com.dicoding.picodiploma.tresh20.pref.UserPreference
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Suppress("DEPRECATION")
class PredictActivity : AppCompatActivity() {
    private lateinit var selectImageButton: Button
    private lateinit var makePrediction: Button
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var bitmap: Bitmap
    private lateinit var camerabtn: Button
    private lateinit var binding: ActivityPredictBinding
    private lateinit var sharedViewMod: SharedViewModel

    private fun checkandGetpermissions() {
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 100)
            }
        } else {
            Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectImageButton = binding.btnGallery
        makePrediction = binding.btnPredict
        imageView = binding.imgPreview
        textView = binding.tvResult
        camerabtn = binding.btnCamera

        // handling permissions
        setupViewModel()
        checkandGetpermissions()

        selectImageButton.setOnClickListener {
            Log.d("msg", "button pressed")
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(intent, 250)
        }
        camerabtn.setOnClickListener {
            val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(camera, 200)
        }

        makePrediction.setOnClickListener {
            classifyImage(bitmap)
        }
    }
    private fun setupViewModel() {
        sharedViewMod = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[SharedViewModel::class.java]
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_language -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
                return true
            }

            R.id.menu_logout -> {
                sharedViewMod.logout()
                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return true
    }

    private fun classifyImage(bitmap: Bitmap) {
        val imageSizeX = 224
        val imageSizeY = 224
        val labels =
            application.assets.open("labels.txt").bufferedReader().use { it.readText() }.split("\n")
        val resized = Bitmap.createScaledBitmap(bitmap, imageSizeX, imageSizeY, false)
        val model = RnRv3model.newInstance(this.applicationContext)

        val tbuffer = TensorImage.fromBitmap(resized)

        val byteBuffer = ByteBuffer.allocateDirect(4 * imageSizeX * imageSizeY * 3)
        byteBuffer.put(tbuffer.buffer)

        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val max = getMax(outputFeature0.floatArray)

        textView.text = labels[max]
        model.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 250) {
            imageView.setImageURI(data?.data)

            val uri: Uri? = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            bitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(bitmap)
        }

    }

    private fun getMax(arr: FloatArray): Int {
        var ind = 0
        var min = 0.0f
        for (i in arr.indices) {
            if (arr[i] > min) {
                min = arr[i]
                ind = i
                Log.d("it", "getIndex : ${arr[i]}")
            }
        }
        return ind
    }
}