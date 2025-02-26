package com.example.krogerdanieltalks

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint.Align
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Scroller
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.substring
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.krogerdanieltalks.apiModels.token.productTerm.Data
import com.example.krogerdanieltalks.ui.theme.KrogerDanielTalksTheme
import com.example.krogerdanieltalks.utils.Constants.Companion.c_upc
import kotlinx.coroutines.launch
import com.google.zxing.*
import com.google.zxing.client.android.Intents.Scan
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import org.w3c.dom.Text
import java.lang.reflect.TypeVariable
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

lateinit var zxing: String
class MainActivity : ComponentActivity() {
    private val viewModel: MyViewModel = MyViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
                result: ActivityResult ->

            if (result.resultCode == RESULT_OK)
            {
                val intent = result.data
                zxing = intent?.getStringExtra("upcA")!!.toString()
                Log.d("part-1", zxing)
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KrogerDanielTalksTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var shouldScan = remember { mutableStateOf(false) }

                    KrogerData(
                        modifier = Modifier.padding(innerPadding), viewModel = viewModel(), shouldScan
                    )
                    if (shouldScan.value)
                    {

                        shouldScan.value = false
                        val data = Intent(this, MainActivity2::class.java)
                        startForResult.launch(data)
                        zxing = data.getStringExtra("upcA").toString()
                        Log.d("part-2", zxing)
                    }
                    val preferences: SharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE)
                    preferences.edit().putString("access_token", viewModel.accessToken.observeAsState().value).apply()

                  Log.d("Token: ", "My Token: ${preferences.getString("access_token", "")}")

                }
            }
        }

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        if (resultCode == RESULT_OK)
        {
            zxing = data?.getStringExtra("upcA")!!
        }

    }

    override fun onResume()
    {
        super.onResume()
        viewModel.viewModelScope.launch { viewModel.getToken() }
    }
}

