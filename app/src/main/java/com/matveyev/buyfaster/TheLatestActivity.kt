package com.matveyev.buyfaster

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.matveyev.buyfaster.databinding.ActivityTheLatestBinding

class TheLatestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_the_latest)

        val binding: ActivityTheLatestBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_the_latest
        )
        binding.viewModel = ViewModelProviders.of(this).get(TheLatestViewModel::class.java)
    }
}
