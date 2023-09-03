package com.rakul.skriningparu

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rakul.skriningparu.data.model.response.ConsentResponse
import com.rakul.skriningparu.data.model.response.ScreeningResponse
import com.rakul.skriningparu.databinding.ActivityMainBinding
import com.rakul.skriningparu.ui.fragment.ConsentSheetFragment
import com.rakul.skriningparu.ui.fragment.PersonalDataFragment
import com.rakul.skriningparu.ui.fragment.ScreeningFragment
import com.rakul.skriningparu.ui.fragment.ScreeningScreenFragment
import com.rakul.skriningparu.ui.viewmodel.MainViewModel
import com.rakul.skriningparu.utils.const.FirebaseChildKey.ANSWER_KEY_CHILD
import com.rakul.skriningparu.utils.const.FirebaseChildKey.BOBOT_KEY_CHILD
import com.rakul.skriningparu.utils.const.FirebaseChildKey.CONSENT_PAGE_KEY_DB
import com.rakul.skriningparu.utils.const.FirebaseChildKey.DATA_KEY_PARENT
import com.rakul.skriningparu.utils.const.FirebaseChildKey.IMAGE_KEY_CHILD
import com.rakul.skriningparu.utils.const.FirebaseChildKey.SCREENING_PHASE_ONE_KEY_DB
import com.rakul.skriningparu.utils.const.FirebaseChildKey.SCREENING_PHASE_TWO_KEY_DB
import com.rakul.skriningparu.utils.const.FirebaseChildKey.TITLE_KEY_CHILD
import com.rakul.skriningparu.utils.dialog.showDialog

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFirebase()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        getDataFromFirebase()
    }

    private fun setupFirebase() {
        firebaseDatabase = Firebase.database
        databaseRef = firebaseDatabase.reference.child(DATA_KEY_PARENT)
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val consentPageData = snapshot.child(CONSENT_PAGE_KEY_DB)
                val firstPhaseScreeningData = snapshot.child(SCREENING_PHASE_ONE_KEY_DB)
                val secondPhaseScreeningData = snapshot.child(SCREENING_PHASE_TWO_KEY_DB)

                val consentData =
                    consentPageData.getValue(ConsentResponse::class.java) as ConsentResponse
                val firstScreeningData = mutableListOf<ScreeningResponse>()
                firstPhaseScreeningData.children.forEach {
                    val answers = mutableListOf<String>()
                    val title = it.child(TITLE_KEY_CHILD).value.toString()
                    it.child(ANSWER_KEY_CHILD).children.forEach { item ->
                        answers.add(item.value.toString())
                    }
                    firstScreeningData.add(
                        ScreeningResponse(
                            title = title,
                            answer = answers
                        )
                    )
                }

                val secondScreeningData = mutableListOf<ScreeningResponse>()
                secondPhaseScreeningData.children.forEach {
                    val answers = mutableListOf<String>()
                    val bobot = mutableListOf<Double>()
                    val title = it.child(TITLE_KEY_CHILD).value.toString()
                    val image = it.child(IMAGE_KEY_CHILD).value.toString()
                    it.child(BOBOT_KEY_CHILD).children.forEach { item ->
                        bobot.add(item.value.toString().toDouble())
                    }
                    it.child(ANSWER_KEY_CHILD).children.forEach { item ->
                        answers.add(item.value.toString())
                    }
                    secondScreeningData.add(
                        ScreeningResponse(
                            title = title,
                            image = image,
                            bobot = bobot,
                            answer = answers
                        )
                    )
                }

                mainViewModel.addConsentData(consentData)
                mainViewModel.addFirstScreeningData(firstScreeningData)
                mainViewModel.addSecondScreeningData(secondScreeningData)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Fail to get data.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        when (mainViewModel.screenName) {
            ConsentSheetFragment::class.java.name -> {
                Toast.makeText(this@MainActivity, "ConsentFragment", Toast.LENGTH_SHORT).show()
            }

            ScreeningFragment::class.java.name -> {
                showDialog(
                    context = this@MainActivity,
                    title = getString(R.string.title_are_you_sure_exit),
                    message = getString(R.string.label_do_you_want_cancel),
                    positiveButtonText = getString(R.string.action_sure),
                    negativeButtonText = getString(R.string.action_not_sure),
                    isShowPositiveButton = true
                ) {
                    super.onBackPressed()
                }
            }

            PersonalDataFragment::class.java.name -> {
                super.onBackPressed()
            }

            ScreeningScreenFragment::class.java.name -> {
                showDialog(
                    context = this@MainActivity,
                    title = getString(R.string.title_back_to_previous_page),
                    message = getString(R.string.label_please_finish_this_screening),
                    positiveButtonText = getString(R.string.action_sure),
                    negativeButtonText = getString(R.string.action_not_sure),
                    isShowPositiveButton = true
                ) {
                    mainViewModel.isScreenBack = true
                    super.onBackPressed()
                }
            }
        }
    }
}