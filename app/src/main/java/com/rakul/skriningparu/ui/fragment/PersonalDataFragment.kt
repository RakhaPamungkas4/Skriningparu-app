package com.rakul.skriningparu.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.rakul.skriningparu.R
import com.rakul.skriningparu.databinding.FragmentPersonalDataBinding

class PersonalDataFragment : Fragment() {
    private var binding: FragmentPersonalDataBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPersonalDataBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            inclLayoutAnswer.apply {
                btnAnswer1.text = getString(R.string.action_next)
                btnAnswer1.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
                btnAnswer1.setOnClickListener {
                    setupNextPage()
                }
                btnAnswer2.text = getString(R.string.action_back)
                btnAnswer2.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
                btnAnswer2.setOnClickListener {
                    activity?.onBackPressed()
                }
            }
        }
    }

    private fun setupNextPage() {
        binding?.apply {
            val action =
                PersonalDataFragmentDirections.actionScreeningIdentityFragmentToScreeningAdvanceFragment()
            inclLayoutAnswer.btnAnswer1.findNavController().navigate(action)
        }
    }
}