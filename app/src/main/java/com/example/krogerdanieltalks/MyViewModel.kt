package com.example.krogerdanieltalks

import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.substring
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krogerdanieltalks.Client.RetrofitClient
import com.example.krogerdanieltalks.apiModels.token.productTerm.ProductTerm
import com.example.krogerdanieltalks.utils.Constants.Companion.LOCATION_ID_ARL_HEIGHTS
import kotlinx.coroutines.launch
import okhttp3.internal.wait

class MyViewModel : ViewModel(){

    private val _krogerData = MutableLiveData("No Data")
    private val _accessToken = MutableLiveData("No data for access token")
    private val _productId = MutableLiveData("No data for product upc search")
    private val _productTerm = MutableLiveData("No data for product term search")
    private val _productTermData = MutableLiveData(ProductTerm(null, null))
    val krogerData: LiveData<String> get() = _krogerData
    val accessToken: LiveData<String> get() = _accessToken
    val productId: LiveData<String> get() = _productId
    val productTerm: LiveData<String> get() = _productTerm
    val productTermData: LiveData<ProductTerm> get() = _productTermData
    var searchTerm = "Cheese"

    init {
        viewModelScope.launch {
            getToken()
            searchProductByTerm(productTerm.value!!)
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

    suspend fun searchProductByTerm(term: String)
    {
        try {
                _productTermData.value = RetrofitClient.krogerAPIService
                .searchProductByTerm("Bearer ${_accessToken.value!!}",
                    term, LOCATION_ID_ARL_HEIGHTS, "50")
        } catch (e: retrofit2.HttpException)
        {
            e.printStackTrace()
            getToken()
            Log.d("ExceptionInvalidToken", "Invalid Token")
            _productTermData.value = RetrofitClient.krogerAPIService
                .searchProductByTerm("Bearer ${_accessToken.value!!}",
                    term, LOCATION_ID_ARL_HEIGHTS, "50")
        }


        Log.d("Whereisdatainfun", term)

    }

    fun setTerm(string: String)
    {
        _productTerm.value = string
        viewModelScope.launch { searchProductByTerm(_productTerm.value!!) }
        Log.d("Whereisdata", _productTerm.value!!)

    }

}