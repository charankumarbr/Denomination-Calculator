package `in`.phoenix.denomcalc.ui.home

import `in`.phoenix.denomcalc.R
import `in`.phoenix.denomcalc.model.DenominationConstants.Companion.DENO_1
import `in`.phoenix.denomcalc.model.DenominationConstants.Companion.DENO_10
import `in`.phoenix.denomcalc.model.DenominationConstants.Companion.DENO_100
import `in`.phoenix.denomcalc.model.DenominationConstants.Companion.DENO_2
import `in`.phoenix.denomcalc.model.DenominationConstants.Companion.DENO_20
import `in`.phoenix.denomcalc.model.DenominationConstants.Companion.DENO_200
import `in`.phoenix.denomcalc.model.DenominationConstants.Companion.DENO_2000
import `in`.phoenix.denomcalc.model.DenominationConstants.Companion.DENO_5
import `in`.phoenix.denomcalc.model.DenominationConstants.Companion.DENO_50
import `in`.phoenix.denomcalc.model.DenominationConstants.Companion.DENO_500
import `in`.phoenix.denomcalc.model.DenominationConstants.Companion.TYPE_COIN
import `in`.phoenix.denomcalc.model.DenominationConstants.Companion.TYPE_NOTE
import `in`.phoenix.denomcalc.model.LineItem
import `in`.phoenix.denomcalc.repository.DataState
import `in`.phoenix.denomcalc.ui.stored.StoredActivity
import `in`.phoenix.denomcalc.util.CalcUtil
import `in`.phoenix.denomcalc.util.gone
import `in`.phoenix.denomcalc.util.visible
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Created by Charan on December 06, 2020
 */
