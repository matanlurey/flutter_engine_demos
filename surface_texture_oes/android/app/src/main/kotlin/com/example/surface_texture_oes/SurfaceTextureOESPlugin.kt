package com.example.surface_texture_oes

import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.GLES20
import android.view.Surface
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import java.lang.RuntimeException
import javax.microedition.khronos.egl.EGL10




class SurfaceTextureOESPlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var binding: FlutterPlugin.FlutterPluginBinding
    private lateinit var channel: MethodChannel
    private lateinit var surface: Surface

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        this.binding = binding
        channel = MethodChannel(binding.binaryMessenger, "surface_texture_oes")
        channel.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        surface.release()
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "generateSurfaceTexture" -> {
                val textureId = eglGenerateSurfaceTexture()
                result.success(textureId)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    private fun eglGenerateSurfaceTexture() : Long {
        val entry = binding.textureRegistry.createSurfaceTexture()
        val texture = entry.surfaceTexture()
        texture.setDefaultBufferSize(500, 500)

        // Create an Android Surface.
        surface = Surface(texture)

        // Initialize EGL.
        val attribList = intArrayOf(
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,  // Request OpenGL ES 2.0
            EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,         // Request window surface
            EGL14.EGL_RED_SIZE, 8,                                // 8 bits for red color
            EGL14.EGL_GREEN_SIZE, 8,                              // 8 bits for green
            EGL14.EGL_BLUE_SIZE, 8,                               // 8 bits for blue
            EGL14.EGL_ALPHA_SIZE, 8,                              // 8 bits for alpha (if you need transparency)
            EGL14.EGL_NONE                                        // Terminate the list
        )

        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs: IntArray = intArrayOf(1)

        val eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        if (!EGL14.eglChooseConfig(eglDisplay, attribList, 0, configs, 0, 1, numConfigs, 0)) {
            throw IllegalStateException("Could not choose config")
        }
        val eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, configs[0], surface, attribList, 0)
        val eglContext = EGL14.eglCreateContext(eglDisplay, configs[0], EGL14.EGL_NO_CONTEXT, attribList, 0)

        // Draw
        EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        EGL14.eglSwapBuffers(eglDisplay, eglSurface)

        return entry.id()
    }
}
