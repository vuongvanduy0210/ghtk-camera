package com.duyvv.camera.ui.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.duyvv.camera.R

class ScanQRView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.STROKE
        strokeWidth = 7f
        isAntiAlias = true
    }

    private val cornerRadius = 70f
    private val lineLength = 60f

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val saveCount = canvas.saveLayer(
            0f, 0f,
            width.toFloat(), height.toFloat(), null
        )

        // Vẽ lớp mờ lên toàn bộ màn hình
        canvas.drawRect(0f, 0f,
            width.toFloat(), height.toFloat(),
            Paint().apply {
                color = Color.BLACK
                alpha = 180
            }
        )

        // Xác định vị trí và kích thước của hình vuông bo tròn
        val centerX = width / 2f
        val centerY = height / 2.5f
        val rectSize = 700f // Kích thước hình vuông
        val left = centerX - rectSize / 2
        val top = centerY - rectSize / 2
        val right = centerX + rectSize / 2
        val bottom = centerY + rectSize / 2

        val padding = 15f
        // Tạo hình chữ nhật bo tròn
        drawRectangleWithRoundedCorners(canvas, left, top, right, bottom, padding)

        // Khôi phục canvas
        canvas.restoreToCount(saveCount)

        // draw border
        drawBorder(canvas, left, top, right, bottom)

        // draw text
        canvas.drawText(
            "Scan QR code",
            width / 2f,
            centerY + rectSize / 2 + 100,
            Paint().apply {
                color = Color.WHITE
                textSize = 60f
                textAlign = Paint.Align.CENTER
            }
        )
    }

    private fun drawBorder(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        // Vẽ góc trên trái
        // vẽ cung tròn
        val topLeftRect = RectF(
            left, top,
            left + 2 * cornerRadius, top + 2 * cornerRadius
        )
        canvas.drawArc(topLeftRect, 180f, 90f, false, paint)
        // vẽ đường dọc
        canvas.drawLine(
            left, top + cornerRadius,
            left, top + cornerRadius + lineLength, paint
        )
        // vẽ đường ngang
        canvas.drawLine(
            left + cornerRadius, top,
            left + cornerRadius + lineLength, top, paint
        )

        // vẽ góc trên bên phải
        val topRightRect = RectF(
            right - 2 * cornerRadius, top,
            right, top + 2 * cornerRadius
        )
        canvas.drawArc(
            topRightRect, 270f,
            90f, false, paint
        )
        canvas.drawLine(
            right, top + cornerRadius,
            right, top + cornerRadius + lineLength, paint
        )
        canvas.drawLine(
            right - cornerRadius,
            top,
            right - cornerRadius - lineLength,
            top,
            paint
        )

        // vẽ góc dưới bên trái
        val bottomLeftRect = RectF(
            left, bottom - 2 * cornerRadius,
            left + 2 * cornerRadius, bottom
        )
        canvas.drawArc(
            bottomLeftRect, 90f,
            90f, false, paint
        )
        canvas.drawLine(
            left, bottom - cornerRadius,
            left, bottom - cornerRadius - lineLength, paint
        )
        canvas.drawLine(
            left + cornerRadius,
            bottom,
            left + cornerRadius + lineLength,
            bottom,
            paint
        )

        // vẽ góc dưới bên phải
        val bottomRightRect = RectF(
            right - 2 * cornerRadius,
            bottom - 2 * cornerRadius, right, bottom
        )
        canvas.drawArc(bottomRightRect, 0f, 90f, false, paint)
        canvas.drawLine(
            right,
            bottom - cornerRadius,
            right,
            bottom - cornerRadius - lineLength,
            paint
        )
        canvas.drawLine(
            right - cornerRadius,
            bottom,
            right - cornerRadius - lineLength,
            bottom,
            paint
        )
    }

    private fun drawRectangleWithRoundedCorners(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        padding: Float
    ) {
        val roundRect = RectF(
            left + padding, top + padding,
            right - padding, bottom - padding
        )
        canvas.drawRoundRect(roundRect, cornerRadius, cornerRadius, Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        })
    }
}