@Composable
fun KrogerData(modifier: Modifier = Modifier, viewModel: MyViewModel = viewModel(), shouldScan: MutableState<Boolean>) {
    zxing = "testing"
    var itemTerm = remember {
        mutableStateOf("")
    }
    val queries = remember { mutableStateOf(0) }
    val scanLauncher = rememberLauncherForActivityResult(contract = ScanContract(),
        onResult = {result->
            queries.value++
            if (result.contents != null ) {
                when (result.contents.length) {
                    12 -> {
                        viewModel.setTerm(
                            "00" + result.contents.substring(
                                0,
                                result.contents.length - 1
                            )
                        )
                        itemTerm.value = "00" + result.contents.substring(
                            0,
                            result.contents.length - 1
                        )
                    }
                    14 -> {
                        viewModel.setTerm(result.contents.substring(0, result.contents.length - 1))
                        itemTerm.value = result.contents.substring(0, result.contents.length - 1)
                    }
                    13 -> {
                        viewModel.setTerm(result.contents)
                        itemTerm.value = result.contents
                    }
                    8 -> {
                        viewModel.setTerm("00" + result.contents.substring(0, 2) + "00000" +  result.contents.substring(3, 5))
                        itemTerm.value = "00" + result.contents.substring(0, 3) + "00000" +  result.contents.substring(3, 6)
                    }
                    else ->{ itemTerm.value = result.contents }
                }

            }
        })

    var barcode = remember { mutableStateOf(c_upc) }
    var mContext = LocalContext.current
    val productTerm = viewModel.productTerm.observeAsState().value
    var productTermData = viewModel.productTermData.observeAsState().value!!.data
    var productMediumImageUrl = ""
    var test : String
    val scrollToTop by viewModel.shouldResetScroll.observeAsState()
    val listState = rememberLazyListState()
    if (scrollToTop == true)
    {

        LaunchedEffect(key1 = scrollToTop) {
            listState.scrollToItem(0)
            viewModel.updateScrollToTop(false)
        }
    }

   if(productTermData != null) {



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
                   Modifier.size(240.dp, 50.dp),
                   textStyle = TextStyle(color = Color.Black),
                   placeholder = {Text("Search items")},
                   trailingIcon = {
                       if (itemTerm.value.isNotEmpty()) {
                           Icon(
                               Icons.Default.Clear,
                               contentDescription = "clear text",
                               modifier = Modifier.clickable { itemTerm.value = "" },
                               tint = Color.Black
                           )
                       }
                       else
                       {
                           Icon(
                               painter = painterResource(R.drawable.ic_action_name),
                               contentDescription = "clear text",
                               modifier = Modifier.clickable {
                                   scanLauncher.launch(ScanOptions().setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)) },
                               tint = Color.Black,
                           )

                       }
                   },
                   shape = RoundedCornerShape(50),
                   keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                   keyboardActions = KeyboardActions(onSearch =
                   {
                       queries.value++
                       if (itemTerm.value.isNotEmpty())
                           if (itemTerm.value.length >= 3) {
                               viewModel.setTerm(itemTerm.value)
                               viewModel.updateScrollToTop(true)

                           }

                   })
               )
               Spacer(modifier = Modifier.width(10.dp))

               Button(onClick = {
                   queries.value++
                   if (itemTerm.value.isNotEmpty())
                       if (itemTerm.value.length >= 3) {
                           viewModel.setTerm(itemTerm.value)
                           viewModel.updateScrollToTop(true)

                       }
               }) {
                   Text(text = "Search",
                       maxLines = 1,
                       fontSize = 15.sp

                   )
               }
           }

           productTermData = refreshData(viewModel, productTermData)

       LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(10.dp), state =
       listState)
       {

           items(1)
           { item ->
               if (productTermData.isNullOrEmpty() && queries.value > 0)
               {
                   Row (modifier = Modifier
                       .wrapContentSize()
                       .fillMaxSize(),
                       verticalAlignment = Alignment.CenterVertically,
                       horizontalArrangement = Arrangement.Center){
                       Text(
                           "No item found",modifier = Modifier
                               .wrapContentWidth()
                               .wrapContentHeight(), textAlign = TextAlign.Center,
                           fontWeight = FontWeight.SemiBold
                       )
                   }
               }
               else
               for (i in 0..<productTermData!!.size) {

                   //pop up box
                   var showPopUp = remember { mutableStateOf(false) }
                   var stockLevel : String?
                   //stockLevel = "UNAVAILABLE"
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
                                   stockLevel =
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


                                   Row(Modifier.padding(30.dp, 20.dp, 0.dp, 0.dp)) {
                                       Text(
                                           text = "$stockLevel STOCK"
                                       )
                                   }

                               }
                               else
                               {
                                   stockLevel = "UNAVAILABLE"
                                   Row(Modifier.padding(30.dp, 20.dp, 0.dp, 0.dp)) {
                                       Text(
                                           text = "$stockLevel"
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
                           Row (modifier = Modifier.align(Alignment.CenterHorizontally)) {
                               if (productTermData!![i].items != null)
                               {
                                  if (productTermData!![i].items!![0].price != null)
                                  {
                                      Box (modifier = Modifier
                                          .align(Alignment.Bottom)
                                          .background(color = Color.White)
                                          .height(30.dp)
                                          .wrapContentWidth()
                                          .wrapContentHeight()

                                          .align(Alignment.Bottom)
                                      ) {
                                          Text(
                                              "Regular: $" + productTermData!![i].items!![0].price?.regular.toString(),
                                              fontWeight = FontWeight.SemiBold,
                                              fontSize = 14.sp,
                                              modifier = Modifier
                                                  .padding(10.dp, 0.dp, 10.dp, 0.dp)
                                                  .wrapContentHeight()
                                                  .wrapContentWidth()
                                                  .align(Alignment.Center)

                                          )
                                      }

                                        if (productTermData!![i].items!![0].price?.promo?.toInt() != 0) {
                                            Box (modifier = Modifier
                                                .clip(RoundedCornerShape(30))
                                                .align(Alignment.Bottom)
                                                .background(color = Color.Yellow)
                                                .height(30.dp)
                                                .wrapContentWidth()
                                                .wrapContentHeight()

                                                ){
                                                Text(
                                                    "Sale: $" + productTermData!![i].items!![0].price?.promo.toString(),
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 14.sp,
                                                    modifier = Modifier
                                                        .padding(10.dp, 0.dp, 10.dp, 0.dp)
                                                        .wrapContentHeight()
                                                        .wrapContentWidth()
                                                        .align(Alignment.Center)


                                                )
                                            }
                                        }

                                  }
                               }

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

                                   else
                                   {
                                       val department = productTermData!![i].taxonomies!![0].department
                                       Text(
                                           fontWeight = FontWeight.Bold,
                                           text = "DEP: {$department}",
                                           fontSize = 14.sp,
                                           modifier = Modifier
                                               .padding(10.dp, 0.dp, 10.dp, 10.dp)
                                               .wrapContentHeight()
                                               .align(Alignment.Bottom)
                                       )
                                   }
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

/*@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KrogerDanielTalksTheme {
        KrogerData(shouldScan = remember { mutableStateOf(false) } )
    }
}*/

@Composable
fun refreshData(viewModel: MyViewModel, data : List<Data>?) : List<Data>? {
    return viewModel.productTermData.observeAsState().value!!.data

}


