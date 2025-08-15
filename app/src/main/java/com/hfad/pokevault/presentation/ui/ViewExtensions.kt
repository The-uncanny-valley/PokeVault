package com.hfad.pokevault.presentation.ui

import android.os.Build
import android.view.View
import androidx.core.graphics.toColorInt

fun View.applyColoredShadow() {
    if (Build.VERSION.SDK_INT >= 28) {
        val c = "#363A81".toColorInt()
        outlineSpotShadowColor = c
        outlineAmbientShadowColor = c
        elevation = 12f  // blur size
        translationZ = 4f // vertical offset
    }
}