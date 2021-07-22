package com.edgar.kotlincalculator

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.edgar.kotlincalculator.databinding.ActivityMainBinding
import java.lang.NumberFormatException

private const val STATE_PENDING_OPERATION = "PendingOperation"
private const val STATE_OPERAND1 = "Operand1"
private const val STATE_OPERAND1_STORED = "Operand1_Stored"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var operand1: Double? = null
    private var pendingOperation = "="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLightImage.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            clearEverything()
        }
        binding.btnDarkImage.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            clearEverything()
        }

        val listener = View.OnClickListener { v ->
            val b = v as Button
            binding.newNumber.append(b.text)
        }

        binding.btnZero.setOnClickListener(listener)
        binding.btnOne.setOnClickListener(listener)
        binding.btnTwo.setOnClickListener(listener)
        binding.btnThree.setOnClickListener(listener)
        binding.btnFour.setOnClickListener(listener)
        binding.btnFive.setOnClickListener(listener)
        binding.btnSix.setOnClickListener(listener)
        binding.btnSeven.setOnClickListener(listener)
        binding.btnEight.setOnClickListener(listener)
        binding.btnNine.setOnClickListener(listener)
        binding.btnDot.setOnClickListener(listener)

        val opListener = View.OnClickListener { v ->
            val op = (v as Button).text.toString()
            try {
                val value = binding.newNumber.text.toString().toDouble()
                performOperation(value, op)
            } catch (e: NumberFormatException){
                binding.newNumber.text = ""
            }
            pendingOperation = op
            binding.operation.text = pendingOperation
        }

        binding.btnEquals.setOnClickListener(opListener)
        binding.btnDivide.setOnClickListener(opListener)
        binding.btnMultiply.setOnClickListener(opListener)
        binding.btnMinus.setOnClickListener(opListener)
        binding.btnPlus.setOnClickListener(opListener)

        binding.btnNegative.setOnClickListener {
            val value = binding.newNumber.text.toString()
            if (value.isEmpty()){
                binding.newNumber.text = "-"
            } else {
                try {
                    var doubleValue = value.toDouble()
                    doubleValue *= -1
                    binding.newNumber.text = ("")
                } catch (e: NumberFormatException){
                    binding.newNumber.text = ""
                }
            }
        }

        binding.btnClear.setOnClickListener { _ ->
            clearEverything()
        }
    }

    private fun performOperation(value: Double, operation: String) {
        if (operand1 == null) {
            operand1 = value
        } else {
            if (pendingOperation == "=") {
                pendingOperation = operation
            }

            when (pendingOperation) {
                "=" -> operand1 = value
                "/" -> operand1 = if (value == 0.0) {
                    Double.NaN
                } else {
                    operand1!! / value
                }
                "*" -> operand1 = operand1!! * value
                "-" -> operand1 = operand1!! - value
                "+" -> operand1 = operand1!! + value
            }
        }
        binding.result.text = removeZeroAfterDot(operand1.toString())
        binding.newNumber.text = ""
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (operand1 != null) {
            outState.putDouble(STATE_OPERAND1, operand1!!)
            outState.putBoolean(STATE_OPERAND1_STORED, true)
        }
        outState.putString(STATE_PENDING_OPERATION, pendingOperation)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        operand1 = if (savedInstanceState.getBoolean(STATE_OPERAND1_STORED, false)){
            savedInstanceState.getDouble(STATE_OPERAND1)
        } else {
            null
        }
        pendingOperation = savedInstanceState.getString(STATE_PENDING_OPERATION).toString()
        binding.operation.text = pendingOperation
    }

    private fun clearEverything(){
        binding.newNumber.text = ""
        binding.operation.text = ""
        binding.result.text = ""
        pendingOperation = ""
        operand1 = null
    }

    private fun removeZeroAfterDot(result: String) : String{
        var value = result
        if(result.endsWith(".0"))
            value = result.substring(0, result.length - 2)
        return value
    }
}


