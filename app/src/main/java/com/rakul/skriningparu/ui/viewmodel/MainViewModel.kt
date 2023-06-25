package com.rakul.skriningparu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rakul.skriningparu.data.model.response.ConsentResponse
import com.rakul.skriningparu.data.model.response.ScreeningResponse

class MainViewModel : ViewModel() {
    private val _consentData = MutableLiveData<ConsentResponse>()
    val consentData: LiveData<ConsentResponse> = _consentData

    private val _firstScreeningData = MutableLiveData<List<ScreeningResponse>>()
    val firstScreeningData: LiveData<List<ScreeningResponse>> = _firstScreeningData

    private val _secondScreeningData = MutableLiveData<List<ScreeningResponse>>()
    val secondScreeningData: LiveData<List<ScreeningResponse>> = _secondScreeningData

    var screenName = ""
    var isScreenBack = false

    fun addConsentData(data: ConsentResponse) {
        _consentData.postValue(data)
    }

    fun addFirstScreeningData(data: List<ScreeningResponse>) {
        _firstScreeningData.postValue(data)
    }

    fun addSecondScreeningData(data: List<ScreeningResponse>) {
        _secondScreeningData.postValue(data)
    }
}