package com.novel.read.widget.page;

import java.util.ArrayList;
import java.util.List;

public class TxtPage {
    int position;
    String title;
    int titleLines; //当前 lines 中为 title 的行数。
    List<String> lines;
    private String pic;

    public int getPosition() {
        return position;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public int getTitleLines() {
        return titleLines;
    }

    public List<String> getLines() {
        if (lines == null) {
            return new ArrayList<>();
        }
//        for (int i = 0; i <lines.size() ; i++) {
//            lines.set(i, StringUtils.convertCC(lines.get(i), MyApp.getContext()));
//        }
        return lines;
    }


    public String getPic() {
        return pic == null ? "" : pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
