package com.novel.read.ui.read.config

import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import com.novel.read.App
import com.novel.read.R
import com.novel.read.base.BaseDialogFragment
import com.novel.read.constant.PreferKey
import com.novel.read.help.AppConfig
import com.novel.read.ui.read.ReadBookActivity
import com.novel.read.utils.ext.*
import kotlinx.android.synthetic.main.dialog_read_adjust.*

class ReadAdjustDialog :BaseDialogFragment() {

    val callBack get() = activity as? ReadBookActivity
    val showBrightnessView get() = context?.getPrefBoolean(PreferKey.showBrightnessView, true)

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
        return inflater.inflate(R.layout.dialog_read_adjust, container)
    }


    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        initData()
        initViewEvent()
    }

    private fun initView() {
    }

    private fun initData() {
        scb_follow_sys.isChecked = brightnessAuto()
        hpb_light.progress = requireContext().getPrefInt("brightness", 100)
        upBrightnessState()

        swt_dark.isChecked=AppConfig.isNightTheme

        swt_dark.setOnCheckedChangeListener { compoundButton, b ->
            AppConfig.isNightTheme = b
            App.INSTANCE.applyDayNight()
        }
    }

    private fun initViewEvent() {
        //亮度调节
        scb_follow_sys.setOnClickListener {
            if (scb_follow_sys.isChecked) {
                scb_follow_sys.setChecked(checked = false, animate = true)
            } else {
                scb_follow_sys.setChecked(checked = true, animate = true)
            }
            context?.putPrefBoolean("brightnessAuto", !brightnessAuto())
            upBrightnessState()
        }

        //亮度调节
        hpb_light.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setScreenBrightness(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                context?.putPrefInt("brightness", hpb_light.progress)
            }

        })
    }

    private fun upBrightnessState() {
        hpb_light.isEnabled = !brightnessAuto()
        context?.getPrefInt("brightness", 100)?.let { setScreenBrightness(it) }
    }


    /**
     * 设置屏幕亮度
     */
    private fun setScreenBrightness(value: Int) {
        var brightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        if (!brightnessAuto()) {
            brightness = value.toFloat()
            if (brightness < 1f) brightness = 1f
            brightness /= 255f
        }
        val params = activity?.window?.attributes
        params?.screenBrightness = brightness
        activity?.window?.attributes = params
    }

    private fun brightnessAuto(): Boolean {
        return context?.getPrefBoolean("brightnessAuto", true)!! || !showBrightnessView!!
    }
}