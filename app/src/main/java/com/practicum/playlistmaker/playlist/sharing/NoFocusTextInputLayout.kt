package com.practicum.playlistmaker

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout

class NoFocusTextInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextInputLayout(context, attrs, defStyleAttr) {

    private val blueColor = ContextCompat.getColor(context, R.color.yp_blue)
    private val grayColor = ContextCompat.getColor(context, R.color.yp_gray)   // для светлой темы / hint
    private val blackTextColor = ContextCompat.getColor(context, R.color.yp_black)
    private val whiteColor = ContextCompat.getColor(context, R.color.yp_white) // для тёмной темы

    // Толщина рамки в px (1 dp)
    private val strokeWidthPx: Int by lazy {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            1f,
            resources.displayMetrics
        ).toInt()
    }

    init {
        boxBackgroundMode = BOX_BACKGROUND_OUTLINE
        setFixedStrokeWidth(strokeWidthPx)
        setDefaultColors()
    }

    private fun isDarkTheme(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    private fun inactiveStrokeColor(): Int {
        // В светлой теме — серый, в тёмной — белый
        return if (isDarkTheme()) whiteColor else grayColor
    }

    private fun setDefaultColors() {
        // Начальные значения в зависимости от темы
        boxStrokeColor = inactiveStrokeColor()
        val hintDefault = if (isDarkTheme()) whiteColor else grayColor
        defaultHintTextColor = ColorStateList.valueOf(hintDefault)
        editText?.let {
            val textDefault = if (isDarkTheme()) whiteColor else blackTextColor
            it.setTextColor(textDefault)
            it.setHintTextColor(ColorStateList.valueOf(hintDefault))
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupListeners()
        updateStrokeAndHintBasedOnState()
        // на всякий случай зафиксируем толщину снова
        setFixedStrokeWidth(strokeWidthPx)
    }

    private fun setupListeners() {
        val et = editText ?: return

        et.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateStrokeAndHintBasedOnState()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        et.setOnFocusChangeListener { _, _ ->
            updateStrokeAndHintBasedOnState()
        }
    }

    fun updateStrokeAndHintBasedOnState() {
        val et = editText ?: return
        val hasText = et.text?.isNotEmpty() == true

        // Логика:
        // - В тёмной теме: всё всегда белое
        // - В светлой: если есть текст -> синий, иначе -> серый
        val strokeTarget = if (isDarkTheme()) {
            whiteColor
        } else {
            if (hasText) blueColor else grayColor
        }

        val hintTarget = if (isDarkTheme()) {
            whiteColor
        } else {
            if (hasText) blueColor else grayColor
        }

        val actualTextColor = if (isDarkTheme()) whiteColor else blackTextColor

        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_focused),
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf()
        )

        // ColorStateList для рамки (одинаковый цвет для всех состояний)
        val strokeColors = intArrayOf(strokeTarget, strokeTarget, strokeTarget)
        val strokeCsl = ColorStateList(states, strokeColors)

        // Применяем state-list для рамки (публичный метод если есть, иначе fallback)
        try {
            val method = TextInputLayout::class.java.getMethod("setBoxStrokeColorStateList", ColorStateList::class.java)
            method.invoke(this, strokeCsl)
        } catch (e1: Exception) {
            try {
                val method2 = TextInputLayout::class.java.getMethod("setBoxStrokeColor", Int::class.javaPrimitiveType)
                method2.invoke(this, strokeTarget)
            } catch (e2: Exception) {
                boxStrokeColor = strokeTarget
            }
        }

        // ColorStateList для hint
        val hintColors = intArrayOf(hintTarget, hintTarget, hintTarget)
        val hintCsl = ColorStateList(states, hintColors)

        // Применяем hint и к TextInputLayout, и к EditText
        defaultHintTextColor = hintCsl
        editText?.setHintTextColor(hintCsl)

        // Гарантируем фиксированную толщину рамки
        setFixedStrokeWidth(strokeWidthPx)

        // Текстовое содержимое
        editText?.setTextColor(actualTextColor)
    }

    private fun setFixedStrokeWidth(px: Int) {
        try {
            val setUnfocused = TextInputLayout::class.java.getMethod("setBoxStrokeWidth", Int::class.javaPrimitiveType)
            val setFocused = TextInputLayout::class.java.getMethod("setBoxStrokeWidthFocused", Int::class.javaPrimitiveType)
            setUnfocused.invoke(this, px)
            setFocused.invoke(this, px)
        } catch (e: Exception) {
            try {
                this.boxStrokeWidth = px
                this.boxStrokeWidthFocused = px
            } catch (e2: Exception) {
                // если ни один путь не доступен — пропускаем
            }
        }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        post { updateStrokeAndHintBasedOnState() }
    }

    override fun dispatchWindowFocusChanged(hasFocus: Boolean) {
        super.dispatchWindowFocusChanged(hasFocus)
        post { updateStrokeAndHintBasedOnState() }
    }
}
