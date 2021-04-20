package com.novel.read.ui.read.page

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import com.novel.read.R
import com.novel.read.base.BaseActivity
import com.novel.read.constant.AppConst.timeFormat
import com.novel.read.data.read.TextPage
import com.novel.read.databinding.ViewBookPageBinding
import com.novel.read.help.ReadBookConfig
import com.novel.read.help.ReadTipConfig
import com.novel.read.service.help.ReadBook
import com.novel.read.ui.widget.BatteryView
import com.novel.read.utils.ext.*
import io.legado.app.ui.book.read.page.provider.ChapterProvider
import org.jetbrains.anko.topPadding
import java.util.*

class ContentView(context: Context) : FrameLayout(context) {
    private val binding = ViewBookPageBinding.inflate(LayoutInflater.from(context), this, true)
    private var battery = 100
    private var tvTitle: BatteryView? = null
    private var tvTime: BatteryView? = null
    private var tvBattery: BatteryView? = null
    private var tvPage: BatteryView? = null
    private var tvTotalProgress: BatteryView? = null
    private var tvPageAndTotal: BatteryView? = null
    private var tvBookName: BatteryView? = null

    val headerHeight: Int
        get() {
            val h1 = if (ReadBookConfig.hideStatusBar) 0 else context.statusBarHeight
            val h2 = if (binding.llHeader.isGone) 0 else binding.llHeader.height
            return h1 + h2
        }

    init {
        //设置背景防止切换背景时文字重叠
        setBackgroundColor(context.getCompatColor(R.color.background))
        upTipStyle()
        upStyle()
        binding.contentTextView.upView = {
            setProgress(it)
        }
    }

    fun upStyle() = with(binding){
        ReadBookConfig.apply {
            bvHeaderLeft.typeface = ChapterProvider.typeface
            tvHeaderLeft.typeface = ChapterProvider.typeface
            tvHeaderMiddle.typeface = ChapterProvider.typeface
            tvHeaderRight.typeface = ChapterProvider.typeface
            bvFooterLeft.typeface = ChapterProvider.typeface
            tvFooterLeft.typeface = ChapterProvider.typeface
            tvFooterMiddle.typeface = ChapterProvider.typeface
            tvFooterRight.typeface = ChapterProvider.typeface
            bvHeaderLeft.setColor(textColor)
            tvHeaderLeft.setColor(textColor)
            tvHeaderMiddle.setColor(textColor)
            tvHeaderRight.setColor(textColor)
            bvFooterLeft.setColor(textColor)
            tvFooterLeft.setColor(textColor)
            tvFooterMiddle.setColor(textColor)
            tvFooterRight.setColor(textColor)
            upStatusBar()
            binding.llHeader.setPadding(
                headerPaddingLeft.dp,
                headerPaddingTop.dp,
                headerPaddingRight.dp,
                headerPaddingBottom.dp
            )
            llFooter.setPadding(
                footerPaddingLeft.dp,
                footerPaddingTop.dp,
                footerPaddingRight.dp,
                footerPaddingBottom.dp
            )
            vwTopDivider.visible(showHeaderLine)
            vwBottomDivider.visible(showFooterLine)
            binding.contentTextView.upVisibleRect()
        }
        upTime()
        upBattery(battery)
    }

    /**
     * 显示状态栏时隐藏header
     */
    fun upStatusBar() = with(binding.vwStatusBar){
        setPadding(paddingLeft, context.statusBarHeight, paddingRight, paddingBottom)
        isGone =
            ReadBookConfig.hideStatusBar || (activity as? BaseActivity<*>)?.isInMultiWindow == true
    }

