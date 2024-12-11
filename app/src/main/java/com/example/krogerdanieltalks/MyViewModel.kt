package com.example.krogerdanieltalks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krogerdanieltalks.Client.RetrofitClient
import com.example.krogerdanieltalks.utils.Constants.Companion.LOCATION_ID_ARL_HEIGHTS
import kotlinx.coroutines.launch

class MyViewModel : ViewModel(){

    private val _krogerData = MutableLiveData("No Data")
    private val _accessToken = MutableLiveData("No data for access token")
    private val _productId = MutableLiveData("No data for product upc search")
    private val _productTerm = MutableLiveData("No data for product term search")
    val krogerData: LiveData<String> get() = _krogerData
    val accessToken: LiveData<String> get() = _accessToken
    val productId: LiveData<String> get() = _productId
    val productTerm: LiveData<String> get() = _productTerm

    init {
        viewModelScope.launch {
            getToken()
            //getProduct()
            searchProductByTerm()

        }
    }

    suspend fun getToken()
    {
        _krogerData.value = RetrofitClient.krogerAPIService.getToken("client_credentials", "product.compact").toString()
        _accessToken.value = RetrofitClient.krogerAPIService.getToken("client_credentials", "product.compact").access_token

    }

    suspend fun getProduct()
    {
        _productId.value = RetrofitClient.krogerAPIService.
        getProduct("0001111041600", "Bearer ${_accessToken.value!!}",
            LOCATION_ID_ARL_HEIGHTS).toString()
    }

    suspend fun searchProductByTerm()
    {
        _productTerm.value = RetrofitClient.krogerAPIService
            .searchProductByTerm("Bearer ${_accessToken.value!!}",
                "Kroger", "Nuts", LOCATION_ID_ARL_HEIGHTS).toString()
    }

}