package com.rakul.skriningparu.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rakul.skriningparu.R
import com.rakul.skriningparu.data.model.request.IdentityRequest
import com.rakul.skriningparu.databinding.FragmentPersonalDataBinding
import com.rakul.skriningparu.ui.viewmodel.MainViewModel
import com.rakul.skriningparu.ui.viewmodel.UserViewModel
import com.rakul.skriningparu.utils.dialog.showDialog
import com.rakul.skriningparu.utils.showLoading
import com.rakul.skriningparu.utils.showToast
import java.util.Random

class PersonalDataFragment : Fragment() {
    private var binding: FragmentPersonalDataBinding? = null
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private val mainViewModel: MainViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

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
        setupFirebase()
        mainViewModel.screenName = PersonalDataFragment::class.java.name
        binding?.apply {
            pbLoading.showLoading(false)
            inclLayoutAnswer.apply {
                btnAnswer1.text = getString(R.string.action_next)
                btnAnswer1.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
                btnAnswer1.setOnClickListener {
                    inclFormData.apply {
                        if (etFullName.text.toString().isNotEmpty() && etPhoneNumber.text.toString()
                                .isNotEmpty()
                        ) {
                            pbLoading.showLoading(true)
                            sendDataToFirebase()
                        } else {
                            showDialog(
                                context = requireContext(),
                                title = "Form Data Belum Lengkap",
                                message = "Harap periksa kembali data yang belum diisi dan tidak sesuai",
                                negativeButtonText = "Periksa Kembali"
                            )
                        }
                    }
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

    private fun setupFirebase() {
        firebaseDatabase = Firebase.database
        databaseRef = firebaseDatabase.reference.child("user")
    }

    private fun sendDataToFirebase() {
        binding?.inclFormData?.apply {

            val rbValue = view?.findViewById(rgSex.checkedRadioButtonId) as RadioButton

            val data = IdentityRequest(
                fullName = etFullName.text.toString().trim(),
                gender = rbValue.text.toString(),
                phoneNumber = etPhoneNumber.text.toString().trim(),
                rangeAge = userViewModel.listFirstAnswers[0],
                screeningType = userViewModel.listFirstAnswers[1],
                subjectScreening = userViewModel.listFirstAnswers[2]
            )

            val uid = if (mainViewModel.isScreenBack) userViewModel.userUid else getRandomUid()
            databaseRef.child(uid).child("form_data").setValue(data) { error, _ ->
                binding?.pbLoading?.showLoading(false)
                if (error != null) {
                    requireContext().showToast("Failed Send Data")
                } else {
                    userViewModel.addUserData(data)
                    userViewModel.addUserUID(uid)
                    setupNextPage()
                }
            }
        }
    }

    private fun getRandomUid(): String {
        val uidRandom = Random().nextInt(10000000)
        return uidRandom.toString()
    }

    private fun setupNextPage() {
        binding?.apply {
            val action =
                PersonalDataFragmentDirections.actionScreeningIdentityFragmentToScreeningAdvanceFragment()
            inclLayoutAnswer.btnAnswer1.findNavController().navigate(action)
        }
    }
}