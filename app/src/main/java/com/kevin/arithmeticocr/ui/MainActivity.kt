package com.kevin.arithmeticocr.ui

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.kevin.arithmeticocr.BuildConfig
import com.kevin.arithmeticocr.R
import com.kevin.arithmeticocr.databinding.ActivityMainBinding
import com.kevin.arithmeticocr.util.DB
import com.kevin.arithmeticocr.util.FILE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    private val adapter: MainItemAdapter by lazy {
        MainItemAdapter()
    }

    val openImagePickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri: Uri? ->
        imageUri?.let {
            viewModel.findEquation(this, it)
        }
    }

    val openCamLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            viewModel.findEquation(this, tempUri)
        }
    }

    val requestImagePermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        val isAllowed = it.all { true }

        if (isAllowed) {
            viewModel.getAllEquation()

            if (BuildConfig.FLAVOR_version.equals("camera"))
                tempUri = initTempUri()
        } else {
            finishAffinity()
        }
    }

    private lateinit var tempUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestImagePermission.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE) )
        } else {
            requestImagePermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE) )

        }

        initFlow()

        binding.rgType.setOnCheckedChangeListener { radioGroup, id ->
            when (id) {
                binding.rbFile.id -> viewModel.setCurrentType(FILE)
                else -> viewModel.setCurrentType(DB)
            }
        }

        binding.rvResult.adapter = adapter

        binding.fabAdd.setOnClickListener {
            actionAdd()
        }
    }

    private fun initFlow() {
        lifecycleScope.launch {
            viewModel.result.consumeEach {
                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.type.collectLatest {
                when (it) {
                    FILE -> binding.rbFile.isChecked = true
                    else -> binding.rbDb.isChecked = true
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.equations.collect {
                adapter.setItems(it)
            }
        }
    }

    private fun chooseImage() {
        openImagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun pickFromCamera() {
        openCamLauncher.launch(tempUri)
    }

    private fun initTempUri(): Uri {
        val tempImagesDir = File(
            applicationContext.filesDir,
            getString(R.string.temp_images_dir))

        tempImagesDir.mkdir()

        val tempImage = File(
            tempImagesDir,
            getString(R.string.temp_image))

        return FileProvider.getUriForFile(
            applicationContext,
            getString(R.string.authorities),
            tempImage)
    }

    private fun actionAdd() {
        if (BuildConfig.FLAVOR_version.equals("file"))
            chooseImage()
        else
            pickFromCamera()
    }
}