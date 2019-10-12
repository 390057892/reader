package com.common_lib.base.picture;

import android.os.AsyncTask;

/**
 * Created by alex on 2016/11/28.
 */

public class TransformSurfaceImageTask extends AsyncTask<Void,Void,byte[]> {
    private String path;
    private int fixedDegree;

    public TransformSurfaceImageTask(String path, int fixedDegree){
        this.path=path;
        this.fixedDegree=fixedDegree;
    }

    @Override
    protected byte[] doInBackground(Void... params) {
        return PictureUtil.bitmapToByte(path,fixedDegree,false);
    }


    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
    }
}