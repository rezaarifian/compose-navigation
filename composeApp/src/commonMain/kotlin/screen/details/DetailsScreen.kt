package screen.details

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import component.LoadingScreen
import data.Product
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch

class DetailsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = navigator.rememberNavigatorScreenModel { DetailViewModel() }
        val detailProduct by viewModel.detail_product
        val state by viewModel.state.collectAsState()
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Detail Product")
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navigator.pop()}
                        ) {
                            Icon(
                                imageVector =  Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back Arror icon"
                            )
                        }
                    }
                )
            }
        ){
            if (state.loading) {
                LoadingScreen() 
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                ){
                    val scrollState = rememberLazyListState()
                    val coroutineScope = rememberCoroutineScope()
                    detailProduct.let { product ->
                        LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        state = scrollState,
                        modifier = Modifier
                            .draggable(orientation = Orientation.Vertical, state = rememberDraggableState { delta ->
                                coroutineScope.launch {
                                    scrollState.scrollBy(-delta)
                                }
                            })
                            .fillMaxSize()
                            .background(color = MaterialTheme.colors.background)
                        ){
                            item {
                                ProductImage(product.image.toString())
                            }
                            item {
                                ProductDetail(product)
                            }
                        }
                    }
                }   
            }
        }
    }
    
    @Composable
    fun ProductImage(imageUrl: String) {
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        KamelImage(
            resource = asyncPainterResource(imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
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
    }
    
    @Composable
    fun ProductDetail(detailProduct: Product) {
        Column(
            modifier  = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ){
            Text(
                text = detailProduct.title,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 0.dp
                )
            ){
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier =  Modifier.width(8.dp))
                Text(text = "Beli Aja Sekarang", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Category ${detailProduct.category}".uppercase(),
                style = MaterialTheme.typography.overline
            )

            Spacer(modifier = Modifier.height(2.dp))
            
            PriceText(dollarPart = "$", pricePart = detailProduct.price.toString())

            Spacer(modifier = Modifier.height(2.dp))
            
            Text(text = detailProduct.description, style = MaterialTheme.typography.body2)
        }
    }
    
    @Composable
    fun PriceText(dollarPart: String, pricePart: String) {
        val priceText = buildAnnotatedString {
            append(dollarPart)
            addStyle(style = SpanStyle(color = Color.Green), start = 0, end = dollarPart.length)
            append(pricePart)
            addStyle(style = SpanStyle(color = Color.Black), start = dollarPart.length, end = dollarPart.length + pricePart.length)
        }
    
        Text(text = priceText)
    }

}