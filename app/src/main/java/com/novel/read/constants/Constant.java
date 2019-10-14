/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.novel.read.constants;


import android.graphics.Color;

import com.novel.read.utlis.FileUtils;

import java.io.File;

public class Constant {
    public static final String NIGHT = "NIGHT";
    public static final String Language = "Language";
    public static final String BookSort = "BookSort";
    public static final String Uid = "Uid";
    public static final String Sex = "Sex";
    public static final String Type = "Type";
    public static final String DateType = "DateType";
    public static final String InstallTime = "InstallTime";  //apk的安装时间
    public static final String InstallCount = "InstallCount";  //apk的打开次数
    public static final String AppraiseShow = "AppraiseShow";  //评价弹框是否提示过
    public static final String BookGuide = "BookGuide";  //图书引导是否提示过

    public static final String FORMAT_BOOK_DATE = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_TIME = "HH:mm";
    public static final int COMMENT_SIZE = 10;

    public static final String FeedBackEmail = "qdxs01@gmail.com";

    /**
     * 百度语音合成
     */
    //壳001  免费小说
    public static final String appId = "16826023";
    public static final String appKey = "vEuU5gIWGwq5hivdTAaKz0P9";
    public static final String secretKey = "FcWRYUIrOPyE7dy51qfYZmg8Y1ZyP1c4 ";


    /**
     * 腾讯bugly
     */
    public static final String buglyId = "aec152f916";//壳包001


    //BookCachePath (因为getCachePath引用了Context，所以必须是静态变量，不能够是静态常量)
    public static String BOOK_CACHE_PATH = FileUtils.getCachePath() + File.separator
            + "book_cache" + File.separator;

    public static final int[] tagColors = new int[]{
            Color.parseColor("#90C5F0"),
            Color.parseColor("#91CED5"),
            Color.parseColor("#F88F55"),
            Color.parseColor("#C0AFD0"),
            Color.parseColor("#E78F8F"),
            Color.parseColor("#67CCB7"),
            Color.parseColor("#F6BC7E"),
            Color.parseColor("#90C5F0"),
            Color.parseColor("#91CED5"),
    };

    //榜单类型
    public interface ListType {
        String Human = "1";
        String EditRecommend = "2";
        String HotSearch = "3";
    }

    public interface GenderType {
        String Man = "1";
        String Woman = "2";
    }

    public interface DateTyp {
        String General = "3";
        String Month = "2";
        String Week = "1";
    }

    public interface Bundle {
        String CategoryId = "category_id";
        String mTitle = "mTitle";
        String BookId = "BookId";
    }

    public interface HasImage {
        int has = 1;
    }

    public interface RequestCode {
        int REQUEST_READ = 1;
    }

    public interface ResultCode {
        String RESULT_IS_COLLECTED = "result_is_collected";
    }
}
