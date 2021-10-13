package com.example.kampa

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.kampa.models.Rol
import com.google.android.material.textfield.TextInputEditText
import com.parse.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var username: TextInputEditText? = null
    private var password: TextInputEditText? = null
    private var login: Button? = null
    private var btnRegLogin: Button? = null
    private var progressDialog: ProgressDialog? = null
    private var roleObject: Rol? = null

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
    fun login(username: String, password: String) {
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
