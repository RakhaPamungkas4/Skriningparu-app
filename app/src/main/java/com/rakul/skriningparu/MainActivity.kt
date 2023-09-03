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
        databaseRef = firebaseDatabase.reference.child("data")
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val consentPageData = snapshot.child("consent_page")
                val firstPhaseScreeningData = snapshot.child("fase_skrining_1")
                val secondPhaseScreeningData = snapshot.child("fase_skrining_2")

                val consentData =
                    consentPageData.getValue(ConsentResponse::class.java) as ConsentResponse
                val firstScreeningData = mutableListOf<ScreeningResponse>()
                firstPhaseScreeningData.children.forEach {
                    val answers = mutableListOf<String>()
                    val title = it.child("title").value.toString()
                    it.child("answer").children.forEach { item ->
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
                    val title = it.child("title").value.toString()
                    val image = it.child("image").value.toString()
                    it.child("bobot").children.forEach { item ->
                        bobot.add(item.value.toString().toDouble())
                    }
                    it.child("answer").children.forEach { item ->
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
                    title = "Apakah anda yakin keluar?",
                    message = "Anda belum menyelesaikan test skrining, apakah anda yakin untuk membatalkannya?",
                    positiveButtonText = "Yakin",
                    negativeButtonText = "Tidak Yakin",
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
                    title = "Apakah anda yakin kembali ke halamaan sebelumnya?",
                    message = "Anda belum menyelesaikan test skrining, apakah anda yakin untuk memeriksa kembali data diri anda dan data pada skrining pertama?",
                    positiveButtonText = "Yakin",
                    negativeButtonText = "Tidak Yakin",
                    isShowPositiveButton = true
                ) {
                    mainViewModel.isScreenBack = true
                    super.onBackPressed()
                }
            }
        }
    }
}