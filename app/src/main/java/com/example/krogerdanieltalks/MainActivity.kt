package com.example.krogerdanieltalks

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.krogerdanieltalks.apiModels.token.Token
import com.example.krogerdanieltalks.ui.theme.KrogerDanielTalksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KrogerDanielTalksTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    KrogerData(
                        modifier = Modifier.padding(innerPadding)
                    )

                  //Log.d("Token: ", "My Token: $Token.access_token")
                }
            }
        }
    }
}

@Composable
fun KrogerData(modifier: Modifier = Modifier, viewModel: MyViewModel = viewModel()) {

    val data = viewModel.krogerData.observeAsState().value


    if(data != null)
        Text(
        text = data,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KrogerDanielTalksTheme {
        KrogerData()
    }
}