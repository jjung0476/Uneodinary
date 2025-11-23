package org.bin.demo.uneodinary.view.fragment

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import demo.ocr.camera.interfaces.CameraProvider
import demo.ocr.camera.interfaces.CameraRepository
import demo.ocr.camera.interfaces.ImageFrameListener
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bin.demo.uneodinary.R
import org.bin.demo.uneodinary.databinding.FragmentCameraBinding
import org.bin.demo.uneodinary.view.viewmodel.OcrTranslateViewModel
import org.bin.demo.uneodinary.view.viewmodel.SharedViewModel

@AndroidEntryPoint
class CameraFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val ocrTranslateViewModel: OcrTranslateViewModel by activityViewModels()

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private var cameraRepository: CameraRepository? = null

    val mutex = Mutex()
    val imageProcessor = ImageFrameListener { bitmap, imageMetadata, imageProcessingGate ->
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            mutex.withLock {
                try {
                    bitmap?.let {
                    }
                } finally {
                    imageProcessingGate.signalProcessingFinished()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_camera, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraRepository = CameraProvider.getCameraInstance(binding.root, R.id.koiCameraX)
        cameraRepository?.startCamera(this, true)


        cameraRepository?.registerImageListener(imageProcessor)

        binding.btnCapture.setOnClickListener {
            cameraRepository?.captureImage({ bitmap, image ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                    bitmap?.let {
                        ocrTranslateViewModel.processImageAndTranslate(it)
                        sharedViewModel.onImageCaptured(it)
                    }
                }
            })
        }
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        cameraRepository?.stopCamera()
        Log.d("Lifecycle", "Fragment onDestroy - binding nulled")
    }

    companion object {
        @JvmStatic
        fun newInstance() = CameraFragment()
    }
}