package com.example.surface_texture

import android.graphics.SurfaceTexture
import android.view.Surface
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

class SurfaceTexturePlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var channel: MethodChannel
    private lateinit var mFlutterPluginBinding: FlutterPlugin.FlutterPluginBinding
    private lateinit var mSurfaceTexture: SurfaceTexture
    private lateinit var mSurface: Surface

    override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        mFlutterPluginBinding = binding
        channel = MethodChannel(mFlutterPluginBinding.binaryMessenger, "surface_texture")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        when (call.method) {
            "generateSurfaceTexture" -> {
                val surfaceHeight = call.argument<Int>("height")
                val textureId = generateSurfaceTexture(surfaceHeight ?: 0)
                result.success(textureId)
            }
            "setSurfaceBufferSize" -> {
                val surfaceHeight = call.argument<Int>("height");
                setSurfaceBufferSize(surfaceHeight ?: 0)
                result.success(null)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun generateSurfaceTexture(surfaceHeight: Int): Long {
        val textureRegistry = mFlutterPluginBinding.textureRegistry
        val surfaceTextureEntry = textureRegistry.createSurfaceTexture()
        mSurfaceTexture = surfaceTextureEntry.surfaceTexture()
        mSurfaceTexture.setDefaultBufferSize(1080, surfaceHeight)
        mSurface = Surface(mSurfaceTexture)

        val canvas = mSurface.lockHardwareCanvas()
        canvas.drawRGB(255, 230, 15)
        mSurface.unlockCanvasAndPost(canvas)

        return surfaceTextureEntry.id()
    }

    private fun setSurfaceBufferSize(surfaceHeight: Int) {
        mSurfaceTexture.setDefaultBufferSize(1080,surfaceHeight)
        val canvas = mSurface.lockCanvas(null)
        canvas.drawRGB(255, 230, 15)
        mSurface.unlockCanvasAndPost(canvas)
    }
}
