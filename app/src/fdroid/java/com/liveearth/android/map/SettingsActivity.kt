package com.liveearth.android.map

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.liveearth.android.map.clasess.EmailUsDialogBox
import com.liveearth.android.map.clasess.Misc
import com.liveearth.android.map.clasess.RateUsDialog
import com.liveearth.android.map.interfaces.OnBackPressCallBack
import com.liveearth.android.map.interfaces.StartActivityCallBack
import kotlinx.android.synthetic.fdroid.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        tvUpgradeToPremium.setOnClickListener {
            Misc.startActivity(this, Misc.isProScreenIntEnabled, object : StartActivityCallBack{
                override fun onStart() {
                    val intent = Intent(this@SettingsActivity, ProScreenActivity::class.java)
                    intent.putExtra(Misc.data, Misc.data)
                    startActivity(intent)
                }
            })
        }

        llShareApp.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Have a look to this interesting application:- \n \n${Misc.appUrl}"
            )
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }

        llPrivacyPolicy.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://sites.google.com/view/liveearthmapxtreamapps/home")
            )
            startActivity(intent)
        }


        llMoreApps.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/developer?id=Xtream+Apps+Solutions")
            )
            startActivity(intent)
        }


        btnBackSettings.setOnClickListener {
            onBackPressed()
        }

        llFeedback.setOnClickListener {
            val objEmailUsDialog = EmailUsDialogBox(this)
            objEmailUsDialog.show()
            val window: Window = objEmailUsDialog.window!!
            window.setLayout(
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawableResource(R.color.nothing)
            objEmailUsDialog.findViewById<ConstraintLayout>(R.id.btnPublishFeedback)
            objEmailUsDialog.findViewById<ConstraintLayout>(R.id.btnPublishFeedback)
                .setOnClickListener {
                    val sub = objEmailUsDialog.findViewById<TextView>(R.id.etTopic).text.toString()
                    val i = Intent(Intent.ACTION_SEND)
                    i.type = "message/rfc822"
                    i.putExtra(Intent.EXTRA_EMAIL, arrayOf("xtreamappssolutions@gmail.com"))
                    i.putExtra(
                        Intent.EXTRA_TEXT,
                        objEmailUsDialog.findViewById<EditText>(R.id.etFeedbackBody).text
                    )
                    i.putExtra(Intent.EXTRA_SUBJECT, sub)
                    i.setPackage("com.google.android.gm")

                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."))
                        objEmailUsDialog.dismiss()
                    } catch (ex: ActivityNotFoundException) {
                        Toast.makeText(
                            this,
                            "Some error occurred in sending email.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            objEmailUsDialog.findViewById<ImageView>(R.id.btnClose).setOnClickListener {
                objEmailUsDialog.dismiss()
            }
        }


        llRateUs.setOnClickListener {
            val objRateUsDialog = RateUsDialog(this)
            objRateUsDialog.show()
            val window: Window = objRateUsDialog.window!!
            window.setLayout(
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawableResource(R.color.nothing)

            objRateUsDialog.findViewById<ImageView>(R.id.btnClose).setOnClickListener {
                objRateUsDialog.dismiss()
            }
            objRateUsDialog.findViewById<ImageView>(R.id.btnRateA).setOnClickListener {
                objRateUsDialog.dismiss()
                Toast.makeText(this, "Thanks.", Toast.LENGTH_SHORT).show()
            }
            objRateUsDialog.findViewById<ImageView>(R.id.btnRateB).setOnClickListener {
                objRateUsDialog.dismiss()
                Toast.makeText(this, "Thanks.", Toast.LENGTH_SHORT).show()
            }
            objRateUsDialog.findViewById<ImageView>(R.id.btnRateC).setOnClickListener {
                objRateUsDialog.dismiss()
                Toast.makeText(this, "Thanks.", Toast.LENGTH_SHORT).show()
            }
            objRateUsDialog.findViewById<ImageView>(R.id.btnRateD).setOnClickListener {
                rateUs()
            }
            objRateUsDialog.findViewById<ImageView>(R.id.btnRateE).setOnClickListener {
                rateUs()
            }
            objRateUsDialog.findViewById<ConstraintLayout>(R.id.btnRateUs).setOnClickListener {
                rateUs()
            }
        }
    }

    private fun rateUs() {

//        val manager = ReviewManagerFactory.create(this)
//        val request = manager.requestReviewFlow()
//        request.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                // We got the ReviewInfo object
//                val reviewInfo = task.result
//            } else {
//                // There was some problem, log or handle the error code.
//                val reviewErrorCode = task.exception
//                Log.e(Misc.logKey, task.exception.toString())
//            }
//        }

        val p = "com.liveearthmap.liveearthcam.streetview.gps.map.worldmap.satellite.app"
        val uri: Uri = Uri.parse("market://details?id=$p")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$p")
                )
            )
        }
    }

    override fun onBackPressed() {
        Misc.onBackPress(this, Misc.isSettingBackIntEnabled, object : OnBackPressCallBack {
            override fun onBackPress() {
                finish()
            }
        })
    }

}