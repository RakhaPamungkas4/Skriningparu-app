package com.rakul.skriningparu.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rakul.skriningparu.HomeActivity
import com.rakul.skriningparu.R
import com.rakul.skriningparu.databinding.ActivityResultScreeningBinding
import com.rakul.skriningparu.utils.const.ResultKey.ALMOST_CERTAINLY_NOT_POSITIVE
import com.rakul.skriningparu.utils.const.ResultKey.ALMOST_CERTAIN_POSITIVE
import com.rakul.skriningparu.utils.const.ResultKey.CERTAIN_POSITIVE
import com.rakul.skriningparu.utils.const.ResultKey.DEFINITELY_NOT_POSITIVE
import com.rakul.skriningparu.utils.const.ResultKey.DONT_KNOW_NOT_POSITIVE
import com.rakul.skriningparu.utils.const.ResultKey.MAYBE_POSITIVE
import com.rakul.skriningparu.utils.const.ResultKey.MOST_LIKELY_NOT_POSITIVE
import com.rakul.skriningparu.utils.const.ResultKey.MOST_LIKELY_POSITIVE
import com.rakul.skriningparu.utils.const.ResultKey.PROBABLY_NOT_POSITIVE
import com.rakul.skriningparu.utils.dialog.showDialog

class ResultScreeningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultScreeningBinding

    private var subTotalBobot = 0.0
    private var resultType = ""

    companion object {

        private const val TAG_SUBTOTAL_BOBOT = "TAG_SUBTOTAL_BOBOT"

        fun start(context: Context, subTotalBobot: Double) {
            context.startActivity(Intent(context, ResultScreeningActivity::class.java).apply {
                putExtra(TAG_SUBTOTAL_BOBOT, subTotalBobot)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultScreeningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subTotalBobot = intent.getDoubleExtra(TAG_SUBTOTAL_BOBOT, 0.0)

        binding.apply {

            // negative section
            if ((subTotalBobot >= -1.0) || (subTotalBobot <= -0.8)) {
                resultType = DEFINITELY_NOT_POSITIVE
                imgLabel.setBackgroundResource(R.drawable.img_result_negatif)
            } else if ((subTotalBobot > -0.8) && (subTotalBobot <= -0.6)) {
                resultType = ALMOST_CERTAINLY_NOT_POSITIVE
                imgLabel.setBackgroundResource(R.drawable.img_result_negatif)
            } else if ((subTotalBobot > -0.6) && (subTotalBobot <= -0.4)) {
                resultType = MOST_LIKELY_NOT_POSITIVE
                imgLabel.setBackgroundResource(R.drawable.img_result_negatif)
            } else if ((subTotalBobot > -0.4) && (subTotalBobot <= 0.2)) {
                resultType = PROBABLY_NOT_POSITIVE
                imgLabel.setBackgroundResource(R.drawable.img_result_negatif)
            } else if ((subTotalBobot > -0.2) && (subTotalBobot <= 0.2)) {
                resultType = DONT_KNOW_NOT_POSITIVE
                imgLabel.setBackgroundResource(R.drawable.img_result_negatif)
            }

            // positive section
            if ((subTotalBobot > 0.2) && (subTotalBobot < 0.6)) {
                resultType = MAYBE_POSITIVE
                imgLabel.setBackgroundResource(R.drawable.img_result_positive)
            } else if ((subTotalBobot >= 0.6) && (subTotalBobot < 0.8)) {
                resultType = MOST_LIKELY_POSITIVE
                imgLabel.setBackgroundResource(R.drawable.img_result_positive)
            } else if ((subTotalBobot >= 0.8) && (subTotalBobot < 1.0)) {
                resultType = ALMOST_CERTAIN_POSITIVE
                imgLabel.setBackgroundResource(R.drawable.img_result_positive)
            } else if ((subTotalBobot >= 1.0)) {
                resultType = CERTAIN_POSITIVE
                imgLabel.setBackgroundResource(R.drawable.img_result_positive)
            }

            if (subTotalBobot > -1.0 || subTotalBobot <= 2.0) {
                tvDescription.text = getString(R.string.label_good_news, resultType)
            } else {
                tvDescription.text = getString(R.string.label_bad_news, resultType)
            }

            btnFinish.setOnClickListener {
                showDialog(
                    context = this@ResultScreeningActivity,
                    title = getString(R.string.title_are_you_sure_exit),
                    message = getString(R.string.message_exit_result),
                    positiveButtonText = getString(R.string.action_yes),
                    negativeButtonText = getString(R.string.action_nope),
                    isShowPositiveButton = true
                ) { onBackPressed() }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this@ResultScreeningActivity, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        super.onBackPressed()
    }
}