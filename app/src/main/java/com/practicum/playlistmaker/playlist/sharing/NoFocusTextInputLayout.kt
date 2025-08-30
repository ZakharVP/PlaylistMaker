package com.practicum.playlistmaker

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout

class NoFocusTextInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextInputLayout(context, attrs, defStyleAttr) {

    private var hasText = false

    fun setHasText(hasText: Boolean) {
        this.hasText = hasText
        updateColors()
    }

    private fun updateColors() {
        val colorRes = if (hasText) R.color.yp_blue else R.color.yp_gray
        val color = context.getColor(colorRes)
        val colorStateList = android.content.res.ColorStateList.valueOf(color)

        // Устанавливаем цвета
        boxStrokeColor = color
        hintTextColor = colorStateList
        defaultHintTextColor = colorStateList
    }

    // Переопределяем чтобы отключить автоматическое изменение цвета при фокусе
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Отключаем автоматическое управление цветом
        boxBackgroundMode = BOX_BACKGROUND_OUTLINE
        updateColors()
    }
}