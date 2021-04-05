package com.novel.read.ui.read.page.delegate

import android.graphics.Canvas
import com.novel.read.ui.read.page.PageView

class NoAnimPageDelegate(pageView: PageView) : HorizontalPageDelegate(pageView) {

    override fun onAnimStart(animationSpeed: Int) {
        if (!isCancel) {
            pageView.fillPage(mDirection)
        }
        stopScroll()
    }

    override fun onDraw(canvas: Canvas) {
        // nothing
    }

    override fun onAnimStop() {
        // nothing
    }


}