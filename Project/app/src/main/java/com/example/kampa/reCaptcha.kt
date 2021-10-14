package com.example.kampa

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.kampa.BuildConfig.SECRET_KEY
import com.example.kampa.BuildConfig.SITE_KEY
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import org.json.JSONObject
import java.lang.Exception
import java.util.HashMap


class reCaptcha : AppCompatActivity(), View.OnClickListener {
    val RESULT_OK:Int = -1
    var TAG = "reCaptcha"
    var btnverifyCaptcha: Button? = null
    var queue: RequestQueue? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_re_captcha)
        btnverifyCaptcha = findViewById(R.id.button)
        btnverifyCaptcha?.setOnClickListener(this)
        queue = Volley.newRequestQueue(applicationContext)
    }

    override fun onClick(view: View) {
        SafetyNet.getClient(this).verifyWithRecaptcha(SITE_KEY)
            .addOnSuccessListener(
                this
            ) { response ->
                if (!response.tokenResult.isEmpty()) {
                    handleSiteVerify(response.tokenResult)
                }
            }
            .addOnFailureListener(this) { e ->
                if (e is ApiException) {
                    Log.d(
                        TAG, "Error message: " +
                                CommonStatusCodes.getStatusCodeString(e.statusCode)
                    )
                } else {
                    Log.d(TAG, "Unknown type of error: " + e.message)
                }
            }
    }

    protected fun handleSiteVerify(responseToken: String) {
        //it is google recaptcha siteverify server
        //you can place your server url
        val url = "https://www.google.com/recaptcha/api/siteverify"
        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean("success")) {
                        Toast.makeText(this, "Verificación exitosa",Toast.LENGTH_LONG).show()
                        val intent = Intent()
                        intent.putExtra("valido", true)
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Intenta nuevamente",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d(TAG, jsonObject.getString("error-codes").toString())
                    }
                } catch (ex: Exception) {
                    Log.d(TAG, "JSON exception: " + ex.message)
                }
            },
            Response.ErrorListener { error -> Log.d(TAG, "Error message: " + error.message) }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["secret"] = SECRET_KEY
                params["response"] = responseToken
                return params
            }
        }
        request.retryPolicy = DefaultRetryPolicy(
            50000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        queue!!.add(request)
    }
}