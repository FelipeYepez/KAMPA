package com.example.kampa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService

/**
 * @author RECON
 * Actividad para capturar una imagen con la cámara del usuario
 * @return savedUri imagen capturada en formato URI
 */
class TakePictureActivity : AppCompatActivity() {

    var imageCapture: ImageCapture? = null
    var viewFinder: PreviewView? = null
    var cameraCaptureButton: Button? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    /**
     * Pide el permiso para utilizar la cámara y coloca un clickListener para tomar la imagen
     * @param savedInstanceState para que la actividad pueda restaurarse a un estado anterior
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)
        viewFinder = findViewById(R.id.viewFinder)
        cameraCaptureButton = findViewById(R.id.camera_capture_button)

        // Permiso para usar la cámara
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Listener para capturar la foto
        cameraCaptureButton?.setOnClickListener { takePhoto() }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    /**
     * Captura la imagen y la regresa en un intent a la actividad anterior
     */
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Time-stamped output para la imagen
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")

        // Crear objeto de opciones de salida
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Listener que se ejecuta después de tomar una imagen
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"

                    val intent = Intent()
                    intent.data = savedUri
                    setResult(RESULT_OK, intent)
                    finish()

                }
            })
    }

    /**
     * Función para mostrar cámara
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Para hacer bind entre el ciclo de vida de las cámaras y el recycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Vista previa
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder?.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Para tomar la foto con la cámara trasera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Bind use cases
                cameraProvider.unbindAll()

                // Bind use cases a camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * Revisar que se tengan todos los permisos necesarios
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Obtener directorio en el que se guardará la imagen
     * @return directorio
     */
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    /**
     * Apaga cámara cuando se termina la actividad
     */
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    /**
     * Constantes
     */
    companion object {
        val RESULT_OK: Int = -1
        private const val TAG = "TakePictureActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    /**
     * Comienza a ejecutar la cámara si todos los permisos son aceptados
     * @param requestCode código de solicitud
     * @param permissions arreglo con los permisos
     * @param grantResults resultados de los permisos
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Los permisos fueron denegados",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}