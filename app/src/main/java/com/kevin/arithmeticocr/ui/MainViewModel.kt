package com.kevin.arithmeticocr.ui

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.kevin.arithmeticocr.data.EquationResult
import com.kevin.arithmeticocr.repo.MainRepository
import com.kevin.arithmeticocr.repo.MainRepositoryImpl
import com.kevin.arithmeticocr.util.FILE
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.objecthunter.exp4j.ExpressionBuilder
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    val result = Channel<String>()

    private val _type: MutableStateFlow<String> = MutableStateFlow(FILE)
    val type: StateFlow<String> get() = _type

    private val _equations: MutableStateFlow<List<EquationResult>> = MutableStateFlow(mutableListOf())
    val equations: StateFlow<List<EquationResult>> get() = _equations

    init {
        getCurrentType()
    }

    fun getCurrentType() {
        viewModelScope.launch {
            repository.getCurrentType()?.let {
                _type.emit(it)
            }
        }
    }

    fun setCurrentType(type: String) {
        viewModelScope.launch {
            repository.setCurrentType(type)
            _type.emit(type)
            getAllEquation()
        }
    }

    fun getAllEquation() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val result: List<EquationResult>
                when (repository.getCurrentType()) {
                    FILE -> result = repository.getAllEquationFile()

                    else -> result = repository.getAllEquationDB()
                }

                _equations.emit(result)
            }
        }
    }

    fun addEquation(data: EquationResult) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val result: List<EquationResult>

                when (repository.getCurrentType()) {
                    FILE -> result = repository.insertEquationFile(data)

                    else -> result = repository.insertEquationDB(data)
                }

                _equations.emit(result)
            }
        }
    }

    fun findEquation(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            val image: InputImage
            try {
                image = InputImage.fromFilePath(context, imageUri)
                recognizer.process(image).addOnSuccessListener {
                    val value = processTextImage(it)

                    value?.let {
                        addEquation(it)
                    }
                }.addOnFailureListener {
                    result.trySend("Invalid")
                }
            } catch (e: Exception) {
                result.trySend("Invalid")
                Log.v("Error", e.message.toString())
            }
        }
    }

    private fun processTextImage(texts: Text): EquationResult? {
        for (block in texts.textBlocks) {
            for (line in block.lines) {
                val text = line.text
                text.let {
                    try {
                        val e = ExpressionBuilder(it).build()

                        val validate = e.validate()

                        if (validate.isValid) {
                            return EquationResult(equation = it, result = e.evaluate().toString())
                        }
                    } catch (e: Exception) {

                    }
                }
            }
        }

        return null
    }

}