package com.example.krogerdanieltalks

import android.content.SharedPreferences
import android.graphics.Paint.Align
import android.os.Bundle
import android.util.Log
import android.widget.Scroller
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.krogerdanieltalks.apiModels.token.productTerm.Data
import com.example.krogerdanieltalks.ui.theme.KrogerDanielTalksTheme
import kotlinx.coroutines.launch


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
    var newSearch = false
    var isRefresh = false
    var canSearch = true
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
                   .padding(0.dp, 30.dp, 0.dp, 15.dp)
           ) {
               OutlinedTextField(value = itemTerm.value, onValueChange = { text ->
                   itemTerm.value = text
               },
                   Modifier.width(240.dp),
                   textStyle = TextStyle(color = Color.Black),
                   placeholder = {Text("Enter item to search")},
                   trailingIcon = {Icon(Icons.Default.Clear,
                       contentDescription = "clear text",
                       modifier = Modifier.clickable { itemTerm.value = "" },
                       tint = Color.Black)}


               )
               Spacer(modifier = Modifier.width(10.dp))

               Button(onClick = {
                   if (itemTerm.value.isNotEmpty())
                       if (itemTerm.value.length >= 3) {
                           viewModel.setTerm(itemTerm.value)
                           newSearch = true
                           canSearch = true
                       }
                   else{
                       canSearch = false
                   }
               }) {
                   Text(text = "Search",
                       maxLines = 1,
                       fontSize = 15.sp

                   )
               }
           }

           productTermData = refreshData(viewModel, productTermData)

       LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(10.dp))
       {

           items(1)
           { item ->
               for (i in 0..<productTermData!!.size) {
                   //pop up box
                   var showPopUp = remember { mutableStateOf(false) }

                   var perspectiveIndex: Int = -1
                   var sizeIndex: Int = 2
                   var hasAisle = true
                   try {
                       for (j in 0..<productTermData!![i].images!!.size) {
                           test = productTermData!![i].images!![j].perspective.toString()
                           perspectiveIndex = test.indexOf("front", 0, true)

                           if (perspectiveIndex >= 0) {
                               perspectiveIndex = j
                               break
                           }
                       }

                       productMediumImageUrl = productTermData!![i].images!![perspectiveIndex]
                           .sizes!![sizeIndex].url.toString()

                       if (productTermData!![i].aisleLocations!!.isEmpty())
                               hasAisle = false;


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
                        val shape = RoundedCornerShape(30.dp)
                       Card(
                           colors = CardDefaults.cardColors(containerColor = Color.White),
                           modifier = Modifier
                               .padding(0.dp, 5.dp)
                               .border(1.dp, Color.LightGray, shape)
                               .clip(shape)
                               .width(400.dp)
                               .then(Modifier.clickable {
                                   Modifier.wrapContentSize()
                                   showPopUp.value = !showPopUp.value
                               })
                       )
                       {
                           if (productTermData!![i].items != null) {
                               if (productTermData!![i].items!![0].inventory != null) {
                                   var stockLevel =
                                       productTermData!![i].items!![0].inventory!!.stockLevel
                                   if (productTermData!![i].items!![0].inventory!!.stockLevel!!.contains("HIGH"))
                                   {
                                       stockLevel = "HIGH"
                                   }
                                   else if (productTermData!![i].items!![0].inventory!!.stockLevel!!.contains("LOW"))
                                   {
                                       stockLevel = "LOW"
                                   }
                                   else if (productTermData!![i].items!![0].inventory!!.stockLevel!!.contains("TEMPORARILY"))
                                   {
                                       stockLevel = "OUT OF"
                                   }
                                   else
                                   {
                                       stockLevel = "UNAVAILABLE"
                                   }

                                   Row(Modifier.padding(30.dp, 20.dp, 0.dp, 0.dp)) {
                                       Text(
                                           text = "$stockLevel STOCK"
                                       )
                                   }

                               }
                           }
                           Row(

                           ) {
                               AsyncImage(
                                   model = productMediumImageUrl,
                                   contentDescription = null,
                                   modifier = Modifier
                                       .size(140.dp)
                                       .aspectRatio(1f)
                                       .align(Alignment.CenterVertically)
                                       .padding(30.dp, 30.dp, 30.dp, 0.dp)
                               )
                               Text(
                                   fontWeight = FontWeight.Bold,
                                   text = productTermData!![i].description.toString(),
                                   maxLines = 2,
                                   overflow = TextOverflow.Ellipsis,
                                   modifier = modifier
                                       .padding(0.dp, 30.dp, 10.dp, 0.dp)
                                       .wrapContentHeight(align = Alignment.CenterVertically)
                               )
                           }
                           Row(
                               modifier = Modifier
                                   .wrapContentHeight()
                                   .wrapContentWidth()
                                   .align(Alignment.CenterHorizontally)
                           ) {
                               if (hasAisle) {
                                       Text(
                                           fontWeight = FontWeight.Bold,
                                           text = "A: " + productTermData!![i].aisleLocations!![0].number.toString()
                                                   + " B: " + productTermData!![i].aisleLocations!![0].side.toString()
                                                   + "-" + productTermData!![i].aisleLocations!![0].bayNumber.toString()
                                                   + " S: " + productTermData!![i].aisleLocations!![0].shelfNumber.toString()
                                                   + " P: " + productTermData!![i].aisleLocations!![0].shelfPositionInBay.toString(),
                                           fontSize = 14.sp,
                                           modifier = Modifier
                                               .padding(10.dp, 0.dp, 10.dp, 10.dp)
                                               .wrapContentHeight()
                                               .wrapContentWidth()
                                               .align(Alignment.Bottom)
                                       )
                               } else {
                                   if (productTermData!![i].taxonomies == null)

                                       Text(
                                           fontWeight = FontWeight.Bold,
                                           text = "DEP: " + "GROCERY",
                                           fontSize = 14.sp,
                                           modifier = Modifier
                                               .padding(10.dp, 0.dp, 10.dp, 10.dp)
                                               .wrapContentHeight()
                                               .align(Alignment.Bottom)
                                       )
                               }
                                   Text(
                                       fontWeight = FontWeight.Bold,
                                       text = productTermData!![i].upc.toString(),
                                       fontSize = 14.sp,
                                       modifier = Modifier
                                           .padding(0.dp, 0.dp, 10.dp, 10.dp)
                                           .wrapContentHeight()
                                           .wrapContentWidth()
                                           .align(Alignment.Bottom)

                                   )
                           }
                       }


                   if(showPopUp.value)
                   {
                       Box()
                       {

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

fun resetScroll()
{

}