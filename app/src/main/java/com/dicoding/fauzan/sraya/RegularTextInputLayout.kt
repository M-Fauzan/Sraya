package com.dicoding.fauzan.sraya

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout

class RegularTextInputLayout : TextInputLayout {
    constructor(context: Context) : super(context) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }
    private fun init(context: Context) {
        typeface = Typeface.createFromAsset(context.assets, "OpenSans-Regular.ttf")
    }
}