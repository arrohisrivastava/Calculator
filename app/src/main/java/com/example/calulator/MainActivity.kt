package com.example.calulator

import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Property
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import com.example.calulator.databinding.ActivityMainBinding
import org.mariuszgromada.math.mxparser.Expression

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var resultTextView: TextView
    private lateinit var finResultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultTextView = findViewById(R.id.textView)
        finResultTextView = findViewById(R.id.textView22)
    }

    fun clearExpression() {
        finResultTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        resultTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38f)

        resultTextView.text = ""
        finResultTextView.text = ""
    }

    fun appendToExpression(txt: String) {
        resultTextView.append(txt)
    }

    fun deleteLastCharacter() {
        val currentText = resultTextView.text.toString()

        if (currentText.isNotEmpty()) {
            val newText = currentText.substring(0, currentText.length - 1)
            resultTextView.text = newText
            if(newText.isEmpty())
                clearExpression()
        }
    }

    fun mekRed() {
        resultTextView.setTextColor(Color.RED)
        finResultTextView.setTextColor(Color.RED)
    }

    fun mekDefault() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            resultTextView.setTextColor(Color.BLACK)
            finResultTextView.setTextColor(Color.BLACK)
        } else {
            resultTextView.setTextColor(Color.WHITE)
            finResultTextView.setTextColor(Color.WHITE)
        }
    }

    fun operason() {
        val expression1 = resultTextView.text.toString()
        val expres = modifyMathExpression(expression1)
        val result = eval(expres)
        if (result.isValidExpression) {
            finResultTextView.append(result.value.toString())
            val smol = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                24f, resources.displayMetrics)
            val bigg = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                38f, resources.displayMetrics)
            finResultTextView.animateTextSize(smol, bigg, 300)
            resultTextView.animateTextSize(bigg, smol, 300)
        } else {
            mekRed()
            finResultTextView.text = "Invalid Expression"
        }
    }
    private fun modifyMathExpression(expression: String): String {
        var modifiedExpression = expression
        modifiedExpression=expression.replace('×', '*').replace('÷', '/').replace('−', '-')

        // Add 0 after decimal point if no number follows it
        modifiedExpression = modifiedExpression.replace(Regex("(?<=\\d)\\.(?=\\D|$)"), ".0")

        //Add * after % if it is followed by a number and not an operator
        modifiedExpression = modifiedExpression.replace(Regex("(?<![*/+-])%(?=\\d)(?![*/+-])"), "%*")

        // Replace % with /100 if it is after a number
        modifiedExpression = modifiedExpression.replace(Regex("(?<=\\d)%"), "/100")

        return modifiedExpression
    }

    private fun eval(expression: String): CalculationResult {
        val value = convertToNumber(Expression(expression).calculate().toString())
        return CalculationResult(value, isValidExpression(expression))
    }

    private fun isValidExpression(expression: String): Boolean {
        val regexPattern = "^([-+.]?\\d+\\.?\\d*[-+*/.%])*[.]?\\d+\\.?\\d*[%]?$".toRegex()
        return expression.matches(regexPattern) && !expression.endsWith("/0")
    }

    private fun convertToNumber(input: String?): Number? {
        return try {
            if (input != null) {
                input.toByteOrNull()?.toByte()
                    ?: input.toShortOrNull()?.toShort()
                    ?: input.toIntOrNull()?.toInt()
                    ?: input.toLongOrNull()?.toLong()
                    ?: input.toFloatOrNull()?.let { value ->
                        if (value.isWholeNumber()) {
                            value.toLong()
                        } else {
                            value
                        }
                    }
                    ?: input.toDoubleOrNull()?.let { value ->
                        if (value.isWholeNumber()) {
                            value.toLong()
                        } else {
                            value
                        }
                    }
            } else {
                null
            }
        } catch (e: NumberFormatException) {
            null
        }
    }

    private fun Float.isWholeNumber(): Boolean {
        return this.toLong().toFloat() == this
    }

    private fun Double.isWholeNumber(): Boolean {
        return this.toLong().toDouble() == this
    }

    data class CalculationResult(val value: Any?, val isValidExpression: Boolean)

    fun TextView.animateTextSize(fromSize: Float, toSize: Float, duration: Long) {
        val property = object : Property<TextView, Float>(Float::class.java, "textSize") {
            override fun get(view: TextView): Float {
                return view.textSize
            }

            override fun set(view: TextView, value: Float) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
            }
        }

        val animator = ObjectAnimator.ofFloat(this, property, fromSize, toSize)
        animator.duration = duration
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    fun buttSlapped(view: View) {
        val butt = view as Button
        val buttText = butt.text.toString()
        when (buttText) {
            "AC" -> {
                clearExpression()
                mekDefault()
            }

            "DEL" -> deleteLastCharacter()
            "=" -> {
                finResultTextView.text = ""
                if (resultTextView.text.isNotEmpty()) {
                    operason()
                }
            }
            else ->{
                finResultTextView.text = ""
                appendToExpression(buttText)
            }
        }
    }
}