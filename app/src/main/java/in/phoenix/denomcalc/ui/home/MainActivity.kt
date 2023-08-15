package `in`.phoenix.denomcalc.ui.home

import `in`.phoenix.denomcalc.R
import `in`.phoenix.denomcalc.databinding.ActivityMainBinding
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
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Created by Charan on December 06, 2020
 */
@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private val LOG_TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    private var focusedQty: EditText? = null
    private var focusedLineTotal: TextView? = null
    private var focusedLineDenomination = 0
    private var focusedLineItem: LineItem? = null

    private lateinit var lineItems: MutableList<LineItem>

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Toast.makeText(this, "App by: Phoenix Apps", Toast.LENGTH_SHORT).show()
        init()
        subscribeObservers()
    }

    private fun init() {
        setSupportActionBar(binding.amToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            binding.amToolbar.overflowIcon?.colorFilter = BlendModeColorFilter(Color.WHITE, BlendMode.SRC_ATOP)
        } else {
            binding.amToolbar.overflowIcon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        }
        with (onQtyFocusListener) {
            binding.etQty2000.onFocusChangeListener = this
            binding.etQty500.onFocusChangeListener = this
            binding.etQty200.onFocusChangeListener = this
            binding.etQty100.onFocusChangeListener = this
            binding.etQty50.onFocusChangeListener = this
            binding.etQty20.onFocusChangeListener = this
            binding.etQty10.onFocusChangeListener = this
            binding.etQty5.onFocusChangeListener = this
            binding.etQty2.onFocusChangeListener = this
            binding.etQty1.onFocusChangeListener = this
            binding.etCQty10.onFocusChangeListener = this
            binding.etCQty5.onFocusChangeListener = this
            binding.etCQty2.onFocusChangeListener = this
            binding.etCQty1.onFocusChangeListener = this
        }

        with (onQtyChangeWatcher) {
            binding.etQty2000.addTextChangedListener(this)
            binding.etQty500.addTextChangedListener(this)
            binding.etQty200.addTextChangedListener(this)
            binding.etQty100.addTextChangedListener(this)
            binding.etQty50.addTextChangedListener(this)
            binding.etQty20.addTextChangedListener(this)
            binding.etQty10.addTextChangedListener(this)
            binding.etQty5.addTextChangedListener(this)
            binding.etQty2.addTextChangedListener(this)
            binding.etQty1.addTextChangedListener(this)
            binding.etCQty10.addTextChangedListener(this)
            binding.etCQty5.addTextChangedListener(this)
            binding.etCQty2.addTextChangedListener(this)
            binding.etCQty1.addTextChangedListener(this)
        }

        binding.amTvClear.setOnClickListener(clickListener)
        binding.amTvShare.setOnClickListener(clickListener)
        binding.amTvSave.setOnClickListener(clickListener)

        lineItems = mainViewModel.lineItems
    }

    private fun subscribeObservers() {
        mainViewModel.saveDenomination.observe(this, { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    binding.amLayoutLoading.gone()
                    Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
                }

                is DataState.Error -> {
                    binding.amLayoutLoading.gone()
                    dataState.exception.printStackTrace()
                    Toast.makeText(this, getString(R.string.unable_to_save), Toast.LENGTH_SHORT).show()
                }

                is DataState.Loading -> {
                    binding.amLayoutLoading.visible()
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
                if (!TextUtils.isEmpty(binding.amTvTotalValue.text.toString()) &&
                    binding.amTvTotalValue.text.toString() != "0") {

                    if (binding.amLayoutLoading.visibility == View.GONE) {
                        binding.amLayoutLoading.visible()
                        val shareData = CalcUtil.prepareShareText(lineItems)
                        Log.d(LOG_TAG, "share - share data ready")

                        val shareIntent = Intent(ACTION_SEND)
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareData.message)
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareData.subject)
                        shareIntent.type = "text/plain"
                        startActivity(Intent.createChooser(shareIntent, "Share Denomination"))

                        binding.amLayoutLoading.gone()
                    }

                } else {
                    Toast.makeText(this, getString(R.string.nothing_to_share), Toast.LENGTH_SHORT).show()
                }
            }

            R.id.amTvSave -> {
                if (!TextUtils.isEmpty(binding.amTvTotalValue.text.toString()) &&
                    binding.amTvTotalValue.text.toString() != "0") {

                    if (binding.amLayoutLoading.visibility == View.GONE) {
                        binding.amLayoutLoading.visible()
                        val shareData = CalcUtil.prepareShareText(lineItems)
                        mainViewModel.saveDenomination(shareData)
                    }

                } else {
                    Toast.makeText(this, getString(R.string.nothing_to_share), Toast.LENGTH_SHORT).show()
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
                binding.etQty1 -> {
                    focusedLineDenomination = 1
                    focusedLineTotal = binding.tvValue1
                    denoType = TYPE_NOTE
                    deno = DENO_1
                }

                binding.etCQty1 -> {
                    focusedLineDenomination = 1
                    focusedLineTotal = binding.tvCValue1
                    denoType = TYPE_COIN
                    deno = DENO_1
                }

                binding.etQty2 -> {
                    focusedLineDenomination = 2
                    focusedLineTotal = binding.tvValue2
                    denoType = TYPE_NOTE
                    deno = DENO_2
                }

                binding.etCQty2 -> {
                    focusedLineDenomination = 2
                    focusedLineTotal = binding.tvCValue2
                    denoType = TYPE_COIN
                    deno = DENO_2
                }

                binding.etQty5 -> {
                    focusedLineDenomination = 5
                    focusedLineTotal = binding.tvValue5
                    denoType = TYPE_NOTE
                    deno = DENO_5
                }

                binding.etCQty5 -> {
                    focusedLineDenomination = 5
                    focusedLineTotal = binding.tvCValue5
                    denoType = TYPE_COIN
                    deno = DENO_5
                }

                binding.etQty10 -> {
                    focusedLineDenomination = 10
                    focusedLineTotal = binding.tvValue10
                    denoType = TYPE_NOTE
                    deno = DENO_10
                }

                binding.etCQty10 -> {
                    focusedLineDenomination = 10
                    focusedLineTotal = binding.tvCValue10
                    denoType = TYPE_COIN
                    deno = DENO_10
                }

                binding.etQty20 -> {
                    focusedLineDenomination = 20
                    focusedLineTotal = binding.tvValue20
                    denoType = TYPE_NOTE
                    deno = DENO_20
                }

                binding.etQty50 -> {
                    focusedLineDenomination = 50
                    focusedLineTotal = binding.tvValue50
                    denoType = TYPE_NOTE
                    deno = DENO_50
                }

                binding.etQty100 -> {
                    focusedLineDenomination = 100
                    focusedLineTotal = binding.tvValue100
                    denoType = TYPE_NOTE
                    deno = DENO_100
                }

                binding.etQty200 -> {
                    focusedLineDenomination = 200
                    focusedLineTotal = binding.tvValue200
                    denoType = TYPE_NOTE
                    deno = DENO_200
                }

                binding.etQty500 -> {
                    focusedLineDenomination = 500
                    focusedLineTotal = binding.tvValue500
                    denoType = TYPE_NOTE
                    deno = DENO_500
                }

                binding.etQty2000 -> {
                    focusedLineDenomination = 2000
                    focusedLineTotal = binding.tvValue2000
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
            //-- purposefully left empty --//
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //-- purposefully left empty --//
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
        val total = CalcUtil.allLineTotal(lineItems)
        binding.amTvTotalValue.text = total.toString()
    }

    private fun clearAll() {
        if (!TextUtils.isEmpty(binding.amTvTotalValue.text.toString())) {
            confirmClearAll()
        }
    }

    private fun confirmClearAll() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.clear))
            .setMessage(getString(R.string.confirm_clear_all))
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
            binding.etQty2000.setText(this)
            binding.etQty500.setText(this)
            binding.etQty200.setText(this)
            binding.etQty100.setText(this)
            binding.etQty50.setText(this)
            binding.etQty20.setText(this)
            binding.etQty10.setText(this)
            binding.etQty5.setText(this)
            binding.etQty2.setText(this)
            binding.etQty1.setText(this)
            binding.etCQty10.setText(this)
            binding.etCQty5.setText(this)
            binding.etCQty2.setText(this)
            binding.etCQty1.setText(this)

            binding.tvValue2000.text = this
            binding.tvValue500.text = this
            binding.tvValue200.text = this
            binding.tvValue100.text = this
            binding.tvValue50.text = this
            binding.tvValue20.text = this
            binding.tvValue10.text = this
            binding.tvValue5.text = this
            binding.tvValue2.text = this
            binding.tvValue1.text = this
            binding.tvCValue10.text = this
            binding.tvCValue5.text = this
            binding.tvCValue2.text = this
            binding.tvCValue1.text = this

            binding.amTvTotalValue.text = this
            binding.amTvTotalValueInWords.text = this

            lineItems.forEach {
                it.lineQty = 0
            }
        }
    }
}