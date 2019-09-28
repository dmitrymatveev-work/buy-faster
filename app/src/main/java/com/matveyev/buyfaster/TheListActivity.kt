package com.matveyev.buyfaster

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.matveyev.buyfaster.databinding.ActivityTheListBinding
import kotlinx.android.synthetic.main.activity_the_list.*
import java.lang.Exception
import com.google.firebase.analytics.FirebaseAnalytics


class TheListActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var viewModel: TheListViewModel

    private val viberPackageName = "com.viber.voip"
    private val skypePackageName = "com.skype.raider"
    private val whatsappPackageName = "com.whatsapp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        setContentView(R.layout.activity_the_list)

        val binding: ActivityTheListBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_the_list
        )
        viewModel = ViewModelProviders.of(this).get(TheListViewModel::class.java)
        val app = this.application as BuyFasterApp
        viewModel.theListRepo = app.theListRepository.value
        viewModel.depsRepo = app.depsRepository.value
        viewModel.refresh()
        binding.viewModel = viewModel

        try {
            val icon = applicationContext.packageManager.getApplicationIcon(viberPackageName)
            viberButton.setImageDrawable(icon)
        } catch (e: Exception) {
            viberButton.visibility = View.GONE
        }

        try {
            val icon = applicationContext.packageManager.getApplicationIcon(skypePackageName)
            skypeButton.setImageDrawable(icon)
        } catch (e: Exception) {
            skypeButton.visibility = View.GONE
        }

        try {
            val icon = applicationContext.packageManager.getApplicationIcon(whatsappPackageName)
            whatsappButton.setImageDrawable(icon)
        } catch (e: Exception) {
            whatsappButton.visibility = View.GONE
        }

        try {
            val smsPackageName = "com.android.mms"
            val icon = applicationContext.packageManager.getApplicationIcon(smsPackageName)
            smsButton.setImageDrawable(icon)
        } catch (e: Exception) {
            try{
                val smsPackageName = getSmsPackageName()
                val icon = applicationContext.packageManager.getApplicationIcon(smsPackageName)
                smsButton.setImageDrawable(icon)
            } catch (e: Exception) {
                smsButton.visibility = View.GONE
            }
        }

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
    }

    private fun getSmsPackageName() : String {
        val defApp = Settings.Secure.getString(contentResolver, "sms_default_application")
        val pm = applicationContext.packageManager
        val iIntent = pm.getLaunchIntentForPackage(defApp)
        val mInfo = pm.resolveActivity(iIntent, 0)
        return mInfo.activityInfo.packageName
    }

    fun addSubject(view: View) {
        val addSubjDialog = AddSubjectDialogFragment(viewModel.departmentsAutoComplete)
        addSubjDialog.positive = DialogInterface.OnClickListener { _, _ -> viewModel.addSubjectAsync(addSubjDialog.viewModel.subject) }
        addSubjDialog.show(supportFragmentManager, "addSubject")
    }

    fun viber(view: View) {
        send(viberPackageName)
    }

    fun skype(view: View) {
        send(skypePackageName)
    }

    fun whatsapp(view: View) {
        send(whatsappPackageName)
    }

    private fun send(packageName: String) {
        if(!isPackageInstalled(packageName, applicationContext.packageManager)) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            return
        }
        val content = viewModel.getMessageContent()
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.setPackage(packageName)
        sendIntent.putExtra(Intent.EXTRA_TEXT, content)
        startActivity(sendIntent)
        hideSendButtons()
    }

    fun sms(view: View) {
        val content = viewModel.getMessageContent()
        val sendIntent = Intent(Intent.ACTION_VIEW)
        sendIntent.type = "vnd.android-dir/mms-sms"
        sendIntent.putExtra("sms_body", content)
        startActivity(sendIntent)
        hideSendButtons()
    }

    private fun hideSendButtons() {
        sendButtons.visibility = View.GONE
        showSendButtons.visibility = View.VISIBLE
    }

    fun send(view: View) {
        sendButtons.visibility = View.VISIBLE
        showSendButtons.visibility = View.GONE
    }

    private fun isPackageInstalled(name: String, packageManager: PackageManager) : Boolean {
        return try {
            packageManager.getApplicationInfo(name, 0).enabled
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
