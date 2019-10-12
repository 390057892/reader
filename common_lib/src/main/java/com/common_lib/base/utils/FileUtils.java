package com.common_lib.base.utils;

import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by alex on 16/7/2.
 */
public class FileUtils {
    private List<File> myFile;

    //get the size of picassio picture cache file size
    public long getFileDir(String filePath) {
        long mySpace = 0;
        try {
            File f = new File(filePath);
            File[] files = f.listFiles();// 列出所有文件
            // 将所有文件存入list中
            if (files != null) {
                int count = files.length;// 文件个数
                myFile = new ArrayList<>();
                for (File file : files) {
                    if ("KuKu".contains(file.getName())) {
                        mySpace += file.length();
                        myFile.add(file);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return mySpace;
        }
    }

    //clear picassio cache file
    public boolean clearCatch() {
        boolean res = false;
        if (myFile == null || myFile.size() == 0) {
            return true;
        } else {
            for (File f : myFile) {
                f.delete();
            }
        }
        return true;
    }


    public static Uri getOutputMediaFileUri(String fileName) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "KuKu");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return Uri.fromFile(mediaFile);
    }

    public static File getOutPutMediaFile(String fileName) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "KuKu");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public static String getOutputMediaFileName() {
        long time = System.currentTimeMillis() / 1000;
        Random random = new Random();
        int number = random.nextInt(10000) % (10000 - 1000 + 1) + 1000;
        return time + "" + number + ".jpg";
    }

    public static String getOutputMediaFileNameWithoutFormat() {
        long time = System.currentTimeMillis() / 1000;
        Random random = new Random();
        int number = random.nextInt(10000) % (10000 - 1000 + 1) + 1000;
        return time + "" + number;
    }


    //This is the method to get taking picture out put url
    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "KuKu");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        long time = System.currentTimeMillis() / 1000;
        Random random = new Random();
        int number = random.nextInt(10000) % (10000 - 1000 + 1) + 1000;
        String timeStamp = time + "" + number;
        return new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");
    }

    //This is the method to get taking picture out put url
    public static File getOutputMediaFile(String name) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "KuKu");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return new File(mediaStorageDir.getPath() + File.separator + name + ".jpg");
    }
}
