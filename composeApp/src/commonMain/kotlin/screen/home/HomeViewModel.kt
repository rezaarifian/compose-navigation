package screen.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import apiClient.httpClient
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.LoadingState
import data.Product
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

typealias MutableTasks = MutableState<List<Product>>
typealias Tasks = MutableState<List<Product>>
class HomeViewModel:  ScreenModel {
    private var _products: MutableTasks = mutableStateOf(listOf())
    val products: Tasks = _products
    
    private val _state = MutableStateFlow(LoadingState())
    val state: StateFlow<LoadingState> = _state
    
    init {
        _state.update { it.copy(loading = true) }
        getProduct()
    }
    
    private fun getProduct() {
        screenModelScope.launch(Dispatchers.Main) {
            delay(500)
            val product = getProductsApi()
            _products.value = product
            _state.update { it.copy(loading = false) }
        }
        
    }
    
    fun onCleared() {
        httpClient.close()
    }

    override fun onDispose() {
        super.onDispose()
    }

    private suspend fun getProductsApi(): List<Product> {
        val response = httpClient.get("https://fakestoreapi.com/products")
        return response.body<List<Product>>()
    }
}