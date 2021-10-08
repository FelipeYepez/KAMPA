package com.example.kampa

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.kampa.R
import com.google.android.material.textfield.TextInputEditText
import com.parse.ParseUser
import com.parse.SignUpCallback
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private var back: ImageView? = null
    private var signup: Button? = null
    private var username: TextInputEditText? = null
    private var password: TextInputEditText? = null
    private var passwordagain: TextInputEditText? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        progressDialog = ProgressDialog(this)

        signup = findViewById(R.id.signup)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        passwordagain = findViewById(R.id.passwordagain)

        btnLogRegister.setOnClickListener {
            onBackPressed()
        }

        signup?.setOnClickListener {
            if (password?.text.toString() == passwordagain?.text.toString() && !TextUtils.isEmpty(username?.text.toString()))
                signup(username?.text.toString(), password?.text.toString());
            else
                Toast.makeText(
                    this,
                    "Make sure that the values you entered are correct.",
                    Toast.LENGTH_SHORT
                ).show();
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
    }

    fun signup(username: String, password: String) {
        progressDialog?.show()

        val user = ParseUser()
        user.setUsername(username)
        user.setPassword(password)
        user.signUpInBackground(SignUpCallback() {
            progressDialog?.dismiss()
            if (it == null) {
                goToMainActivity()
            } else {
                ParseUser.logOut();
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show();
            }
        });
    }
    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}
