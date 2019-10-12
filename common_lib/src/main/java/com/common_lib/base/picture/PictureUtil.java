package com.common_lib.base.picture;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Base64;

import com.common_lib.base.BaseConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by alex on 2016/3/16.
 */
public class PictureUtil {
    /**
     * convert bitmap to right size and send it to service
     *
     * @param filePath ; square
     * @return byte[]
     */
    public static byte[] bitmapToByte(String filePath, boolean square) {
        Bitmap bm;
        byte[] b=null;
        try {
            if (square) {
                bm = getSmallBitmap(filePath, 1200f, 1200f, filePath,0);
            } else {
                bm = getSmallBitmap(filePath, 2400f, 2400f, filePath,0);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            //这个视情况而定吧
            bm.recycle();
            b = baos.toByteArray();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * convert bitmap to right size and send it to service
     *
     * @param filePath ; square
     * @return byte[]
     */
    public static byte[] bitmapToByte(String filePath, int fixedDegree, boolean square) {
        Bitmap bm;
        byte[] b=null;
        try {
            if (square) {
                bm = getSmallBitmap(filePath, 1200f, 1200f, filePath,fixedDegree);
            } else {
                bm = getSmallBitmap(filePath, 2400f, 2400f, filePath,fixedDegree);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            //这个视情况而定吧
            bm.recycle();
            b = baos.toByteArray();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    public static byte[] bitmapToByte(Bitmap bitmap, boolean needRecycle) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        if (needRecycle) {
            bitmap.recycle();
        }
        byte[] b = baos.toByteArray();
        try {
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    public static byte[] logoBitmapToByte(Bitmap bitmap, boolean needRecycle) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (needRecycle) {
            bitmap.recycle();
        }
        byte[] b = baos.toByteArray();
        try {
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @param srcPath
     * @return
     */
    public static Bitmap getSmallBitmap(String srcPath, float maxHeight, float maxWidth, String filePath, int fixedDegree) throws IOException {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
        // Calculate inSampleSize
        newOpts.inSampleSize = computeSampleSize(newOpts, maxWidth, maxHeight);
        // Decode bitmap with inSampleSize set
        newOpts.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        // bitmap = ThumbnailUtils.extractThumbnail(bitmap, 480, 800,
        // ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        Bitmap bitmap1 = compressImage(bitmap);
        bitmap.recycle();
        return rotateBitMap(bitmap1, filePath,fixedDegree);// 压缩好比例大小后再进行质量压缩

    }

    private static Bitmap rotateBitMap(Bitmap bp, String filePath, int fixedDegree) throws IOException {
        ExifInterface exif = new ExifInterface(filePath);
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)+fixedDegree;
        int rotationInDegrees = exifToDegrees(rotation)+fixedDegree;
        Matrix matrix = new Matrix();
        Bitmap nowBp=null;
        if (rotation != 0f) {
            matrix.preRotate(rotationInDegrees);
            nowBp = Bitmap.createBitmap(bp, 0, 0, bp.getWidth(),
                    bp.getHeight(), matrix, true);
            if (bp.isRecycled()) {
                bp.recycle();
            }
        }
        if (nowBp!=null){
            return nowBp;
        }else{
            return bp;
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 1024 * 5) { // 循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            options -= 10;// 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /*
     * 这个方法可以把base64转换的字符串转换成bitmap
     */
    public static Bitmap StringToBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static File getStorageFile() {
        //		File file = new File(Environment.getExternalStoragePublicDirectory(
        //				Environment.DIRECTORY_PICTURES), "JustBig/"+TimeUtils.GetTodayTime());
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), BaseConstants.Picture_Folder);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static String md5HashBytes(byte[] bytes) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(bytes);
        byte[] hashedBytes = m.digest();

        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < hashedBytes.length; i++) {
            stringBuffer.append(Integer.toString((hashedBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    public static int computeSampleSize(BitmapFactory.Options options, float reqWidth, float reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            double scaleHeight = Math.ceil(height / reqHeight);
            double scaleWidth = Math.ceil(width / reqWidth);
            if (scaleHeight > scaleWidth) {
                inSampleSize = (int) scaleHeight + 1;
            } else if (scaleHeight < scaleWidth) {
                inSampleSize = (int) scaleWidth + 1;
            } else {
                inSampleSize = (int) scaleHeight + 1;
            }
        }
        return inSampleSize;
    }

    public static boolean isSquareImage(String path) {
        boolean res = false;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile((path), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        if (imageHeight == imageWidth) {
            res = true;
        }
        return res;
    }

    public static boolean isSquareImage(Bitmap bitmap) {
        boolean res = false;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width == height) {
            res = true;
        }
        return res;
    }

    public static int getSquareCropDimensionForBitmap(Bitmap bitmap) {
        int dimension;
        //If the bitmap is wider than it is tall
        //use the height as the square crop dimension
        if (bitmap.getWidth() >= bitmap.getHeight()) {
            dimension = bitmap.getHeight();
        }
        //If the bitmap is taller than it is wide
        //use the width as the square crop dimension
        else {
            dimension = bitmap.getWidth();
        }
        return dimension;
    }

}
