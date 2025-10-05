package com.practicum.playlistmaker.playlist.player.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.practicum.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null
    private var currentBitmap: Bitmap? = null

    private var playIconRes: Int = R.drawable.play_button_light
    private var pauseIconRes: Int = R.drawable.pause_light

    private var onClickListener: (() -> Unit)? = null

    private val dstRect = RectF()

    var isPlaying: Boolean = false
        set(value) {
            field = value
            updateBitmap()
            invalidate()
        }

    init {
        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.PlaybackButtonView)
        try {
            playIconRes = ta.getResourceId(
                R.styleable.PlaybackButtonView_playIcon,
                R.drawable.play_button_light
            )
            pauseIconRes = ta.getResourceId(
                R.styleable.PlaybackButtonView_pauseIcon,
                R.drawable.pause_light
            )
        } finally {
            ta.recycle()
        }
    }

    fun setIcons(playRes: Int, pauseRes: Int) {
        playIconRes = playRes
        pauseIconRes = pauseRes
        if (width > 0 && height > 0) {
            createBitmaps(width, height)
            invalidate()
        } else {
            requestLayout()
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            createBitmaps(w, h)
        }
    }

    private fun createBitmaps(w: Int, h: Int) {
        playBitmap?.recycle()
        pauseBitmap?.recycle()
        playBitmap = null
        pauseBitmap = null
        currentBitmap = null

        playBitmap = drawableToBitmap(playIconRes, w, h)
        pauseBitmap = drawableToBitmap(pauseIconRes, w, h)

        updateBitmap()
    }

    private fun drawableToBitmap(resId: Int, w: Int, h: Int): Bitmap? {
        val drawable = AppCompatResources.getDrawable(context, resId) ?: return null
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bmp
    }

    private fun updateBitmap() {
        currentBitmap = if (isPlaying) pauseBitmap else playBitmap
        currentBitmap?.let { bmp ->
            val left = (width - bmp.width) / 2f
            val top = (height - bmp.height) / 2f
            dstRect.set(left, top, left + bmp.width, top + bmp.height)
        } ?: run {
            dstRect.setEmpty()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bmp = currentBitmap
        if (bmp != null && !bmp.isRecycled && !dstRect.isEmpty) {
            canvas.drawBitmap(bmp, null, dstRect, null)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minSize = resources.getDimensionPixelSize(R.dimen.playback_button_size)
        val width = resolveSizeAndState(minSize, widthMeasureSpec, 0)
        val height = resolveSizeAndState(minSize, heightMeasureSpec, 0)
        setMeasuredDimension(width, height)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                performClick()
                onClickListener?.invoke()
                toggleStateLocalPreview()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun toggleStateLocalPreview() {
        isPlaying = !isPlaying
    }

    fun setOnPlaybackClickListener(listener: () -> Unit) {
        onClickListener = listener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        playBitmap?.recycle()
        pauseBitmap?.recycle()
        playBitmap = null
        pauseBitmap = null
        currentBitmap = null
    }
}
