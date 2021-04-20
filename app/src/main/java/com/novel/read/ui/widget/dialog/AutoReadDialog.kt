package com.novel.read.ui.widget.dialog

import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import com.novel.read.R
import com.novel.read.base.BaseDialogFragment
import com.novel.read.databinding.DialogAutoReadBinding
import com.novel.read.help.ReadBookConfig
import com.novel.read.service.BaseReadAloudService
import com.novel.read.service.help.ReadAloud
import com.novel.read.utils.ColorUtils
import com.novel.read.utils.ext.bottomBackground
import com.novel.read.utils.ext.getPrimaryTextColor
import com.novel.read.utils.viewbindingdelegate.viewBinding
import org.jetbrains.anko.sdk27.listeners.onClick

class AutoReadDialog : BaseDialogFragment() {
    var callBack: CallBack? = null

    private val binding by viewBinding(DialogAutoReadBinding::bind)

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setBackgroundDrawableResource(R.color.background)
            it.decorView.setPadding(0, 0, 0, 0)
            val attr = it.attributes
            attr.dimAmount = 0.0f
            attr.gravity = Gravity.BOTTOM
            it.attributes = attr
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        callBack = activity as? CallBack
        return inflater.inflate(R.layout.dialog_auto_read, container)
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        val bg = requireContext().bottomBackground
        val isLight = ColorUtils.isColorLight(bg)
        val textColor = requireContext().getPrimaryTextColor(isLight)
        binding.root.setBackgroundColor(bg)
        binding.tvReadSpeedTitle.setTextColor(textColor)
        binding.tvReadSpeed.setTextColor(textColor)
        binding.ivCatalog.setColorFilter(textColor)
        binding.tvCatalog.setTextColor(textColor)
        binding.ivMainMenu.setColorFilter(textColor)
        binding.tvMainMenu.setTextColor(textColor)
        binding.ivAutoPageStop.setColorFilter(textColor)
        binding.tvAutoPageStop.setTextColor(textColor)
        binding.ivSetting.setColorFilter(textColor)
        binding.tvSetting.setTextColor(textColor)
        initOnChange()
        initData()
        initEvent()
    }

    private fun initData() {
        val speed = if (ReadBookConfig.autoReadSpeed < 10) 10 else ReadBookConfig.autoReadSpeed
        binding.tvReadSpeed.text = String.format("%ds", speed)
        binding.seekAutoRead.progress = speed
    }

    private fun initOnChange() {
        binding.seekAutoRead.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val speed = if (progress < 10) 10 else progress
                binding.tvReadSpeed.text = String.format("%ds", speed)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                ReadBookConfig.autoReadSpeed =
                    if (binding.seekAutoRead.progress < 10) 10 else binding.seekAutoRead.progress
                upTtsSpeechRate()
            }
        })
    }

    private fun initEvent() {
        binding.llMainMenu.onClick { callBack?.showMenuBar(); dismiss() }
        binding.llSetting.onClick {
            ReadAloudConfigDialog().show(childFragmentManager, "readAloudConfigDialog")
        }
        binding.llCatalog.onClick { callBack?.openChapterList() }
        binding.llAutoPageStop.onClick {
            callBack?.autoPageStop()
            dismiss()
        }
    }

    private fun upTtsSpeechRate() {
        ReadAloud.upTtsSpeechRate(requireContext())
        if (!BaseReadAloudService.pause) {
            ReadAloud.pause(requireContext())
            ReadAloud.resume(requireContext())
        }
    }

    interface CallBack {
        fun showMenuBar()
        fun openChapterList()
        fun autoPageStop()
    }
}