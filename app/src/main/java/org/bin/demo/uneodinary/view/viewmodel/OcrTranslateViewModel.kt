package org.bin.demo.uneodinary.view.viewmodel

import android.graphics.Bitmap
import android.util.Log
import android.view.translation.Translator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.bin.demo.uneodinary.UApplication.Companion.LOG_TAG
import org.bin.demo.uneodinary.UApplication.Companion.getUAppContext

class OcrTranslateViewModel : ViewModel() {

    private val _ocrText = MutableStateFlow("추출된 텍스트:")
    val ocrText: StateFlow<String> = _ocrText.asStateFlow()

    private val _translatedText = MutableStateFlow("번역된 텍스트:")
    val translatedText: StateFlow<String> = _translatedText.asStateFlow()

    val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.KOREAN)
        .setTargetLanguage(TranslateLanguage.ENGLISH)
        .build()
    val englishGermanTranslator = Translation.getClient(options)


    val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    private lateinit var translator: Translator

    init {
        setupTranslator()
    }

    private fun setupTranslator() {

    }

    suspend fun processImageAndTranslate(bitmap: Bitmap) {
        Log.d(LOG_TAG, "processImageAndTranslate !")
        viewModelScope.launch {
            try {
                val image = InputImage.fromBitmap(bitmap, 0)

                // 1. OCR 텍스트 인식
                val recognizedText = recognizeText(image)
                Log.d(LOG_TAG, "koreanText: $recognizedText !")
                val prompt = buildString {
                    append("다음 항목을 카테고리로 분류해줘, 카테고리 목록: 식비, 카페, 교통, 통신비, 쇼핑, 편의점, 기타")
                    append(recognizedText)
                }

                var conditions = DownloadConditions.Builder()
                    .requireWifi()
                    .build()
                englishGermanTranslator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        englishGermanTranslator.translate(recognizedText)
                            .addOnSuccessListener { translatedText ->
                                Log.d(LOG_TAG, "translatedText: $translatedText !")

                            }
                            .addOnFailureListener { exception ->
                                // Error.
                                // ...
                            }
                    }
                    .addOnFailureListener { exception ->
                        // Model couldn’t be downloaded or other internal error.
                        // ...
                    }


                _ocrText.value = "추출된 텍스트:\n$recognizedText"
//                if (koreanText.isNotEmpty()) {
//                    // 2. 번역 (모델이 다운로드되어 있어야 함)
//                    val englishText = translator.translate(koreanText).await()
//                    _translatedText.value = "번역된 텍스트 (영어):\n$englishText"
//                }

            } catch (e: Exception) {
                e.printStackTrace()
                _translatedText.value = "오류 발생: ${e.message}"
            }
        }
    }


    suspend fun recognizeText(image: InputImage): String {
        val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
        val result = recognizer.process(image).await()  // Task를 suspend로 변환
        return result.text
    }
    override fun onCleared() {
        super.onCleared()
    }
}


private fun translateToEnglish(koreanText: String, callback: (String) -> Unit) {
    val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.KOREAN)
        .setTargetLanguage(TranslateLanguage.ENGLISH)
        .build()

    val translator = Translation.getClient(options)

    // Download model if needed
    translator.downloadModelIfNeeded()
        .addOnSuccessListener {
            translator.translate(koreanText)
                .addOnSuccessListener { translatedText ->
                    callback(translatedText)
                }
                .addOnFailureListener { e ->
                    Log.e("Translate", "Translation failed", e)
                    callback(koreanText) // fallback
                }
        }
        .addOnFailureListener { e ->
            Log.e("Translate", "Model download failed", e)
            callback(koreanText) // fallback
        }
}

