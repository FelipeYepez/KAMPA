package com.example.kampa

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.parse.ParseUser
import com.parse.SignUpCallback

class RegisterActivity : AppCompatActivity() {

    private var back: ImageView? = null
    private var signup: Button? = null
    private var username: TextInputEditText? = null
    private var password: TextInputEditText? = null
    private var passwordagain: TextInputEditText? = null
    private var progressDialog: ProgressDialog? = null
    private var btnLogRegister: Button? = null
    private var terminos: CheckBox? = null
    private var terminosText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        progressDialog = ProgressDialog(this)

        signup = findViewById(R.id.signup)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        passwordagain = findViewById(R.id.passwordagain)
        btnLogRegister = findViewById(R.id.btnLogRegister)
        terminos = findViewById(R.id.terminos)

        terminos?.movementMethod = LinkMovementMethod.getInstance()

        btnLogRegister?.setOnClickListener {
            onBackPressed()
        }

        signup?.setOnClickListener {
            if(terminos!!.isChecked == false){
                Toast.makeText(
                    this,
                    "Acepta los términos y condiciones para continuar.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if(password?.text.toString() == passwordagain?.text.toString() && !TextUtils.isEmpty(username?.text.toString()))
                signup(username?.text.toString(), password?.text.toString())
            else
                Toast.makeText(
                    this,
                    "Asegura que los valores introducidos son correctos.",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
    }

    fun signup(username: String, password: String) {
        progressDialog?.show()

        val addecuatePassword = Regex("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,}\$")

        if(addecuatePassword.containsMatchIn(password)){
        val user = ParseUser()
        user.setUsername(username)
        user.setPassword(password)
        user.signUpInBackground(SignUpCallback() {
            progressDialog?.dismiss()
            if (it == null) {
                goToMainActivity()
            } else {
                ParseUser.logOut()
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
        })
        }
        else{
            ParseUser.logOut()
            Toast.makeText(this, "Tu contraseña debe de contener almenos 8 caracteres 1 digito 1 mayuscula 1 minuscula y 1 caracter especial", Toast.LENGTH_LONG).show()
            progressDialog?.hide()
        }
    }
    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}

object RegisterUtils{
    /**
     * Valida que los campos para registrar un nuevo usuario sean validos
     *
     *
     */
    fun validateInputs(usuario: String, password: String, passwordAgain: String):String{
        val addecuatePassword = Regex("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,}\$")
        if(usuario.length == 0){
            return "Escribe el nombre de usuario"
        }
        else if(password.length == 0){
            return "Escribe la contrasena"
        }
        else if(password.length < 8){
            return "Contra demasiado corta"
        }
        else if(passwordAgain.length == 0){
            return "Valida la contrasena"
        }
        else if(!addecuatePassword.containsMatchIn(password)){
            return "Contrasena invalida"
        }
        else if(password != passwordAgain){
            return "Validacion nula"
        }
        return "todo correcto"
    }
}
