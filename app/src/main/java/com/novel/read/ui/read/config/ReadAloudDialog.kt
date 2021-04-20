package com.novel.read.ui.read.config

import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import com.novel.read.R
import com.novel.read.base.BaseDialogFragment
import com.novel.read.constant.EventBus
import com.novel.read.databinding.DialogReadAloudBinding
import com.novel.read.help.AppConfig
import com.novel.read.service.BaseReadAloudService
import com.novel.read.service.help.ReadAloud
import com.novel.read.service.help.ReadBook
import com.novel.read.ui.widget.dialog.ReadAloudConfigDialog
import com.novel.read.utils.ColorUtils
import com.novel.read.utils.ext.*
import com.novel.read.utils.viewbindingdelegate.viewBinding

class ReadAloudDialog : BaseDialogFragment() {
    var callBack: CallBack? = null
    private val binding by viewBinding(DialogReadAloudBinding::bind)
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
        return inflater.inflate(R.layout.dialog_read_aloud, container)
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        val bg = requireContext().bottomBackground
        val isLight = ColorUtils.isColorLight(bg)
        val textColor = requireContext().getPrimaryTextColor(isLight)
        with(binding) {
            rootView.setBackgroundColor(bg)
            tvPre.setTextColor(textColor)
            tvNext.setTextColor(textColor)
            ivPlayPrev.setColorFilter(textColor)
            ivPlayPause.setColorFilter(textColor)
            ivPlayNext.setColorFilter(textColor)
            ivStop.setColorFilter(textColor)
            ivTimer.setColorFilter(textColor)
            tvTimer.setTextColor(textColor)
            tvTtsSpeed.setTextColor(textColor)
            ivCatalog.setColorFilter(textColor)
            tvCatalog.setTextColor(textColor)
            ivMainMenu.setColorFilter(textColor)
            tvMainMenu.setTextColor(textColor)
            ivToBackstage.setColorFilter(textColor)
            tvToBackstage.setTextColor(textColor)
            ivSetting.setColorFilter(textColor)
            tvSetting.setTextColor(textColor)
            cbTtsFollowSys.setTextColor(textColor)
        }
        initOnChange()
        initData()
        initEvent()
    }

    private fun initData() = with(binding) {
        upPlayState()
        upTimerText(BaseReadAloudService.timeMinute)
        seekTimer.progress = BaseReadAloudService.timeMinute
        cbTtsFollowSys.isChecked = requireContext().getPrefBoolean("ttsFollowSys", true)
        seekTtsSpeechRate.isEnabled = !cbTtsFollowSys.isChecked
        seekTtsSpeechRate.progress = AppConfig.ttsSpeechRate
    }

    private fun initOnChange() = with(binding){
        cbTtsFollowSys.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                requireContext().putPrefBoolean("ttsFollowSys", isChecked)
                seekTtsSpeechRate.isEnabled = !isChecked
                upTtsSpeechRate()
            }
        }
        seekTtsSpeechRate.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                AppConfig.ttsSpeechRate = seekBar.progress
                upTtsSpeechRate()
            }
        })
        seekTimer.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                upTimerText(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                ReadAloud.setTimer(requireContext(), seekTimer.progress)
            }
        })
    }

    private fun initEvent() = with(binding) {
        llMainMenu.setOnClickListener {
            callBack?.showMenuBar()
            dismissAllowingStateLoss()
        }
        llSetting.setOnClickListener {
            ReadAloudConfigDialog().show(childFragmentManager, "readAloudConfigDialog")
        }
        tvPre.setOnClickListener { ReadBook.moveToPrevChapter(upContent = true, toLast = false) }
        tvNext.setOnClickListener { ReadBook.moveToNextChapter(true) }
        ivStop.setOnClickListener {
            ReadAloud.stop(requireContext())
            dismissAllowingStateLoss()
        }
        ivPlayPause.setOnClickListener { callBack?.onClickReadAloud() }
        ivPlayPrev.setOnClickListener { ReadAloud.prevParagraph(requireContext()) }
        ivPlayNext.setOnClickListener { ReadAloud.nextParagraph(requireContext()) }
        llCatalog.setOnClickListener { callBack?.openChapterList() }
        llToBackstage.setOnClickListener { callBack?.finish() }
    }

    private fun upPlayState() {
        if (!BaseReadAloudService.pause) {
            binding.ivPlayPause.setImageResource(R.drawable.ic_pause_24dp)
        } else {
            binding.ivPlayPause.setImageResource(R.drawable.ic_play_24dp)
        }
        val bg = requireContext().bottomBackground
        val isLight = ColorUtils.isColorLight(bg)
        val textColor = requireContext().getPrimaryTextColor(isLight)
        binding.ivPlayPause.setColorFilter(textColor)
    }

    private fun upTimerText(timeMinute: Int) {
        binding.tvTimer.text = requireContext().getString(R.string.timer_m, timeMinute)
    }

    private fun upTtsSpeechRate() {
        ReadAloud.upTtsSpeechRate(requireContext())
        if (!BaseReadAloudService.pause) {
            ReadAloud.pause(requireContext())
            ReadAloud.resume(requireContext())
        }
    }

    override fun observeLiveBus() {
        observeEvent<Int>(EventBus.ALOUD_STATE) { upPlayState() }
        observeEvent<Int>(EventBus.TTS_DS) { binding.seekTimer.progress = it }
    }

    interface CallBack {
        fun showMenuBar()
        fun openChapterList()
        fun onClickReadAloud()
        fun finish()
    }
}