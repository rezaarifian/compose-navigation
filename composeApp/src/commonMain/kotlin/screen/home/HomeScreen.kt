package screen.home

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import screen.details.DetailsScreen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import component.LoadingScreen
import data.Product
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import screen.details.DetailViewModel

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel =  navigator.rememberNavigatorScreenModel { HomeViewModel() }
        val products by viewModel.products
        val state by viewModel.state.collectAsState()
        
        BoxWithConstraints {
            val scope = this
            val maxWidth = scope.maxWidth

            var cols = 2
            if (maxWidth > 840.dp) {
                cols = 3
            }

            val scrollState = rememberLazyGridState()
            val coroutineScope = rememberCoroutineScope()

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.loading) {
                    LoadingScreen()
                }  else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(cols),
                        state = scrollState,
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.draggable(orientation = Orientation.Vertical, state = rememberDraggableState { delta ->
                            coroutineScope.launch {
                                scrollState.scrollBy(-delta)
                            }
                        })
                    ) {
                        item(span = { GridItemSpan(cols) }) {
                            Column {
                                SearchBar(
                                    modifier = Modifier.fillMaxWidth(),
                                    query = "",
                                    active = false,
                                    onActiveChange = {},
                                    onQueryChange = {},
                                    onSearch = {},
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search"
                                        )
                                                },
                                    placeholder = { Text("Search Products") }
                                ) {}
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                        
                        items(products) { product ->
                            CardCell(product)
                        }
                        item {
                            Spacer(modifier = Modifier.height(40.dp)) // Add spacer at the end
                        }
                    }   
                }
            }
        }
    }
    
    @Composable
    fun CardCell(product: Product) {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel =  navigator.rememberNavigatorScreenModel { DetailViewModel() }
        Card(
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    viewModel.clearState()
                    viewModel.getDetailProduct(product.id)
                    navigator.push(DetailsScreen())
                },
            elevation = 2.dp
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    val snackbarHostState = remember { SnackbarHostState() }
                    KamelImage(
                        resource = asyncPainterResource(product.image.toString()),
                        contentDescription = product.description,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().aspectRatio(1.0f),
                        onLoading = { progress -> CircularProgressIndicator(progress) },
                        onFailure = { exception ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = exception.message.toString(),
                                    actionLabel = "Hide",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                    SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(16.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = product.title,
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.wrapContentWidth()
                            .padding(horizontal = 16.dp).heightIn(min = 40.dp)
                        )
                }
            }
    }

}