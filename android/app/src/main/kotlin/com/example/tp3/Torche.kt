package com.example.tp3


import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class Torche(context: Context) : MethodChannel.MethodCallHandler {

    private val cameraManager = context.getSystemService(CameraManager::class.java)

    // Cherche la première caméra qui possède un flash
    private fun idCameraAvecFlash(): String? =
        cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "allumer" -> {
                val etat = call.argument<Boolean>("etat")
                if (etat == null) {
                    result.error("ARGUMENT_MANQUANT", "Le paramètre « etat » est obligatoire", null)
                    return
                }
                try {
                    val id = idCameraAvecFlash()
                    if (id == null) {
                        result.error("PAS_DE_FLASH", "Cet appareil n'a pas de flash", null)
                        return
                    }
                    cameraManager.setTorchMode(id, etat)
                    result.success(null)
                } catch (e: CameraAccessException) {
                    result.error("ERREUR_CAMERA", e.message, null)
                }
            }
            else -> result.notImplemented()
        }
    }
}