package com.novel.read.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.text.TextUtils
import android.view.View
import com.allenliu.versionchecklib.v2.AllenVersionChecker
import com.allenliu.versionchecklib.v2.builder.UIData
import com.mango.mangolib.event.EventManager
import com.novel.read.R
import com.novel.read.base.NovelBaseActivity
import com.novel.read.constants.Constant
import com.novel.read.event.UpdateBookEvent
import com.novel.read.event.VersionEvent
import com.novel.read.http.AccountManager
import com.novel.read.model.protocol.VersionResp
import com.novel.read.utlis.CleanCacheUtils
import com.novel.read.utlis.LocalManageUtil
import com.novel.read.utlis.SpUtil
import com.novel.read.utlis.VersionUtil
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_setting.*

class NovelSettingActivity : NovelBaseActivity(), View.OnClickListener {

    private var resp: VersionResp? = null

    override val layoutId: Int get() = R.layout.activity_setting

    override fun initView() {
        EventManager.instance.registerSubscriber(this)
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        tv_language.text = resources.getStringArray(R.array.setting_dialog_language_choice)[SpUtil.getIntValue(Constant.Language, 1)]
        tv_version.text = "V" + VersionUtil.getPackageName(this)!!
        try {
            val cacheSize = CleanCacheUtils.getInstance().getTotalCacheSize(this@NovelSettingActivity)
            tv_cache_num.text = cacheSize
        } catch (e: Exception) {
            e.printStackTrace()
        }
        toolbar.setNavigationOnClickListener { finish() }
        AccountManager.getInstance().checkVersion(VersionUtil.getPackageCode(this))

        ll_choose_language.setOnClickListener(this)
        ll_clear_cache.setOnClickListener(this)
        tv_check.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ll_choose_language -> showLanguageDialog()
            R.id.ll_clear_cache -> {
                //默认不勾选清空书架列表，防手抖！！
                val selected = booleanArrayOf(true, false)
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.clear_cache))
                    .setCancelable(true)
                    .setMultiChoiceItems(
                        arrayOf(
                            getString(R.string.clear_cache),
                            getString(R.string.clear_book)
                        ), selected
                    ) { _, which, isChecked -> selected[which] = isChecked }
                    .setPositiveButton(getString(R.string.sure)) { dialog, _ ->
                        Thread {
                            CleanCacheUtils.getInstance()
                                .clearCache(selected[0], selected[1], this@NovelSettingActivity)
                            var cacheSize = ""
                            try {
                                cacheSize =
                                    CleanCacheUtils.getInstance()
                                        .getTotalCacheSize(this@NovelSettingActivity)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            val finalCacheSize = cacheSize
                            runOnUiThread {
                                EventManager.instance.postEvent(UpdateBookEvent())
                                tv_cache_num.text = finalCacheSize
                            }
                        }.start()
                        dialog.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, which -> dialog.dismiss() }
                    .create().show()
            }
            R.id.tv_check ->
                //版本大小不为空 去更新。
                updateApk(resp!!)
        }
    }

    private fun showLanguageDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.choose_language))
            .setSingleChoiceItems(
                resources.getStringArray(R.array.setting_dialog_language_choice),
                SpUtil.getIntValue(Constant.Language, 1)
            ) { dialog, which ->
                val language =
                    resources.getStringArray(R.array.setting_dialog_language_choice)[which]
                tv_language.text = language
                SpUtil.setIntValue(Constant.Language, which)
                dialog.dismiss()

                if (which == 0) {
                    selectLanguage(0)
                } else {
                    selectLanguage(1)
                }
            }
            .create().show()
    }

    private fun selectLanguage(select: Int) {
        LocalManageUtil.saveSelectLanguage(this, select)
        NovelMainActivity.reStart(this)
    }

    @Subscribe
    fun checkVersion(event: VersionEvent) {
        if (event.isFail) {

        } else {
            if (TextUtils.isEmpty(event.result!!.version.size)) {
                return
            }
            resp = event.result
            tv_check.visibility = View.VISIBLE

        }
    }

    private fun updateApk(resp: VersionResp) {
        val versionBean = resp.version
        val builder = AllenVersionChecker
            .getInstance()
            .downloadOnly(
                UIData.create()
                    .setTitle(getString(R.string.new_version, versionBean.version))
                    .setContent(versionBean.content)
                    .setDownloadUrl(versionBean.download)
            )
        builder.executeMission(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.instance.unregisterSubscriber(this)
    }
}