    fun upTipStyle()  = with(binding) {
        ReadTipConfig.apply {
            tvHeaderLeft.isInvisible = tipHeaderLeft != chapterTitle
            bvHeaderLeft.isInvisible = tipHeaderLeft == none || !tvHeaderLeft.isInvisible
            tvHeaderRight.isGone = tipHeaderRight == none
            tvHeaderMiddle.isGone = tipHeaderMiddle == none
            tvFooterLeft.isInvisible = tipFooterLeft != chapterTitle
            bvFooterLeft.isInvisible = tipFooterLeft == none || !tvFooterLeft.isInvisible
            tvFooterRight.isGone = tipFooterRight == none
            tvFooterMiddle.isGone = tipFooterMiddle == none
            binding.llHeader.isGone = hideHeader
            llFooter.isGone = hideFooter
        }
        tvTitle = when (ReadTipConfig.chapterTitle) {
            ReadTipConfig.tipHeaderLeft -> tvHeaderLeft
            ReadTipConfig.tipHeaderMiddle -> tvHeaderMiddle
            ReadTipConfig.tipHeaderRight -> tvHeaderRight
            ReadTipConfig.tipFooterLeft -> tvFooterLeft
            ReadTipConfig.tipFooterMiddle -> tvFooterMiddle
            ReadTipConfig.tipFooterRight -> tvFooterRight
            else -> null
        }
        tvTitle?.apply {
            isBattery = false
            textSize = 12f
        }
        tvTime = when (ReadTipConfig.time) {
            ReadTipConfig.tipHeaderLeft -> bvHeaderLeft
            ReadTipConfig.tipHeaderMiddle -> tvHeaderMiddle
            ReadTipConfig.tipHeaderRight -> tvHeaderRight
            ReadTipConfig.tipFooterLeft -> bvFooterLeft
            ReadTipConfig.tipFooterMiddle -> tvFooterMiddle
            ReadTipConfig.tipFooterRight -> tvFooterRight
            else -> null
        }
        tvTime?.apply {
            isBattery = false
            textSize = 12f
        }
        tvBattery = when (ReadTipConfig.battery) {
            ReadTipConfig.tipHeaderLeft -> bvHeaderLeft
            ReadTipConfig.tipHeaderMiddle -> tvHeaderMiddle
            ReadTipConfig.tipHeaderRight -> tvHeaderRight
            ReadTipConfig.tipFooterLeft -> bvFooterLeft
            ReadTipConfig.tipFooterMiddle -> tvFooterMiddle
            ReadTipConfig.tipFooterRight -> tvFooterRight
            else -> null
        }
        tvBattery?.apply {
            isBattery = true
            textSize = 10f
        }
        tvPage = when (ReadTipConfig.page) {
            ReadTipConfig.tipHeaderLeft -> bvHeaderLeft
            ReadTipConfig.tipHeaderMiddle -> tvHeaderMiddle
            ReadTipConfig.tipHeaderRight -> tvHeaderRight
            ReadTipConfig.tipFooterLeft -> bvFooterLeft
            ReadTipConfig.tipFooterMiddle -> tvFooterMiddle
            ReadTipConfig.tipFooterRight -> tvFooterRight
            else -> null
        }
        tvPage?.apply {
            isBattery = false
            textSize = 12f
        }
        tvTotalProgress = when (ReadTipConfig.totalProgress) {
            ReadTipConfig.tipHeaderLeft -> bvHeaderLeft
            ReadTipConfig.tipHeaderMiddle -> tvHeaderMiddle
            ReadTipConfig.tipHeaderRight -> tvHeaderRight
            ReadTipConfig.tipFooterLeft -> bvFooterLeft
            ReadTipConfig.tipFooterMiddle -> tvFooterMiddle
            ReadTipConfig.tipFooterRight -> tvFooterRight
            else -> null
        }
        tvTotalProgress?.apply {
            isBattery = false
            textSize = 12f
        }
        tvPageAndTotal = when (ReadTipConfig.pageAndTotal) {
            ReadTipConfig.tipHeaderLeft -> bvHeaderLeft
            ReadTipConfig.tipHeaderMiddle -> tvHeaderMiddle
            ReadTipConfig.tipHeaderRight -> tvHeaderRight
            ReadTipConfig.tipFooterLeft -> bvFooterLeft
            ReadTipConfig.tipFooterMiddle -> tvFooterMiddle
            ReadTipConfig.tipFooterRight -> tvFooterRight
            else -> null
        }
        tvPageAndTotal?.apply {
            isBattery = false
            textSize = 12f
        }
        tvBookName = when (ReadTipConfig.bookName) {
            ReadTipConfig.tipHeaderLeft -> bvHeaderLeft
            ReadTipConfig.tipHeaderMiddle -> tvHeaderMiddle
            ReadTipConfig.tipHeaderRight -> tvHeaderRight
            ReadTipConfig.tipFooterLeft -> bvFooterLeft
            ReadTipConfig.tipFooterMiddle -> tvFooterMiddle
            ReadTipConfig.tipFooterRight -> tvFooterRight
            else -> null
        }
        tvBookName?.apply {
            isBattery = false
            textSize = 12f
        }
    }

    fun setBg(bg: Drawable?) {
        binding.pagePanel.background = bg
    }

    fun upTime() {
        tvTime?.text = timeFormat.format(Date(System.currentTimeMillis()))
    }

    fun upBattery(battery: Int) {
        this.battery = battery
        tvBattery?.setBattery(battery)
    }

    fun setContent(textPage: TextPage, resetPageOffset: Boolean = true) {
        setProgress(textPage)
        if (resetPageOffset)
            resetPageOffset()
        binding.contentTextView.setContent(textPage)
    }

    fun resetPageOffset() {
        binding.contentTextView.resetPageOffset()
    }

    @SuppressLint("SetTextI18n")
    fun setProgress(textPage: TextPage) = textPage.apply {
        tvBookName?.text = ReadBook.book?.bookName
        tvTitle?.text = textPage.title
        tvPage?.text = "${index.plus(1)}/$pageSize"
        tvTotalProgress?.text = readProgress
        tvPageAndTotal?.text = "${index.plus(1)}/$pageSize  $readProgress"
    }

    fun onScroll(offset: Float) {
        binding.contentTextView.onScroll(offset)
    }

    fun upSelectAble(selectAble: Boolean) {
        binding.contentTextView.selectAble = selectAble
    }

    fun selectText(
        x: Float, y: Float,
        select: (relativePage: Int, lineIndex: Int, charIndex: Int) -> Unit
    ) {
        return binding.contentTextView.selectText(x, y - headerHeight, select)
    }

    fun selectStartMove(x: Float, y: Float) {
        binding.contentTextView.selectStartMove(x, y - headerHeight)
    }

    fun selectStartMoveIndex(relativePage: Int, lineIndex: Int, charIndex: Int) {
        binding.contentTextView.selectStartMoveIndex(relativePage, lineIndex, charIndex)
    }

    fun selectEndMove(x: Float, y: Float) {
        binding.contentTextView.selectEndMove(x, y - headerHeight)
    }

    fun selectEndMoveIndex(relativePage: Int, lineIndex: Int, charIndex: Int) {
        binding.contentTextView.selectEndMoveIndex(relativePage, lineIndex, charIndex)
    }

    fun cancelSelect() {
        binding.contentTextView.cancelSelect()
    }

    val selectedText: String get() = binding.contentTextView.selectedText

}