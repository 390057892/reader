package com.novel.read.ui.setting

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import androidx.preference.Preference
import com.allenliu.versionchecklib.v2.AllenVersionChecker
import com.allenliu.versionchecklib.v2.builder.UIData
import com.novel.read.App
import com.novel.read.R
import com.novel.read.base.BasePreferenceFragment
import com.novel.read.constant.EventBus
import com.novel.read.constant.PreferKey
import com.novel.read.data.model.AppUpdateResp
import com.novel.read.help.AppConfig
import com.novel.read.help.BookHelp
import com.novel.read.lib.ATH
import com.novel.read.lib.dialogs.alert
import com.novel.read.lib.dialogs.noButton
import com.novel.read.lib.dialogs.okButton
import com.novel.read.network.repository.HomeRepository
import com.novel.read.utils.FileUtils
import com.novel.read.utils.LanguageUtils
import com.novel.read.utils.ext.*
import com.novel.read.help.coroutine.Coroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlin.coroutines.CoroutineContext


class OtherConfigFragment : BasePreferenceFragment(), CoroutineScope by MainScope(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val homeRepository by lazy { HomeRepository() }
    var appResp = MutableLiveData<AppUpdateResp>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_config_other)
        findPreference<Preference>("check_update")?.summary =
            "${getString(R.string.version)} ${App.versionName}"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        ATH.applyEdgeEffectColor(listView)
        appResp.observe(viewLifecycleOwner) {
            updateApk(it)
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            PreferKey.cleanCache -> clearCache()
            "check_update" -> {
                appUpdate()
            }
        }
        return super.onPreferenceTreeClick(preference)
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PreferKey.language -> {
                val lg = sharedPreferences?.all?.get("language")
                if (lg == "zh") {
                    AppConfig.chineseConverterType = 1
                } else if (lg == "tw") {
                    AppConfig.chineseConverterType = 2
                }
                LanguageUtils.setConfiguration(App.INSTANCE)
                postEvent(EventBus.RECREATE, "")
            }

        }
    }

    private fun appUpdate() {
        execute {
            appResp.value = homeRepository.appUpdate()
        }
    }

    private fun updateApk(resp: AppUpdateResp?) {
        val versionBean = resp?.appEdition
        if (versionBean != null) {
            val builder = AllenVersionChecker
                .getInstance()
                .downloadOnly(
                    UIData.create()
                        .setTitle(getString(R.string.new_version, versionBean.editionCode))
                        .setContent(versionBean.upgradeContent)
                        .setDownloadUrl(versionBean.fileUrl)
                )
            builder.executeMission(activity)
        }
    }

    fun <T> execute(
        scope: CoroutineScope = this,
        context: CoroutineContext = Dispatchers.IO,
        block: suspend CoroutineScope.() -> T
    ): Coroutine<T> {
        return Coroutine.async(scope, context) { block() }
    }

    private fun clearCache() {
        requireContext().alert(
            titleResource = R.string.clear_cache,
            messageResource = R.string.sure_del
        ) {
            okButton {
                BookHelp.clearCache()
                FileUtils.deleteFile(requireActivity().cacheDir.absolutePath)
                toast(R.string.clear_cache_success)
            }
            noButton()
        }.show().applyTint()
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

}