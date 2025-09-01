package com.simdea.deeplinktester.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URI

class ParameterEditorViewModel : ViewModel() {

    private val _parameters = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val parameters: StateFlow<List<Pair<String, String>>> = _parameters.asStateFlow()

    private var baseUri: String = ""

    fun setDeeplink(deeplink: String) {
        viewModelScope.launch {
            try {
                val uri = URI(deeplink)
                baseUri = URI(uri.scheme, uri.authority, uri.path, null, uri.fragment).toString()
                val query = uri.query ?: ""
                val params = query.split("&").filter { it.isNotBlank() }.map {
                    val parts = it.split("=")
                    parts[0] to if (parts.size > 1) parts[1] else ""
                }
                _parameters.value = params
            } catch (e: Exception) {
                // Handle invalid URI
                baseUri = deeplink
                _parameters.value = emptyList()
            }
        }
    }

    fun addParameter() {
        _parameters.value = _parameters.value + ("" to "")
    }

    fun updateParameter(index: Int, key: String, value: String) {
        val newList = _parameters.value.toMutableList()
        newList[index] = key to value
        _parameters.value = newList
    }

    fun removeParameter(index: Int) {
        val newList = _parameters.value.toMutableList()
        newList.removeAt(index)
        _parameters.value = newList
    }

    fun buildDeeplink(): String {
        val query = _parameters.value.joinToString("&") { "${it.first}=${it.second}" }
        return if (query.isNotBlank()) {
            "$baseUri?$query"
        } else {
            baseUri
        }
    }
}
