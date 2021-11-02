package com.descubre.kampa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.descubre.kampa.BuildConfig.SECRET_KEY
import com.descubre.kampa.BuildConfig.SITE_KEY
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import org.json.JSONObject
import java.util.*

/**
 * @author RECON
 * Actividad para validar que la persona intentando iniciar sesión no es un robot
 * @return Boolean indicando si la verificación ha sido exitosa o no
 */
class reCaptcha : AppCompatActivity(), View.OnClickListener {
    val RESULT_OK:Int = -1
    var TAG = "reCaptcha"
    var btnverifyCaptcha: Button? = null
    var queue: RequestQueue? = null

    /**
     * Inicializa la actividad
     * Coloca un clickListener en el captcha
     * @param savedInstanceState para que la actividad pueda restaurarse a un estado anterior
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_re_captcha)
        btnverifyCaptcha = findViewById(R.id.button)
        btnverifyCaptcha?.setOnClickListener(this)
        queue = Volley.newRequestQueue(applicationContext)
    }

    /**
     * ClickListener para el captcha
     * Se utiliza la API SafetyNet para ejecutar el captcha
     * @param view vista en la que se coloca el captcha
     */
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

    /**
     * Manejo del resultado del captcha
     * Se termina la actividad y se regresa un extra con valor true si el captcha fue exitoso
     * @param responseToken respuesta del captcha
     */
    protected fun handleSiteVerify(responseToken: String) {
        //Sitio de google para verificar recaptcha
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