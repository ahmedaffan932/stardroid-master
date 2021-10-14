package com.google.android.stardroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.stardroid.clasess.Misc
import com.google.android.stardroid.interfaces.StartActivityCallBack
import kotlinx.android.synthetic.fdroid.activity_world_quiz.*

class WorldQuizActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_quiz)

        btnViewWorld.setOnClickListener{
            Misc.startActivity(this, Misc.isViewWorldIntEnabled, object : StartActivityCallBack {
                override fun onStart() {
                    val intent = Intent(this@WorldQuizActivity, AmChartsActivity::class.java)
                    startActivity(intent)
                }
            })
        }
//        btnPlayGame.setOnClickListener{
//            Misc.startActivity(this, Misc.isViewWorldIntEnabled, object : StartActivityCallBack {
//                override fun onStart() {
//                    val intent = Intent(this@WorldQuizActivity, AmChartsActivity::class.java)
//                    intent.putExtra(Misc.data, "sda")
//                    startActivity(intent)
//                }
//            })
//        }
    }
}