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
import kotlinx.android.synthetic.fdroid.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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
                Uri.parse("https://google.com")
            )
            startActivity(intent)
        }


        llTermsAndConditions.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://google.com")
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
                    i.putExtra(Intent.EXTRA_EMAIL, arrayOf("elitetranslatorapps@gmail.com"))
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
        val p = "com.guru.translate.translator.translation.learn.language"
        val uri: Uri = Uri.parse("market://details?id=$p")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
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

}