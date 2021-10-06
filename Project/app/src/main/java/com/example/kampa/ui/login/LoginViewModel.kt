package com.example.kampa.ui.login

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import android.widget.Toast
import com.example.kampa.MainActivity
import com.example.kampa.data.LoginRepository
import com.example.kampa.data.Result

import com.example.kampa.R
import com.parse.ParseUser
import com.parse.LogInCallback;
import com.parse.ParseException;

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult


    fun login(username: String, password: String) {
        Log.d("LoginViewModel", "here")

        ParseUser.logInInBackground(username,password) { user: ParseUser?, e: ParseException? ->
            if (e == null){

            if (user != null) {
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = username))
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
            }
            else{
                e.printStackTrace()
            }
        }

    }




    fun loginDataChanged(password: String) {
        if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}