package com.example.krogerdanieltalks

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
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
    if(productTerm != null) {
        Text(
            text = productTerm,
            modifier = modifier
        )
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KrogerDanielTalksTheme {
        KrogerData()
    }
}
