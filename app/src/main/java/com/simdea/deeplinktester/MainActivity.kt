package com.simdea.deeplinktester

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.simdea.deeplinktester.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val dataBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding.lifecycleOwner = this

        dataBinding.btnSubmit.setOnClickListener {
            launchBrowser(dataBinding.edtDL.text.toString())
        }

    }

    private fun launchBrowser(action: String) {
        val launchBrowser = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(action)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(launchBrowser)
    }

}
