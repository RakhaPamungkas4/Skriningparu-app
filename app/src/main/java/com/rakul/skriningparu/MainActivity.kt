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
import com.rakul.skriningparu.ui.viewmodel.MainViewModel

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

//        setupActionBarWithNavController(navController)

        getDataFromFirebase()
    }

    private fun setupFirebase() {
        firebaseDatabase = Firebase.database
        databaseRef = firebaseDatabase.reference.child("data")
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataResponse1 = snapshot.child("consent_page")
                val dataResponse2 = snapshot.child("fase_skrining_1")
                val dataResponse3 = snapshot.child("fase_skrining_2")

                val consentData =
                    dataResponse1.getValue(ConsentResponse::class.java) as ConsentResponse
                val firstScreeningData = mutableListOf<ScreeningResponse>()
                dataResponse2.children.forEach {
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
                dataResponse3.children.forEach {
                    val answers = mutableListOf<String>()
                    val title = it.child("title").value.toString()
                    val image = it.child("image").value.toString()
                    it.child("answer").children.forEach { item ->
                        answers.add(item.value.toString())
                    }
                    secondScreeningData.add(
                        ScreeningResponse(
                            title = title,
                            image = image,
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
                Toast.makeText(this@MainActivity, "ScreeningFragment", Toast.LENGTH_SHORT).show()
            }

            PersonalDataFragment::class.java.name -> {
                Toast.makeText(this@MainActivity, "PersonalDataFragment", Toast.LENGTH_SHORT).show()
            }
        }
        super.onBackPressed()
    }
}