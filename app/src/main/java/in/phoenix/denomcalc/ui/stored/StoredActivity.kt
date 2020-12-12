package `in`.phoenix.denomcalc.ui.stored

import `in`.phoenix.denomcalc.R
import `in`.phoenix.denomcalc.adapter.OnSavedDenoClickListener
import `in`.phoenix.denomcalc.adapter.SavedDenominationAdapter
import `in`.phoenix.denomcalc.model.Denomination
import `in`.phoenix.denomcalc.repository.DataState
import `in`.phoenix.denomcalc.util.gone
import `in`.phoenix.denomcalc.util.visible
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_stored_denominations.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

/**
 * Created by Charan on December 12, 2020
 */
@AndroidEntryPoint
@ExperimentalCoroutinesApi
class StoredActivity: AppCompatActivity(), OnSavedDenoClickListener {

    private val storedViewModel: StoredViewModel by viewModels()

    @Inject
    lateinit var savedDenominationAdapter: SavedDenominationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stored_denominations)

        init()
        subscribeObservers()
        storedViewModel.getSavedDenomination()
        savedDenominationAdapter.setOnSavedDenominationClickListener(this)
        asdRvSaved.adapter = savedDenominationAdapter
    }

    private fun init() {
        setSupportActionBar(asdToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24)
    }

    private fun subscribeObservers() {
        storedViewModel.getSavedDenomination.observe(this, {
            when (it) {
                is DataState.Loading -> {
                    asdRvSaved.gone()
                    asdPbLoading.visible()
                }

                is DataState.Success -> {
                    asdPbLoading.gone()
                    var allSaved = it.data
                    if (allSaved.isEmpty()) {
                        Toast.makeText(this, "Zero saved items to display", Toast.LENGTH_SHORT)
                            .show()
                        finish()

                    } else {
                        savedDenominationAdapter.submitList(allSaved)
                        asdRvSaved.visible()
                    }
                }

                is DataState.Error -> {
                    asdPbLoading.gone()
                    Toast.makeText(this, "Unable to display", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        })
    }

    override fun onSavedDenoClick(denomination: Denomination, position: Int) {
        if (!isFinishing) {
            supportFragmentManager.beginTransaction()
                .add(StoredFragment.newInstance(denomination), "StoredFragment")
                .commitAllowingStateLoss()
        }
        Log.d("StoredAc", "Data: ${denomination.description} Pos: $position")
    }

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, StoredActivity::class.java)
            context.startActivity(intent)
        }
    }
}