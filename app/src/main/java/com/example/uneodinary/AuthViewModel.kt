package com.example.uneodinary

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<List<Report>>>()
    val loginResult: LiveData<Result<List<Report>>> = _loginResult

    fun loadReportsByTag(reportId: Int, tagName: String) {
        viewModelScope.launch {
            val request = LoginRequest(reportId, tagName, "", "", "")
            val result = repository.Login(request)
            _loginResult.postValue(result)
        }
    }
}