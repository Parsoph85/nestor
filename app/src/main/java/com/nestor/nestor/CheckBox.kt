package com.nestor.nestor

/*
class CheckBoxSpan(private val checkBox: CheckBox) : ReplacementSpan() {
    var isChecked: Boolean = false
        set(value) {
            field = value
            checkBox.isChecked = value
        }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        checkBox.layout(0, top, checkBox.measuredWidth, bottom)
        checkBox.draw(canvas)
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return checkBox.measuredWidth // Ширина чекбокса
    }
}*/