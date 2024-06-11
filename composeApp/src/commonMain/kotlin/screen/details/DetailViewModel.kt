package screen.details

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import apiClient.httpClient
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.LoadingState
import data.Product
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

typealias MutableTasks = MutableState<Product>
typealias Tasks = MutableState<Product>

class DetailViewModel :  ScreenModel {
    private val _detail_product: MutableTasks =  mutableStateOf(Product(1, "", null, ""))
    val detail_product: Tasks = _detail_product
    private val _state = MutableStateFlow(LoadingState())
    val state: StateFlow<LoadingState> = _state
    
    
    fun getDetailProduct(id: Int) {
        _state.update { it.copy(loading = true) }
        screenModelScope.launch(Dispatchers.Main) {
            delay(500)
            val detailProduct = getDetailProductsApi(id)
            _detail_product.value = detailProduct
            _state.update { it.copy(loading = false) }
        }
        
    }
    
    fun clearState() {
        _detail_product.value = Product(0, "", null, "")
    }
    
    fun onCleared() {
        httpClient.close()
    }

    override fun onDispose() {
        super.onDispose()
    }

    private suspend fun getDetailProductsApi(id: Int): Product {
        val response = httpClient.get("https://fakestoreapi.com/products/${id}")
        return response.body<Product>()
    }
}