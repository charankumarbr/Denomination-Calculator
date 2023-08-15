package `in`.phoenix.denomcalc.ui.stored

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import `in`.phoenix.denomcalc.R
import `in`.phoenix.denomcalc.adapter.SavedDenominationAdapter
import `in`.phoenix.denomcalc.adapter.listener.OnSavedDenoClickListener
import `in`.phoenix.denomcalc.databinding.ActivityStoredDenominationsBinding
import `in`.phoenix.denomcalc.model.Denomination
import `in`.phoenix.denomcalc.repository.DataState
import `in`.phoenix.denomcalc.util.gone
import `in`.phoenix.denomcalc.util.visible
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

/**
 * Created by Charan on December 12, 2020
 */
@AndroidEntryPoint
@ExperimentalCoroutinesApi
class StoredActivity: AppCompatActivity(), OnSavedDenoClickListener {

    private val LOG_TAG = "StoredActivity"

    private lateinit var binding: ActivityStoredDenominationsBinding

    private val storedViewModel: StoredViewModel by viewModels()

    @Inject
    lateinit var savedDenominationAdapter: SavedDenominationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoredDenominationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        subscribeObservers()
        storedViewModel.getSavedDenomination()
        savedDenominationAdapter.setOnSavedDenominationClickListener(this)
        binding.asdRvSaved.adapter = savedDenominationAdapter

        if (savedInstanceState == null) {
            onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        }
    }

    private fun init() {
        setSupportActionBar(binding.asdToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
    }

    private fun subscribeObservers() {
        storedViewModel.getSavedDenomination.observe(this) { dataState ->
            when (dataState) {
                is DataState.Loading -> {
                    binding.asdRvSaved.gone()
                    binding.asdPbLoading.visible()
                }

                is DataState.Success -> {
                    binding.asdPbLoading.gone()
                    val allSaved = dataState.data
                    if (allSaved.isEmpty()) {
                        Toast.makeText(this, getString(R.string.zero_saved_items), Toast.LENGTH_SHORT)
                            .show()
                        finish()

                    } else {
                        savedDenominationAdapter.submitList(allSaved)
                        binding.asdRvSaved.visible()
                    }
                }

                is DataState.Error -> {
                    binding.asdPbLoading.gone()
                    dataState.exception.printStackTrace()
                    Toast.makeText(this, getString(R.string.unable_to_display), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onSavedDenoClick(denomination: Denomination, position: Int) {
        if (!isFinishing) {
            supportFragmentManager.beginTransaction()
                .add(StoredFragment.newInstance(denomination), StoredFragment.FRAGMENT_TAG)
                .commitAllowingStateLoss()
        }
        Log.d(LOG_TAG, "Data: ${denomination.description} :: Pos: $position")
    }

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, StoredActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}