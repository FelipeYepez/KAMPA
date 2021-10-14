package com.example.kampa

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.kampa.models.Rol
import com.google.android.material.textfield.TextInputEditText
import com.parse.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private var username: TextInputEditText? = null
    private var password: TextInputEditText? = null
    private var login: Button? = null
    private var btnRegLogin: Button? = null
    private var progressDialog: ProgressDialog? = null
    private var roleObject: Rol? = null
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
        if(intentos >= 2){
            startForResult.launch(Intent(this, reCaptcha::class.java))
        }
        if(intentos < 2){
            progressDialog?.show()
            ParseUser.logInInBackground(username,password) { parseUser: ParseUser?, parseException: ParseException? ->
                progressDialog?.dismiss()
                if (parseException == null){
                    if (parseUser != null) {
                        var currentRole: Rol = ParseUser.getCurrentUser().get("idRol") as Rol
                        roleQuery(currentRole.objectId.toString())

                        if(roleObject?.descripcion == "administrador"){
                            goToAdminActivity()
                        }
                        else if (roleObject?.descripcion == "usuario") {
                            goToMainActivity()
                        }
                    }
                    else {
                        ParseUser.logOut()
                    }
                }
                else{
                    if(parseException.code == 100){
                        Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_SHORT).show()
                    }
                    if(parseException.code == 101){
                        Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                        intentos += 1
                    }
                }
            }
        }
    }
    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToAdminActivity() {
        val intent = Intent(this, AdminMenu::class.java)
        startActivity(intent)
    }

    private fun roleQuery(id:String){
        val query = ParseQuery<Rol>("Rol")
        try {
            roleObject = query[id]
        } catch (e: ParseException) {
            Log.d("role", e.toString())
        }
    }
}
