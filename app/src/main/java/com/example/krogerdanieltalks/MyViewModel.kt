package com.example.krogerdanieltalks

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.krogerdanieltalks.Client.RetrofitClient
import com.example.krogerdanieltalks.apiModels.token.Token
import com.example.krogerdanieltalks.utils.Constants
import kotlinx.coroutines.launch

class MyViewModel : ViewModel(){

    private val _krogerData = MutableLiveData("No Data")
    private val _accessToken = MutableLiveData("No Data")
    val krogerData: LiveData<String> get() = _krogerData
    val accessToken: LiveData<String> get() = _accessToken

    init {
        viewModelScope.launch {
            getToken()

        }
    }

    suspend fun getToken()
    {
        _krogerData.value = RetrofitClient.krogerAPIService.getToken("client_credentials", "product.compact").toString()
        _accessToken.value = RetrofitClient.krogerAPIService.getToken("client_credentials", "product.compact").access_token


    }

}