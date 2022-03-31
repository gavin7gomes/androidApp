package com.gavin.easypharm.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class EditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs){
    init {
        applyFont()
    }
    private fun applyFont(){
        //This gets the file from asset folder and sets it to the title edit view.
        val typeface: Typeface = Typeface.createFromAsset(context.assets, "Montserrat-Regular.ttf")
        setTypeface(typeface)
    }
}