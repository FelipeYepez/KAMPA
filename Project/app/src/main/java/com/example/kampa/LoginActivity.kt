package com.example.kampa

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.kampa.models.Rol
import com.google.android.material.textfield.TextInputEditText
import com.parse.*

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private var username: TextInputEditText? = null
    private var password: TextInputEditText? = null
    private var login: Button? = null
    private var btnRegLogin: Button? = null
    private var progressDialog: ProgressDialog? = null
    private var intentos:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        progressDialog = ProgressDialog(this@LoginActivity)
        username = findViewById(R.id.user)
        password = findViewById(R.id.password)
        login = findViewById(R.id.btnLog)
        btnRegLogin = findViewById(R.id.btnRegLogin)
        btnRegLogin?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
        }
        login?.setOnClickListener(View.OnClickListener {
            login(
                username?.text.toString(),
                password?.text.toString()
            )
        })
    }

    var startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if (result.resultCode == Activity.RESULT_OK) {

            var intent = result.data

            if (intent?.getBooleanExtra("valido", false) == true) {
                intentos = 0
            }

        }
    }

    fun login(username: String, password: String) {
        progressDialog?.show()
        if(intentos >= 2){
            startForResult.launch(Intent(this, reCaptcha::class.java))
            progressDialog?.dismiss()
        }
        if(intentos < 2){
            val isInputComplete = LoginUtils.validateInputs(username, password)
            if(isInputComplete == getString(R.string.valores_completos)) {
                ParseUser.logInInBackground(
                    username,
                    password
                ) { parseUser: ParseUser?, parseException: ParseException? ->
                    if (parseException == null) {
                        if (parseUser != null) {
                            goToMainActivity()
                        } else {
                            ParseUser.logOut()
                        }
                    } else {
                        if (parseException.code == 100) {
                            Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_SHORT)
                                .show()
                            progressDialog?.dismiss()
                        }
                        if (parseException.code == 101) {
                            Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT)
                                .show()
                            intentos += 1
                            progressDialog?.dismiss()
                        }
                    }
                }
            } else {
                Toast.makeText(this, isInputComplete, Toast.LENGTH_SHORT).show()
                progressDialog?.dismiss()
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

object LoginUtils {

    /**
     * Valida que los campos obligatorios para ingresar al sistema hayan sido llenados.
     * @param username nombre de usuario
     * @param password contraseña del usuario
     */
    fun validateInputs(username:String, password:String):String {
        if(username.isEmpty()){
            return "Escriba su nombre de usuario"
        } else if(password.isEmpty()){
            return "Escriba su contraseña"
        }

        return "Valores Completos"
    }
}
