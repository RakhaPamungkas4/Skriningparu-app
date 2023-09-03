package com.rakul.skriningparu.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.rakul.skriningparu.databinding.FragmentConsentSheetBinding
import com.rakul.skriningparu.ui.viewmodel.MainViewModel
import com.rakul.skriningparu.utils.HtmlUtils.setHtmlHandlersText

class ConsentSheetFragment : Fragment() {
    private var binding: FragmentConsentSheetBinding? = null
    private val mainViewModel: MainViewModel by activityViewModels()

    private var isChecked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConsentSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        setupAction()
    }

    private fun setupObserver() {
        mainViewModel.consentData.observe(viewLifecycleOwner) { data ->
            binding?.apply {
                if (data.title.isNotEmpty() && data.description.isNotEmpty()) {
                    tvTitle.text = data.title
                    tvDesc.text = data.description.setHtmlHandlersText(true)
                    cbAgree.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupAction() {
        binding?.apply {
            btnAgree.setOnClickListener { button ->
                val action =
                    ConsentSheetFragmentDirections.actionConsentSheetFragmentToScreeningFragment()
                button?.findNavController()?.navigate(action)
                cbAgree.isChecked = false
                isChecked = false
            }
            cbAgree.setOnClickListener {
                isChecked = !isChecked
                btnAgree.isEnabled = isChecked
            }
        }
    }
}