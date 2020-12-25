package `in`.phoenix.denomcalc.ui.stored

import `in`.phoenix.denomcalc.R
import `in`.phoenix.denomcalc.model.Denomination
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

/**
 * Created by Charan on December 12, 2020
 */
class StoredFragment: DialogFragment() {

    private var denomination: Denomination? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        denomination = arguments?.get(DATA_DENOMINATION) as Denomination
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_saved_denomination, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCanceledOnTouchOutside(false)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvDesc: TextView = view.findViewById(R.id.dsdTvDesc)
        tvDesc.movementMethod = ScrollingMovementMethod()
        tvDesc.text = denomination?.description

        view.findViewById<Button>(R.id.dsdBtnShare).setOnClickListener(clickListener)
    }

    private val clickListener = View.OnClickListener {
        when (it.id) {
            R.id.dsdBtnShare -> {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.putExtra(Intent.EXTRA_TEXT, denomination?.description)
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, denomination?.totalValueForShare)
                shareIntent.type = "text/plain"
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_denomination)))
            }
        }
    }

    companion object {
        fun newInstance(denomination: Denomination): StoredFragment {
            return StoredFragment().apply {
                val bundle = Bundle()
                bundle.putParcelable(DATA_DENOMINATION, denomination)
                arguments = bundle
            }
        }

        const val DATA_DENOMINATION = "DENOMINATION_DATA"

        const val FRAGMENT_TAG = "StoredFragment"
    }
}