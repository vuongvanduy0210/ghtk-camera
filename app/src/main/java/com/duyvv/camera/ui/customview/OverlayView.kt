package com.duyvv.camera.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var scanQRViewRect: RectF? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        alpha = 128 // Đặt độ mờ (0 - 255)
    }
    private val clearPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    fun setScanQRViewRect(rect: RectF) {
        scanQRViewRect = rect
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Vẽ lớp mờ lên toàn bộ màn hình
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // Loại bỏ vùng ScanQRView khỏi lớp mờ
        scanQRViewRect?.let {
            canvas.drawRect(it, clearPaint)
        }
    }
}
