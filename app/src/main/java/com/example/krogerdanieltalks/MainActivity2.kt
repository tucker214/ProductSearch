package com.example.krogerdanieltalks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewModelScope
import com.example.krogerdanieltalks.ui.theme.KrogerDanielTalksTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import com.example.krogerdanieltalks.utils.Constants.Companion.c_upc


class MainActivity2 : ComponentActivity() {
    companion object{
        var barcodeTest = "empty"
    }

    private val viewModel: MyViewModel = MyViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MainActivity2::class.java)


        enableEdgeToEdge()
        setContent {
            KrogerDanielTalksTheme {
            }
        }
        barcodeTest = registerUiListener()
        intent.putExtra("upcA", barcodeTest)
        setResult(RESULT_OK, intent)
        finish()

    }

    private fun registerUiListener() : String
    {
        var upc = "testing"
        val scannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ){

            result ->

        if (result.contents == null) {
            upc =  "0000000000000"
        } else if (result.contents != null){
            upc = buildString {
                append("00")
                append(result.contents.substring(0, result.contents.length))
            }
        }

            upc = "wtf is going on"
    }
        upc = "wtf is going on"
        scannerLauncher.launch(
            ScanOptions().setPrompt("Scan Barcode")
            .setBeepEnabled(true)
            .setDesiredBarcodeFormats(ScanOptions.UPC_A)
                .setCameraId(0)
                .addExtra("upcA", barcodeTest)
                )

        return upc
    }



    override fun onResume()
    {
        super.onResume()
        viewModel.viewModelScope.launch { viewModel.getToken() }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

