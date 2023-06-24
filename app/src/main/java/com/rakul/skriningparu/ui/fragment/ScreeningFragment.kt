package com.rakul.skriningparu.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.rakul.skriningparu.data.model.response.ScreeningResponse
import com.rakul.skriningparu.databinding.FragmentScreeningBinding
import com.rakul.skriningparu.ui.viewmodel.MainViewModel

class ScreeningFragment : Fragment() {
    private var binding: FragmentScreeningBinding? = null
    private val mainViewModel: MainViewModel by activityViewModels()

    private var index = 0
    private val items = mutableListOf<ScreeningResponse>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScreeningBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        setupAction()
        mainViewModel.screenName = ScreeningFragment::class.java.name
    }

    private fun setupObserver() {
        mainViewModel.firstScreeningData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                index = 0
                items.clear()
                items.addAll(it)
                binding?.apply {
                    tvTitle.text = it[index].title
                    inclLayoutAnswer.btnAnswer1.text = it[index].answer[0]
                    inclLayoutAnswer.btnAnswer2.text = it[index].answer[1]
                }
            } else {
                Toast.makeText(requireContext(), "Terjadi Kesalahan!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAction() {
        binding?.apply {
            inclLayoutAnswer.btnAnswer1.setOnClickListener {
                index += 1
                setupNextPage(index, it)
            }
            inclLayoutAnswer.btnAnswer2.setOnClickListener {
                index += 1
                setupNextPage(index, it)
            }
        }
    }

    private fun setupNextPage(index: Int, view: View) {
        binding?.apply {
            if (index < items.size) {
                tvTitle.text = items[index].title
                inclLayoutAnswer.btnAnswer1.text = items[index].answer[0]
                inclLayoutAnswer.btnAnswer2.text = items[index].answer[1]
            } else {
                val action =
                    ScreeningFragmentDirections.actionScreeningFragmentToScreeningIdentityFragment()
                view.findNavController().navigate(action)
            }
        }
    }
}