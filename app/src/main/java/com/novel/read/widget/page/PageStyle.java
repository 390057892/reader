package com.novel.read.widget.page;


import androidx.annotation.ColorRes;

import com.novel.read.R;

/**
 * Created by zlj
 * 页面的展示风格。
 */
public enum PageStyle {
    BG_0(R.color.read_font_one, R.color.read_bg_one),
    BG_1(R.color.read_font_two, R.color.read_bg_two),
//    BG_2(R.color.nb_read_font_3, R.color.nb_read_bg_3),
    BG_3(R.color.read_font_four, R.color.read_bg_four),
    BG_4(R.color.read_font_five, R.color.read_bg_five),
    NIGHT(R.color.read_font_night, R.color.read_bg_night),;

    private int fontColor;
    private int bgColor;

    PageStyle(@ColorRes int fontColor, @ColorRes int bgColor) {
        this.fontColor = fontColor;
        this.bgColor = bgColor;
    }

    public int getFontColor() {
        return fontColor;
    }

    public int getBgColor() {
        return bgColor;
    }
}
