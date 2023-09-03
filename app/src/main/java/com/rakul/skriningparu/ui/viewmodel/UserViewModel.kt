package com.rakul.skriningparu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rakul.skriningparu.data.model.request.IdentityRequest

class UserViewModel : ViewModel() {
    private val _userData = MutableLiveData<IdentityRequest>()
    val userData: LiveData<IdentityRequest> = _userData

    var userUid = ""
    val listAnswers = mutableListOf<String>()
    val listFirstAnswers = mutableListOf<String>() // Use in first screening page
    var subTotalBobot = 0.0

    fun addUserUID(data: String) {
        userUid = data
    }

    fun addUserData(data: IdentityRequest) {
        _userData.postValue(data)
    }

    fun addUserAnswer(data: String) {
        listAnswers.add(data)
    }

    fun clearListAnswers() {
        listAnswers.clear()
    }

    fun addSubTotalBobot(subTotal: Double) {
        subTotalBobot = subTotal
    }
}