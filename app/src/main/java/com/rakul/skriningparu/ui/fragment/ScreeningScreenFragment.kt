package com.rakul.skriningparu.ui.fragment

import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_PX
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
import com.rakul.skriningparu.utils.const.FirebaseChildKey.FORM_SUBTOTAL_KEY_DB
import com.rakul.skriningparu.utils.dialog.showDialog
import com.rakul.skriningparu.utils.showLoading
import com.rakul.skriningparu.utils.showToast
import kotlin.math.abs
import kotlin.math.min

class ScreeningScreenFragment : Fragment() {
    private var binding: FragmentScreeningScreenBinding? = null
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private val mainViewModel: MainViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private var index = 0
    private var items = mutableListOf<ScreeningResponse>()
    private var bobot = mutableListOf<List<Double>>()

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
                bobot.clear()
                items.clear()

                items.addAll(it)
                bobot.addAll(items.map { data -> data.bobot })

                binding?.apply { setupLayoutAnswer(it[0]) }
            } else {
                Toast.makeText(requireContext(), "Terjadi Kesalahan!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAction() {
        binding?.apply {
            inclLayoutAnswer.inclLayoutDoubleAnswer.btnAnswer2.setOnClickListener {
                if (index == items.size) {
                    showDialog(
                        context = requireContext(),
                        title = getString(R.string.title_do_you_want_recheck),
                        message = getString(R.string.message_are_you_sure_this_reset_data),
                        positiveButtonText = getString(R.string.action_sure),
                        negativeButtonText = getString(R.string.action_not_sure),
                        isShowPositiveButton = true
                    ) {
                        userViewModel.clearListAnswers()
                        index = 0
                        setupUi()
                    }
                } else {
                    val answer = inclLayoutAnswer.inclLayoutDoubleAnswer.btnAnswer2.text.toString()
                    setupNextSection(bobot[index][0], answer)
                    index += 1
                    setupUi()
                }
            }
            inclLayoutAnswer.inclLayoutDoubleAnswer.btnAnswer1.setOnClickListener {
                if (index == items.size) {
                    showDialog(
                        context = requireContext(),
                        title = getString(R.string.title_are_you_sure),
                        message = getString(R.string.message_are_you_sure_this_data_ok),
                        positiveButtonText = getString(R.string.action_sure),
                        negativeButtonText = getString(R.string.action_not_sure),
                        isShowPositiveButton = true
                    ) {
                        pbLoading.showLoading(true)
                        sendDataToFirebase()
                    }
                } else {
                    val answer = inclLayoutAnswer.inclLayoutDoubleAnswer.btnAnswer1.text.toString()
                    setupNextSection(bobot[index][1], answer)
                    index += 1
                    setupUi()
                }
            }
            inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer1.setOnClickListener {
                val answer = inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer1.text.toString()
                setupNextSection(bobot[index][0], answer)
                index += 1
                setupUi()
            }
            inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer2.setOnClickListener {
                val answer = inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer2.text.toString()
                setupNextSection(bobot[index][1], answer)
                index += 1
                setupUi()
            }
            inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer3.setOnClickListener {
                val answer = inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer3.text.toString()
                setupNextSection(bobot[index][2], answer)
                index += 1
                setupUi()
            }
        }
    }

    private fun setupLayoutAnswer(data: ScreeningResponse) {
        binding?.apply {
            inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer1.setTextSize(
                COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.text_size_10sp)
            )
            inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer2.setTextSize(
                COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.text_size_10sp)
            )
            inclLayoutAnswer.inclLayoutTripleAnswer.btnAnswer3.setTextSize(
                COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.text_size_10sp)
            )

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
        val subTotalBobot = userViewModel.subTotalBobot
        databaseRef.child(userUid).child("answers").setValue(answers) { error, _ ->
            binding?.pbLoading?.showLoading(false)
            if (error != null) {
                requireContext().showToast("Failed Send Answers Data")
            } else {
                databaseRef.child(userUid).child(FORM_SUBTOTAL_KEY_DB)
                    .setValue(subTotalBobot) { errorSubTotal, _ ->
                        if (errorSubTotal != null) {
                            requireContext().showToast("Failed Send Sub Total Data")
                        } else {
                            userViewModel.clearListAnswers()
                            ResultScreeningActivity.start(
                                requireContext(),
                                userViewModel.subTotalBobot
                            )
                        }
                    }
            }
        }
    }

    private fun setupUi() {
        binding?.apply {
            if (index < items.size) {
                setupLayoutAnswer(items[index])
            } else {
                tvTitle.text = getString(R.string.label_validate_data)
                Glide.with(requireContext())
                    .load("https://firebasestorage.googleapis.com/v0/b/skrining-paru-app.appspot.com/o/second_screening_img%2Fare-you-sure-1.png?alt=media&token=43714bf0-9652-4120-a9f6-28294b2694e2")
                    .into(imgScreening)
                inclLayoutAnswer.apply {
                    inclLayoutTripleAnswer.root.visibility = View.GONE
                    inclLayoutDoubleAnswer.root.visibility = View.VISIBLE
                    inclLayoutDoubleAnswer.apply {
                        btnAnswer1.text = getString(R.string.action_sure)
                        btnAnswer1.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                        btnAnswer2.text = getString(R.string.action_recheck)
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

    private fun setupNextSection(
        bobot: Double,
        answer: String
    ) = with(userViewModel) {
        val bobotOfSymptoms = items.size
        if (index <= bobotOfSymptoms) {
            if (index == 0) {
                addSubTotalBobot(bobot)
            } else {
                addSubTotalBobot(addingToSubTotal(bobot, subTotalBobot))
            }
        }
        addUserAnswer(answer)
    }

    private fun addingToSubTotal(
        bobot: Double,
        subTotal: Double
    ): Double {
        return if (subTotal > 0 && bobot > 0) {
            subTotal + bobot * (1 - subTotal)
        } else if (subTotal < 0 && bobot < 0) {
            subTotal + bobot * (1 + subTotal)
        } else {
            (subTotal + bobot) / (1 - min(abs(subTotal), abs(bobot)))
        }
    }
}