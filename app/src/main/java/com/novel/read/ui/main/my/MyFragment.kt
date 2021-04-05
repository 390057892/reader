package com.novel.read.ui.main.my

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.novel.read.App
import com.novel.read.R
import com.novel.read.base.BaseFragment
import com.novel.read.ui.feedback.FeedBackActivity
import com.novel.read.ui.setting.SettingActivity
import com.novel.read.ui.widget.dialog.AppraiseDialog
import com.novel.read.utils.ext.startActivity
import com.novel.read.utils.ext.toast
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.android.synthetic.main.view_title_bar.*

class MyFragment : BaseFragment(R.layout.fragment_my) {


    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        setSupportToolbar(toolbar)
        tv_options.setOnClickListener {
            startActivity<FeedBackActivity>()
        }
        tv_setting.setOnClickListener {
            startActivity<SettingActivity>()
        }
        tv_appraise.setOnClickListener {
            val dialog = AppraiseDialog(requireActivity())
            dialog.appraiseDialog {
                goToMarket(App.INSTANCE, App.INSTANCE.packageName)
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun goToMarket(context: Context, packageName: String) {
        val uri = Uri.parse("market://details?id=$packageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        val googlePlay = "com.android.vending" //这里对应的是谷歌商店，跳转别的商店改成对应的即可
        goToMarket.setPackage(googlePlay) //这里对应的是谷歌商店，跳转别的商店改成对应的即可
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            if (goToMarket.resolveActivity(context.packageManager) != null) { //有浏览器
                startActivity(goToMarket)
            } else {
                toast(R.string.no_google)
            }
            e.printStackTrace()
        }
    }

}