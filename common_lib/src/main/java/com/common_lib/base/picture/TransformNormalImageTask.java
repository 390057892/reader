package com.common_lib.base.picture;

import android.os.AsyncTask;

/**
 * Created by alex on 2016/11/28.
 */

public class TransformNormalImageTask extends AsyncTask<String,Void,byte[]> {
    @Override
    protected byte[] doInBackground(String... params) {
        return PictureUtil.bitmapToByte(params[0],false);
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
    }
}
