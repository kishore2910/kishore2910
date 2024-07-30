package com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.databinding.FragmentSettingBinding
import com.powersoaps.distributorsales.ui.main.activity.support.HelpSupportActivity
import com.powersoaps.distributorsales.ui.main.activity.webview.WebviewActivity
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener

class SettingFragment : Fragment() {

    private val settingBinding by lazy { FragmentSettingBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return settingBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        settingBinding.Title.text = "About PowerSoaps"
//        val URL = "https://powersoaps.online/aboutus"
//        settingBinding.webview.webViewClient = WebViewClient()
//        settingBinding.webview.loadUrl(URL)
//        val webSettings = settingBinding.webview.settings
//        webSettings.javaScriptEnabled = true

//        settingBinding.logout.setOnDebounceListener {
//            val dialogBuilder = AlertDialog.Builder(requireContext())
//            dialogBuilder.setMessage("Are you sure you want to Logout?")
//                .setCancelable(false)
//                .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
//                    dialog.dismiss()
//                })
//                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
//                    dialog.dismiss()
//                    Preference(requireActivity()).onLogOut(requireActivity())
//                    startActivity(Intent(requireContext(), LoginActivity::class.java))
//                })
//                .show()
//        }
//
        settingBinding.about.setOnDebounceListener {
            startActivity(
                Intent(requireContext(), WebviewActivity::class.java)
                    .putExtra("Title", getString(R.string.aboutus))
                    .putExtra("URL", "https://powersoaps.online/aboutus")
            )
        }
        settingBinding.terms.setOnDebounceListener {
            startActivity(
                Intent(requireContext(), WebviewActivity::class.java)
                    .putExtra("Title", getString(R.string.terms_and_conditions))
                    .putExtra("URL", "https://powersoaps.online/terms-condition")
            )
        }
        settingBinding.privacy.setOnDebounceListener {
            startActivity(
                Intent(requireContext(), WebviewActivity::class.java)
                    .putExtra("Title", getString(R.string.privacy))
                    .putExtra("URL", "https://powersoaps.online/privacy_policy.html")
            )
        }
        settingBinding.support.setOnDebounceListener {
            startActivity(
                Intent(requireContext(), HelpSupportActivity::class.java)
            )
        }
    }

}