@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private var focusedQty: EditText? = null
    private var focusedLineTotal: TextView? = null
    private var focusedLineDenomination = 0
    private var focusedLineItem: LineItem? = null

    private lateinit var lineItems: MutableList<LineItem>

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Toast.makeText(this, "App by: Phoenix Apps", Toast.LENGTH_SHORT).show()
        init()
        subscribeObservers()
    }

    private fun init() {
        setSupportActionBar(amToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            amToolbar.overflowIcon?.colorFilter = BlendModeColorFilter(Color.WHITE, BlendMode.SRC_ATOP)
        } else {
            amToolbar.overflowIcon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        }
        with (onQtyFocusListener) {
            etQty2000.onFocusChangeListener = this
            etQty500.onFocusChangeListener = this
            etQty200.onFocusChangeListener = this
            etQty100.onFocusChangeListener = this
            etQty50.onFocusChangeListener = this
            etQty20.onFocusChangeListener = this
            etQty10.onFocusChangeListener = this
            etQty5.onFocusChangeListener = this
            etQty2.onFocusChangeListener = this
            etQty1.onFocusChangeListener = this
            etCQty10.onFocusChangeListener = this
            etCQty5.onFocusChangeListener = this
            etCQty2.onFocusChangeListener = this
            etCQty1.onFocusChangeListener = this
        }

        with (onQtyChangeWatcher) {
            etQty2000.addTextChangedListener(this)
            etQty500.addTextChangedListener(this)
            etQty200.addTextChangedListener(this)
            etQty100.addTextChangedListener(this)
            etQty50.addTextChangedListener(this)
            etQty20.addTextChangedListener(this)
            etQty10.addTextChangedListener(this)
            etQty5.addTextChangedListener(this)
            etQty2.addTextChangedListener(this)
            etQty1.addTextChangedListener(this)
            etCQty10.addTextChangedListener(this)
            etCQty5.addTextChangedListener(this)
            etCQty2.addTextChangedListener(this)
            etCQty1.addTextChangedListener(this)
        }

        amTvClear.setOnClickListener(clickListener)
        amTvShare.setOnClickListener(clickListener)
        amTvSave.setOnClickListener(clickListener)

        lineItems = mutableListOf(
            LineItem(TYPE_NOTE, DENO_2000, 0),
            LineItem(TYPE_NOTE, DENO_500, 0),
            LineItem(TYPE_NOTE, DENO_200, 0),
            LineItem(TYPE_NOTE, DENO_100, 0),
            LineItem(TYPE_NOTE, DENO_50, 0),
            LineItem(TYPE_NOTE, DENO_20, 0),
            LineItem(TYPE_NOTE, DENO_10, 0),
            LineItem(TYPE_NOTE, DENO_5, 0),
            LineItem(TYPE_NOTE, DENO_2, 0),
            LineItem(TYPE_NOTE, DENO_1, 0),
            LineItem(TYPE_COIN, DENO_10, 0),
            LineItem(TYPE_COIN, DENO_5, 0),
            LineItem(TYPE_COIN, DENO_2, 0),
            LineItem(TYPE_COIN, DENO_1, 0)
        )
    }

    private fun subscribeObservers() {
        mainViewModel.saveDenomination.observe(this, {
            when (it) {
                is DataState.Success -> {
                    amLayoutLoading.gone()
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                }

                is DataState.Error -> {
                    amLayoutLoading.gone()
                    Toast.makeText(this, "Unable to save", Toast.LENGTH_SHORT).show()
                }

                is DataState.Loading -> {
                    amLayoutLoading.visible()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_saved -> {
                StoredActivity.startActivity(this)
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private val clickListener = View.OnClickListener {
        when (it.id) {
            R.id.amTvClear -> {
                clearAll()
            }

            R.id.amTvShare -> {
                if (!TextUtils.isEmpty(amTvTotalValue.text.toString()) &&
                    amTvTotalValue.text.toString() != "0") {

                    if (amLayoutLoading.visibility == View.GONE) {
                        amLayoutLoading.visible()
                        val shareData = CalcUtil.prepareShareText(lineItems)
                        Log.d("share data ready", "share")
                        val shareIntent = Intent(ACTION_SEND)
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareData.message)
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareData.subject)
                        shareIntent.type = "text/plain"
                        startActivity(Intent.createChooser(shareIntent, "Share Denomination"))
                        amLayoutLoading.gone()
                    }

                } else {
                    Toast.makeText(this, "Nothing to share", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.amTvSave -> {
                if (!TextUtils.isEmpty(amTvTotalValue.text.toString()) &&
                    amTvTotalValue.text.toString() != "0") {

                    if (amLayoutLoading.visibility == View.GONE) {
                        amLayoutLoading.visible()
                        val shareData = CalcUtil.prepareShareText(lineItems)
                        mainViewModel.saveDenomination(shareData)
                    }

                } else {
                    Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val onQtyFocusListener = View.OnFocusChangeListener { qtyView, hasFocus ->
        if (hasFocus) {
            focusedQty = qtyView as EditText?
            var denoType = -1
            var deno = -1
            when (qtyView) {
                etQty1 -> {
                    focusedLineDenomination = 1
                    focusedLineTotal = tvValue1
                    denoType = TYPE_NOTE
                    deno = DENO_1
                }

                etCQty1 -> {
                    focusedLineDenomination = 1
                    focusedLineTotal = tvCValue1
                    denoType = TYPE_COIN
                    deno = DENO_1
                }

                etQty2 -> {
                    focusedLineDenomination = 2
                    focusedLineTotal = tvValue2
                    denoType = TYPE_NOTE
                    deno = DENO_2
                }

                etCQty2 -> {
                    focusedLineDenomination = 2
                    focusedLineTotal = tvCValue2
                    denoType = TYPE_COIN
                    deno = DENO_2
                }

                etQty5 -> {
                    focusedLineDenomination = 5
                    focusedLineTotal = tvValue5
                    denoType = TYPE_NOTE
                    deno = DENO_5
                }

                etCQty5 -> {
                    focusedLineDenomination = 5
                    focusedLineTotal = tvCValue5
                    denoType = TYPE_COIN
                    deno = DENO_5
                }

                etQty10 -> {
                    focusedLineDenomination = 10
                    focusedLineTotal = tvValue10
                    denoType = TYPE_NOTE
                    deno = DENO_10
                }

                etCQty10 -> {
                    focusedLineDenomination = 10
                    focusedLineTotal = tvCValue10
                    denoType = TYPE_COIN
                    deno = DENO_10
                }

                etQty20 -> {
                    focusedLineDenomination = 20
                    focusedLineTotal = tvValue20
                    denoType = TYPE_NOTE
                    deno = DENO_20
                }

                etQty50 -> {
                    focusedLineDenomination = 50
                    focusedLineTotal = tvValue50
                    denoType = TYPE_NOTE
                    deno = DENO_50
                }

                etQty100 -> {
                    focusedLineDenomination = 100
                    focusedLineTotal = tvValue100
                    denoType = TYPE_NOTE
                    deno = DENO_100
                }

                etQty200 -> {
                    focusedLineDenomination = 200
                    focusedLineTotal = tvValue200
                    denoType = TYPE_NOTE
                    deno = DENO_200
                }

                etQty500 -> {
                    focusedLineDenomination = 500
                    focusedLineTotal = tvValue500
                    denoType = TYPE_NOTE
                    deno = DENO_500
                }

                etQty2000 -> {
                    focusedLineDenomination = 2000
                    focusedLineTotal = tvValue2000
                    denoType = TYPE_NOTE
                    deno = DENO_2000
                }
            }

            focusedLineItem = findLineItem(denoType, deno)
        }
    }

    private fun findLineItem(denoType: Int, deno: Int): LineItem? {
        return lineItems?.find {
            it.denominationType == denoType &&
                    it.lineDenomination == deno
        }
    }

    private val onQtyChangeWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            val lineTotal = lineTotal(focusedLineDenomination, s.toString())
            Log.d("LineTotal", "${focusedLineDenomination}::" + lineTotal)
            focusedLineTotal?.text = lineTotal.toString()
            focusedLineItem?.lineQty = CalcUtil.formatQty(s.toString())
            doAllLineTotal()
        }
    }

    private fun lineTotal(lineDenomination: Int, quantity: String): Int {
        return if (TextUtils.isEmpty(quantity)) {
            0
        } else {
            try {
                lineDenomination * quantity.toInt()
            } catch (e: NumberFormatException) {
                0
            }
        }
    }

    private fun doAllLineTotal() {

        var total = CalcUtil.allLineTotal(lineItems)
        /*if (isValidEntry(tvValue2000.text.toString())) {
            total += tvValue2000.text.toString().toInt()
        }

        if (isValidEntry(tvValue500.text.toString())) {
            total += tvValue500.text.toString().toInt()
        }

        if (isValidEntry(tvValue200.text.toString())) {
            total += tvValue200.text.toString().toInt()
        }

        if (isValidEntry(tvValue100.text.toString())) {
            total += tvValue100.text.toString().toInt()
        }

        if (isValidEntry(tvValue50.text.toString())) {
            total += tvValue50.text.toString().toInt()
        }

        if (isValidEntry(tvValue20.text.toString())) {
            total += tvValue20.text.toString().toInt()
        }

        if (isValidEntry(tvValue10.text.toString())) {
            total += tvValue10.text.toString().toInt()
        }

        if (isValidEntry(tvValue5.text.toString())) {
            total += tvValue5.text.toString().toInt()
        }

        if (isValidEntry(tvValue2.text.toString())) {
            total += tvValue2.text.toString().toInt()
        }

        if (isValidEntry(tvValue1.text.toString())) {
            total += tvValue1.text.toString().toInt()
        }

        if (isValidEntry(tvCValue10.text.toString())) {
            total += tvCValue10.text.toString().toInt()
        }

        if (isValidEntry(tvCValue5.text.toString())) {
            total += tvCValue5.text.toString().toInt()
        }

        if (isValidEntry(tvCValue2.text.toString())) {
            total += tvCValue2.text.toString().toInt()
        }

        if (isValidEntry(tvCValue1.text.toString())) {
            total += tvCValue1.text.toString().toInt()
        }*/

        amTvTotalValue.text = total.toString()
    }

    private fun clearAll() {
        if (!TextUtils.isEmpty(amTvTotalValue.text.toString())) {
            confirmClearAll()
        }
    }

    private fun confirmClearAll() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Clear")
            .setMessage("Are you sure you want to clear all inputs?")
            .setPositiveButton("Yes") { dialog, _ ->
                doClearAll()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

        if (!isFinishing && !isChangingConfigurations) {
            alertDialog.show()
        }
    }

    private fun doClearAll() {
        val zeroString = ""
        focusedQty?.clearFocus()
        with (zeroString) {
            etQty2000.setText(this)
            etQty500.setText(this)
            etQty200.setText(this)
            etQty100.setText(this)
            etQty50.setText(this)
            etQty20.setText(this)
            etQty10.setText(this)
            etQty5.setText(this)
            etQty2.setText(this)
            etQty1.setText(this)
            etCQty10.setText(this)
            etCQty5.setText(this)
            etCQty2.setText(this)
            etCQty1.setText(this)

            tvValue2000.text = this
            tvValue500.text = this
            tvValue200.text = this
            tvValue100.text = this
            tvValue50.text = this
            tvValue20.text = this
            tvValue10.text = this
            tvValue5.text = this
            tvValue2.text = this
            tvValue1.text = this
            tvCValue10.text = this
            tvCValue5.text = this
            tvCValue2.text = this
            tvCValue1.text = this

            amTvTotalValue.text = this
            amTvTotalValueInWords.text = this

            lineItems.forEach {
                it.lineQty = 0
            }
        }
    }
}