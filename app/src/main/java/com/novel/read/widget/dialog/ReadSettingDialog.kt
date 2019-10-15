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
import com.novel.read.widget.page.PageLoader
import com.novel.read.widget.page.PageMode
import com.novel.read.widget.page.PageStyle
import com.novel.read.widget.page.ReadSettingManager
import kotlinx.android.synthetic.main.layout_setting.*

class ReadSettingDialog(mActivity: Activity, private var mPageLoader: PageLoader) :
    Dialog(mActivity, R.style.ReadSettingDialog) {

    private var mPageStyleAdapter: PageStyleAdapter? = null
    private var mSettingManager: ReadSettingManager? = null

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
        mSettingManager = ReadSettingManager.getInstance()

        isBrightnessAuto = mSettingManager!!.isBrightnessAuto
        mBrightness = mSettingManager!!.brightness
        mTextSize = mSettingManager!!.textSize
        isTextDefault = mSettingManager!!.isDefaultTextSize
        mPageMode = mSettingManager!!.pageMode
        mPageStyle = mSettingManager!!.pageStyle
        convertType = mSettingManager!!.convertType
        if (convertType == 0) {
            tv_simple.isSelected = true
            tv_trans.isSelected = false
        } else {
            tv_simple.isSelected = false
            tv_trans.isSelected = true
        }
    }

    private fun initWidget() {
        setUpAdapter()
    }

    private fun setUpAdapter() {
        val drawables = arrayOf(
            getDrawable(R.color.nb_read_bg_1),
            getDrawable(R.color.nb_read_bg_2),
            getDrawable(R.color.nb_read_bg_4),
            getDrawable(R.color.nb_read_bg_5)
        )

        mPageStyleAdapter = PageStyleAdapter(listOf(*drawables), mPageLoader)
        read_setting_rv_bg.layoutManager = GridLayoutManager(context, 5)
        read_setting_rv_bg.adapter = mPageStyleAdapter

        mPageStyleAdapter!!.setPageStyleChecked(mPageStyle!!)

    }


    private fun getDrawable(drawRes: Int): Drawable? {
        return ContextCompat.getDrawable(context, drawRes)
    }

    private fun initClick() {

        //字体大小调节
        read_setting_tv_font_minus.setOnClickListener { v ->
            val fontSize = mSettingManager!!.textSize - 1
            if (fontSize < 0) {
                return@setOnClickListener
            }
            mPageLoader.setTextSize(fontSize)
        }

        read_setting_tv_font_plus.setOnClickListener { v ->
            val fontSize = mSettingManager!!.textSize + 1
            mPageLoader.setTextSize(fontSize)
        }

        tv_simple.setOnClickListener(View.OnClickListener {
            if (convertType == 0) {
                return@OnClickListener
            }
            tv_simple.isSelected = true
            tv_trans.isSelected = false
            mSettingManager!!.convertType = 0
            convertType = 0
            mPageLoader.setTextSize(mSettingManager!!.textSize)
        })

        tv_trans.setOnClickListener(View.OnClickListener {
            if (convertType == 1) {
                return@OnClickListener
            }
            tv_simple.isSelected = false
            tv_trans.isSelected = true
            mSettingManager!!.convertType = 1
            convertType = 1
            mPageLoader.setTextSize(mSettingManager!!.textSize)
        })

    }

    companion object {
        private val TAG = "ReadSettingDialog"
    }

}
