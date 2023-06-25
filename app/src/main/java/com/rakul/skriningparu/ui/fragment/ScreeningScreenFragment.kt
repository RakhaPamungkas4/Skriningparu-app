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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rakul.skriningparu.R
import com.rakul.skriningparu.data.model.response.ScreeningResponse
import com.rakul.skriningparu.databinding.FragmentScreeningScreenBinding
import com.rakul.skriningparu.ui.ResultScreeningActivity
import com.rakul.skriningparu.ui.viewmodel.MainViewModel
import com.rakul.skriningparu.ui.viewmodel.UserViewModel
import com.rakul.skriningparu.utils.dialog.showDialog
import com.rakul.skriningparu.utils.showLoading
import com.rakul.skriningparu.utils.showToast

class ScreeningScreenFragment : Fragment() {
    private var binding: FragmentScreeningScreenBinding? = null
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private val mainViewModel: MainViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

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
        binding?.pbLoading?.showLoading(false)
        mainViewModel.screenName = ScreeningScreenFragment::class.java.name
        setupFirebase()
        setupObserver()
        setupAction()
    }

    private fun setupObserver() {
        mainViewModel.secondScreeningData.observe(viewLifecycleOwner) {
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
                val answer = inclLayoutAnswer.inclLayoutDoubleAnswer.btnAnswer1.text.toString()
                userViewModel.addUserAnswer(answer)
                if (index == items.size - 1) {
                    showDialog(
                        context = requireContext(),
                        title = "Apakah anda yakin untuk memerika ulang?",
                        message = "Data yang telah dipilih akan dihapus dan kembali ke soal awal, apakah anda yakin?",
                        positiveButtonText = "Yakin",
                        negativeButtonText = "Belum yakin",
                        isShowPositiveButton = true
                    ) {
                        userViewModel.clearListAnswers()
                        index = 0
                        setupUi()
                    }
                } else {
                    index += 1
                    setupUi()
                }
            }
            inclLayoutAnswer.inclLayoutDoubleAnswer.btnAnswer2.setOnClickListener {
                val answer = inclLayoutAnswer.inclLayoutDoubleAnswer.btnAnswer2.text.toString()
                userViewModel.addUserAnswer(answer)
                if (index == items.size - 1) {
                    showDialog(
                        context = requireContext(),
                        title = "Apakah anda sudah yakin?",
                        message = "Data yang diisi sudah benar?",
                        positiveButtonText = "Yakin",
                        negativeButtonText = "Belum yakin",
                        isShowPositiveButton = true
                    ) {
                        pbLoading.showLoading(true)
                        sendDataToFirebase()
                    }
                } else {
                    index += 1
                    setupUi()
                }
            }
            inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer1.setOnClickListener {
                index += 1
                val answer = inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer1.text.toString()
                userViewModel.addUserAnswer(answer)
                setupUi()
            }
            inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer2.setOnClickListener {
                index += 1
                val answer = inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer2.text.toString()
                userViewModel.addUserAnswer(answer)
                setupUi()
            }
            inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer3.setOnClickListener {
                index += 1
                val answer = inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer3.text.toString()
                userViewModel.addUserAnswer(answer)
                setupUi()
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

    private fun setupFirebase() {
        firebaseDatabase = Firebase.database
        databaseRef = firebaseDatabase.reference.child("user")
    }

    private fun sendDataToFirebase() {
        val userUid = userViewModel.userUid
        val answers = userViewModel.listAnswers
        databaseRef.child(userUid).child("answers").setValue(answers) { error, _ ->
            binding?.pbLoading?.showLoading(false)
            if (error != null) {
                requireContext().showToast("Failed Send Data")
            } else {
                userViewModel.clearListAnswers()
                ResultScreeningActivity.start(requireContext())
            }
        }
    }

    private fun setupUi() {
        binding?.apply {
            if (index < items.size) {
                setupLayoutAnswer(items[index])
            }
        }
    }
}