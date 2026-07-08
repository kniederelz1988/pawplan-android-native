package de.kniederelz.pawplan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.kniederelz.pawplan.R

class AuthDialogFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "AuthBottomSheetDialogFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This listens to the FragmentManager where the NavHostFragment resides
        childFragmentManager.setFragmentResultListener("closeSheet", this) { _, _ ->
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_auth, container, false)
    }
}