package com.novel.read.widget.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.novel.read.R
import com.novel.read.adapter.PageStyleAdapter
import com.novel.read.utlis.ScreenUtils
import com.novel.read.widget.page.PageLoader
import com.novel.read.widget.page.PageMode
import com.novel.read.widget.page.PageStyle
import com.novel.read.widget.page.ReadSettingManager
import kotlinx.android.synthetic.main.layout_setting.*

class ReadSettingDialog(mActivity: Activity, private var mPageLoader: PageLoader) :
    Dialog(mActivity, R.style.ReadSettingDialog) {

    private lateinit var mPageStyleAdapter: PageStyleAdapter
    private var mSettingManager: ReadSettingManager = ReadSettingManager.getInstance()

    private var mPageMode: PageMode? = null
    private var mPageStyle: PageStyle? = null

    private var mBrightness: Int = 0
    private var mTextSize: Int = 0

    private var isBrightnessAuto: Boolean = false
    private var isTextDefault: Boolean = false

    private var convertType: Int = 0

    init {
        setContentView(R.layout.layout_setting)
        setUpWindow()
        initData()
        initWidget()
        initClick()
        initPageMode()
    }

    //设置Dialog显示的位置
    private fun setUpWindow() {
        val window = window
        val lp = window!!.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.BOTTOM
        window.attributes = lp
    }

    private fun initData() {

        isBrightnessAuto = mSettingManager.isBrightnessAuto
        mBrightness = mSettingManager.brightness
        mTextSize = mSettingManager.textSize
        isTextDefault = mSettingManager.isDefaultTextSize
        mPageMode = mSettingManager.pageMode
        mPageStyle = mSettingManager.pageStyle
        convertType = mSettingManager.convertType
        if (convertType == 0) {
            tv_simple.isSelected = true
            tv_trans.isSelected = false
        } else {
            tv_simple.isSelected = false
            tv_trans.isSelected = true
        }
        tv_size.text = "$mTextSize"
    }

    private fun initWidget() {
        setUpAdapter()
    }

    private fun setUpAdapter() {
        val drawables = arrayOf(
            getDrawable(R.color.read_bg_one),
            getDrawable(R.color.read_bg_two),
            getDrawable(R.color.read_bg_four),
            getDrawable(R.color.read_bg_five)
        )

        mPageStyleAdapter = PageStyleAdapter(listOf(*drawables), mPageLoader)
        read_setting_rv_bg.layoutManager = GridLayoutManager(context, 4)
        read_setting_rv_bg.adapter = mPageStyleAdapter

        mPageStyleAdapter.setPageStyleChecked(mPageStyle!!)

    }

    private fun getDrawable(drawRes: Int): Drawable? {
        return ContextCompat.getDrawable(context, drawRes)
    }

    private fun initClick() {

        //字体大小调节
        read_setting_tv_font_minus.setOnClickListener {
            val fontSize = mSettingManager.textSize - 1
            if (fontSize < 0) {
                return@setOnClickListener
            }
            mPageLoader.setTextSize(fontSize)
            tv_size.text = "$fontSize"
        }

        read_setting_tv_font_plus.setOnClickListener {
            val fontSize = mSettingManager.textSize + 1
            mPageLoader.setTextSize(fontSize)
            tv_size.text = "$fontSize"
        }

        tv_font_default.setOnClickListener {
            val fontSize = ScreenUtils.spToPx(16)
            mSettingManager.textSize = fontSize
            mPageLoader.setTextSize(fontSize)
            tv_size.text = "$fontSize"
        }

        tv_simple.setOnClickListener(View.OnClickListener {
            if (convertType == 0) {
                return@OnClickListener
            }
            tv_simple.isSelected = true
            tv_trans.isSelected = false
            mSettingManager.convertType = 0
            convertType = 0
            mPageLoader.setTextSize(mSettingManager.textSize)
        })

        tv_trans.setOnClickListener(View.OnClickListener {
            if (convertType == 1) {
                return@OnClickListener
            }
            tv_simple.isSelected = false
            tv_trans.isSelected = true
            mSettingManager.convertType = 1
            convertType = 1
            mPageLoader.setTextSize(mSettingManager.textSize)
        })

        //Page Mode 切换
        read_setting_rg_page_mode.setOnCheckedChangeListener { group, checkedId ->
            val pageMode: PageMode = when (checkedId) {
                R.id.read_setting_rb_simulation -> PageMode.SIMULATION
                R.id.read_setting_rb_cover -> PageMode.COVER
                R.id.read_setting_rb_scroll -> PageMode.SCROLL
                R.id.read_setting_rb_none -> PageMode.NONE
                else -> PageMode.SIMULATION
            }
            mPageLoader.setPageMode(pageMode)
        }

    }

    private fun initPageMode() {
        when (mPageMode) {
            PageMode.SIMULATION -> read_setting_rb_simulation.isChecked = true
            PageMode.COVER -> read_setting_rb_cover.isChecked = true
            PageMode.NONE -> read_setting_rb_none.isChecked = true
            PageMode. SCROLL -> read_setting_rb_scroll.isChecked = true
            else -> {

            }
        }
    }

    companion object {
        private val TAG = "ReadSettingDialog"
    }

}
