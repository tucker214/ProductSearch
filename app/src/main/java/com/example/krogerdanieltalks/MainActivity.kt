package com.example.krogerdanieltalks

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.krogerdanieltalks.apiModels.token.productTerm.Data
import com.example.krogerdanieltalks.apiModels.token.productTerm.ProductTerm
import com.example.krogerdanieltalks.ui.theme.KrogerDanielTalksTheme


class MainActivity : ComponentActivity() {
    val viewModel: MyViewModel = MyViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KrogerDanielTalksTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    KrogerData(
                        modifier = Modifier.padding(innerPadding), viewModel = viewModel()
                    )

                    val preferences: SharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE)
                    preferences.edit().putString("access_token", viewModel.accessToken.observeAsState().value).apply()

                  Log.d("Token: ", "My Token: ${preferences.getString("access_token", "")}")
                }
            }
        }
    }
}

@Composable
fun KrogerData(modifier: Modifier = Modifier, viewModel: MyViewModel = viewModel()) {

    //val data = viewModel.krogerData.observeAsState().value
    //val token = viewModel.accessToken.observeAsState().value
    //val product = viewModel.productId.observeAsState().value
    val productTerm = viewModel.productTerm.observeAsState().value
    var productTermData = viewModel.productTermData.observeAsState().value!!.data
    var productMediumImageUrl = ""
    var test : String

    var isRefresh = false
   if(productTermData != null) {

       var itemTerm = remember {
           mutableStateOf("")
       }

       Column(
           modifier = Modifier
               .fillMaxSize()
               .padding(16.dp)
       ) {
           Row(
               modifier = Modifier
                   .fillMaxWidth()

           ) {
               OutlinedTextField(value = itemTerm.value, onValueChange = { text ->
                   itemTerm.value = text
               },
                   Modifier.width(240.dp),
                   textStyle = TextStyle(color = Color.Black)


               )
               Spacer(modifier = Modifier.width(10.dp))

               Button(onClick = {viewModel.setTerm(itemTerm.value)
               }) {
                   Text(text = "Search",
                       maxLines = 1,
                       fontSize = 15.sp

                   )


               }
           }

           productTermData = refreshData(viewModel, productTermData)

       LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(10.dp)) {
           items(1)
           { i ->
               for (i in 0..<productTermData!!.size) {

                   var perspectiveIndex: Int = -1
                   var sizeIndex: Int = 2
                   try {
                       for (j in 0..<productTermData!![i].images!!.size) {
                           test = productTermData!![i].images!![j].perspective.toString()
                           perspectiveIndex = test.indexOf("front", 0, true)

                           if (perspectiveIndex >= 0) {
                               //productMediumImageUrl =  productTermData[i].images!![j].sizes!![2].url.toString()
                               perspectiveIndex = j
                               break
                           }
                       }

                       productMediumImageUrl = productTermData!![i].images!![perspectiveIndex]
                           .sizes!![sizeIndex].url.toString()

                   } catch (indexOutOfBounds: IndexOutOfBoundsException) {
                       indexOutOfBounds.printStackTrace()
                       productMediumImageUrl = "No Image"

                       try {
                           sizeIndex = 1
                       } catch (indexOutOfBounds: IndexOutOfBoundsException) {
                           indexOutOfBounds.printStackTrace()
                           productMediumImageUrl = "No Image"
                       }
                   }

                   Card(
                       border = BorderStroke(0.5.dp, Color.LightGray),
                       colors = CardDefaults.cardColors(containerColor = Color.White),
                       modifier = Modifier.padding(0.dp, 5.dp)
                   )
                   {
                       Row(

                       ) {
                           AsyncImage(
                               model = productMediumImageUrl,
                               contentDescription = null,
                               modifier = Modifier
                                   .size(75.dp)
                                   .aspectRatio(1f)
                                   .align(Alignment.CenterVertically)
                                   .padding(10.dp)
                           )
                           Text(
                               fontWeight = FontWeight.Bold,
                               text = productTermData!![i].description.toString(),
                               maxLines = 2,
                               overflow = TextOverflow.Ellipsis,
                               modifier = modifier
                                   .padding(5.dp)
                                   .wrapContentHeight(align = Alignment.CenterVertically)

                           )
                           /*                       Text(
                           fontWeight = FontWeight.Bold,
                           text = productMediumImageUrl,
                           maxLines = 2,
                           overflow = TextOverflow.Ellipsis,
                           modifier = modifier
                               .padding(5.dp)
                               .wrapContentHeight(align = Alignment.CenterVertically)
                       )*/
                       }
                   }
               }
           }
       }
       }
   }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KrogerDanielTalksTheme {
        KrogerData()
//        Column(horizontalAlignment = Alignment.End,
//        verticalArrangement = Arrangement.Center,
//            modifier = Modifier.fillMaxSize()){
//            Text(text= "hello", color = Color.Blue )
//            Text(text= "What's up", color = Color.Blue )
//        }
/*        LazyRow (modifier = Modifier.fillMaxSize()){
            items(10)
            { i ->
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            }
        }*/
    }
}

@Composable
fun refreshData(viewModel: MyViewModel, data : List<Data>?) : List<Data>? {
    return viewModel.productTermData.observeAsState().value!!.data

}