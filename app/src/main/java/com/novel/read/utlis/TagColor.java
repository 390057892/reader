
package com.novel.read.utlis;

import android.graphics.Color;


import com.novel.read.constants.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class TagColor {

  public int borderColor = Color.parseColor("#49C120");
  public int backgroundColor = Color.parseColor("#49C120");
  public int textColor = Color.WHITE;

  public static List<TagColor> getRandomColors(int size) {

    List<TagColor> list = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      TagColor color = new TagColor();
      color.borderColor = color.backgroundColor = Constant.tagColors[i % Constant.tagColors.length];
      list.add(color);
    }
    return list;
  }
}
