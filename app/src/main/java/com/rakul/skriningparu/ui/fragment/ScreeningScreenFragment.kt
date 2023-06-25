package com.rakul.skriningparu.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.rakul.skriningparu.R
import com.rakul.skriningparu.data.model.response.ScreeningResponse
import com.rakul.skriningparu.databinding.FragmentScreeningScreenBinding
import com.rakul.skriningparu.ui.viewmodel.MainViewModel

class ScreeningScreenFragment : Fragment() {
    private var binding: FragmentScreeningScreenBinding? = null
    private val mainViewMode: MainViewModel by activityViewModels()

    private var index = 0
    private var items = mutableListOf<ScreeningResponse>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScreeningScreenBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        setupAction()
    }

    private fun setupObserver() {
        mainViewMode.secondScreeningData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                index = 0
                items.clear()
                items.addAll(it)
                binding?.apply {
                    setupLayoutAnswer(it[0])
                }
            } else {
                Toast.makeText(requireContext(), "Terjadi Kesalahan!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAction() {
        binding?.apply {
            inclLayoutAnswer.inclLayoutDoubleAnswer.btnAnswer1.setOnClickListener {
                index += 1
                setupNextPage()
            }
            inclLayoutAnswer.inclLayoutDoubleAnswer.btnAnswer2.setOnClickListener {
                index += 1
                setupNextPage()
            }
            inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer1.setOnClickListener {
                index += 1
                setupNextPage()
            }
            inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer2.setOnClickListener {
                index += 1
                setupNextPage()
            }
            inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer3.setOnClickListener {
                index += 1
                setupNextPage()
            }
        }
    }

    private fun setupLayoutAnswer(data: ScreeningResponse) {
        binding?.apply {
            tvTitle.text = data.title
            Glide.with(requireContext())
                .load(data.image)
                .into(imgScreening)
            if (data.answer.size > 2) {
                inclLayoutAnswer.apply {
                    inclLayoutDoubleAnswer.root.visibility = View.GONE
                    inclLayoutTripleAnswer.root.visibility = View.VISIBLE

                    inclLayoutTripleAnswer.btnAnswer1.text = data.answer[0]
                    inclLayoutTripleAnswer.btnAnswer2.text = data.answer[1]
                    inclLayoutTripleAnswer.btnAnswer3.text = data.answer[2]
                }
            } else {
                inclLayoutAnswer.apply {
                    inclLayoutTripleAnswer.root.visibility = View.GONE
                    inclLayoutDoubleAnswer.root.visibility = View.VISIBLE
                    inclLayoutDoubleAnswer.apply {
                        btnAnswer1.text = data.answer[1]
                        btnAnswer1.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                        btnAnswer2.text = data.answer[0]
                        btnAnswer2.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setupNextPage() {
        binding?.apply {
            if (index < items.size) {
                setupLayoutAnswer(items[index])
            } else {
                // TODO : Go to Screening Result Page
            }
        }
    }
}