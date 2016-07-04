package com.example.administrator.xiaominote.utils;

/**
 * Created by gac on 2016/5/25.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 图片压缩工具类
 *
 * @author Administrator
 *
 */
public class ImageCompress {

    public static final String CONTENT = "content";
    public static final String FILE = "file";


    public Bitmap getBitmap(String imagPath){
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        opts.inInputShareable = true;
        opts.inPurgeable = true;
        //do not compress
        opts.inSampleSize = 1;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(imagPath,opts);
    }

    //store bitmap into specified image path
    public void storeImage(Bitmap bitmap,String outPah) throws FileNotFoundException{
        FileOutputStream os = new FileOutputStream(outPah);
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,os);
    }


    //compress iamge by pixel,this will modify image width/height
    public Bitmap ratio(String imgPath,float pixelw,float pixelh){
        BitmapFactory.Options options = new BitmapFactory.Options();
        //
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath,options);
        int w = options.outWidth;
        int h = options.outHeight;
        //want scale size
        float hh = pixelh;
        float ww = pixelw;
        int scalesize = 1;// not scale
        if(w > h && w > ww){
            scalesize = (int)(options.outWidth/ww);
        }else if(w < h && h >hh ){
            scalesize = (int)(options.outHeight /hh);
        }
        if(scalesize <= 0) scalesize = 1;
        options.inSampleSize = scalesize;
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath,options);
        return  bitmap;
    }

    //compress image by size this will modify image width/height;
    //get thumbnail
    public Bitmap ratio(Bitmap image,float pixelw,float pixelh){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,os);
        if(os.toByteArray().length /1024 > 1024){//image more than 1m compress
            os.reset();
            image.compress(Bitmap.CompressFormat.JPEG,50,os);//compress 50%
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is,null,options);
        options.inJustDecodeBounds = false;
        int w = options.outWidth;
        int h = options.outHeight;
        float hh = pixelh;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelw;// 设置宽度为120f，可以明显看到图片缩小了
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (options.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (options.outHeight / hh);
        }
        if (be <= 0) be = 1;
        options.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, options);
        //压缩好比例大小后再进行质量压缩
//      return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }


    /**
     * Compress by quality,  and generate image to the path specified
     *
     * @param image
     * @param outPath
     * @param maxSize target will be compressed to be smaller than this size.(kb)
     * @throws IOException
     */
    public void compressAndGenImage(Bitmap image, String outPath, int maxSize) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 100;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        while ( os.toByteArray().length / 1024 > maxSize) {
            // Clean up os
            os.reset();
            // interval 10
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, os);
        }

        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(os.toByteArray());
        fos.flush();
        fos.close();
    }

    /**
     * Compress by quality,  and generate image to the path specified
     *
     * @param imgPath
     * @param outPath
     * @param maxSize target will be compressed to be smaller than this size.(kb)
     * @param needsDelete Whether delete original file after compress
     * @throws IOException
     */
    public void compressAndGenImage(String imgPath, String outPath, int maxSize, boolean needsDelete) throws IOException {
        compressAndGenImage(getBitmap(imgPath), outPath, maxSize);

        // Delete original file
        if (needsDelete) {
            File file = new File (imgPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * Ratio and generate thumb to the path specified
     *
     * @param image
     * @param outPath
     * @param pixelW target pixel of width
     * @param pixelH target pixel of height
     * @throws FileNotFoundException
     */
    public void ratioAndGenThumb(Bitmap image, String outPath, float pixelW, float pixelH) throws FileNotFoundException {
        Bitmap bitmap = ratio(image, pixelW, pixelH);
        storeImage( bitmap, outPath);
    }

    /**
     * Ratio and generate thumb to the path specified
     *
     * @param image
     * @param outPath
     * @param pixelW target pixel of width
     * @param pixelH target pixel of height
     * @param needsDelete Whether delete original file after compress
     * @throws FileNotFoundException
     */
    public void ratioAndGenThumb(String imgPath, String outPath, float pixelW, float pixelH, boolean needsDelete) throws FileNotFoundException {
        Bitmap bitmap = ratio(imgPath, pixelW, pixelH);
        storeImage( bitmap, outPath);

        // Delete original file
        if (needsDelete) {
            File file = new File (imgPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @TargetApi(19)
    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }


    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    /**
     * 图片压缩参数
     *
     * @author Administrator
     *
     */
    public static class CompressOptions {
        public static final int DEFAULT_WIDTH = 400;
        public static final int DEFAULT_HEIGHT = 800;

        public int maxWidth = DEFAULT_WIDTH;
        public int maxHeight = DEFAULT_HEIGHT;
        /**
         * 压缩后图片保存的文件
         */
        public File destFile;
        /**
         * 图片压缩格式,默认为jpg格式
         */
        public Bitmap.CompressFormat imgFormat = Bitmap.CompressFormat.JPEG;

        /**
         * 图片压缩比例 默认为30
         */
        public int quality = 30;
        public float widthDivideHeight ;
        public Uri uri;
    }


    //当从系统相册返回uri 解析图片返回空指针异常
    public Bitmap compressFromUri(Activity context,
                                  CompressOptions compressOptions) {

        // uri指向的文件路径
        String filePath = getFilePath(context,compressOptions.uri);//getImageAbsolutePath(context,compressOptions.uri);//getFilePath(context, compressOptions.uri);
        Log.e("gac","filePath:"+filePath);
        if (null == filePath) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//
        //仅仅返回options bitmap返回为空值
        Bitmap temp = BitmapFactory.decodeFile(filePath, options);


        int actualWidth = options.outWidth;
        int actualHeight = options.outHeight;
        Log.e("gac","outWidth"+options.outWidth+" outHeight:"+options.outHeight);
        int desiredWidth = getResizedDimension(compressOptions.maxWidth,
                compressOptions.maxHeight, actualWidth, actualHeight);
        int desiredHeight = getResizedDimension(compressOptions.maxHeight,
                compressOptions.maxWidth, actualHeight, actualWidth);
        Log.e("gac","desiredWidth:"+desiredWidth+" desiredHeight:"+desiredHeight);
        //图片不会加载入内存
        options.inJustDecodeBounds = false;
        options.inSampleSize = findBestSampleSize(actualWidth, actualHeight,
                desiredWidth, desiredHeight);//缩放比例
        Log.e("gac","InSampleSize:"+options.inSampleSize);
        Bitmap bitmap = null;
        Bitmap destBitmap = BitmapFactory.decodeFile(filePath, options);
        if(destBitmap == null){
            return null;
        }
        // If necessary, scale down to the maximal acceptable size.
        if (destBitmap.getWidth() > desiredWidth
                || destBitmap.getHeight() > desiredHeight) {
            bitmap = Bitmap.createScaledBitmap(destBitmap, desiredWidth,
                    desiredHeight, true);
            BitmapUtils.recyleBitmap(destBitmap);
        } else {
            bitmap = destBitmap;
        }

        // compress file if need
        if (null != compressOptions.destFile) {
            compressFile(compressOptions, bitmap);
        }
       //BitmapUtils.recyleBitmap(bitmap);
        return bitmap;
    }

    public static void compressPicture(String srcPath, String desPath) {
        FileOutputStream fos = null;
        BitmapFactory.Options op = new BitmapFactory.Options();

        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        op.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, op);
        op.inJustDecodeBounds = false;

        // 缩放图片的尺寸
        float w = op.outWidth;
        float h = op.outHeight;
        float hh = 1024f;//
        float ww = 1024f;//
        // 最长宽度或高度1024
        float be = 1.0f;
        if (w > h && w > ww) {
            be = (float) (w / ww);
        } else if (w < h && h > hh) {
            be = (float) (h / hh);
        }
        if (be <= 0) {
            be = 1.0f;
        }
        op.inSampleSize = (int) be;// 设置缩放比例,这个数字越大,图片大小越小.
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, op);
        int desWidth = (int) (w / be);
        int desHeight = (int) (h / be);
        bitmap = Bitmap.createScaledBitmap(bitmap, desWidth, desHeight, true);
        try {
            fos = new FileOutputStream(desPath);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Bitmap compressFromUri1(Activity context,
                                  CompressOptions compressOptions) {

        // uri指向的文件路径
        String filePath = getFilePath(context,compressOptions.uri);//getImageAbsolutePath(context,compressOptions.uri);//getFilePath(context, compressOptions.uri);
        Log.e("gac","filePath:"+filePath);
        if (null == filePath) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//
        //仅仅返回options bitmap返回为空值
        Bitmap temp = BitmapFactory.decodeFile(filePath, options);


        int actualWidth = options.outWidth;
        int actualHeight = options.outHeight;

        Log.e("gac","outWidth"+options.outWidth+" outHeight:"+options.outHeight);

        //图片不会加载入内存
        options.inJustDecodeBounds = false;
        options.inSampleSize = 1;
                //findBestSampleSize(actualWidth, actualHeight,
               // desiredWidth, desiredHeight);//缩放比例
        Log.e("gac","InSampleSize:"+options.inSampleSize);
        Bitmap bitmap = null;
        Bitmap destBitmap = BitmapFactory.decodeFile(filePath, options);
        if(destBitmap == null){
            return null;
        }
        // If necessary, scale down to the maximal acceptable size.

            bitmap = Bitmap.createScaledBitmap(destBitmap, actualWidth/8,
                    actualHeight/8, true);
            BitmapUtils.recyleBitmap(destBitmap);

        // compress file if need
        if (null != compressOptions.destFile) {
            compressFile(compressOptions, bitmap);
        }
        //BitmapUtils.recyleBitmap(bitmap);
        return bitmap;
    }
    /**
     * compress file from bitmap with compressOptions
     *
     * @param compressOptions
     * @param bitmap
     */
    private void compressFile(CompressOptions compressOptions, Bitmap bitmap) {
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(compressOptions.destFile);
        } catch (FileNotFoundException e) {
            Log.e("ImageCompress", e.getMessage());
        }

        bitmap.compress(compressOptions.imgFormat, compressOptions.quality,
                stream);
        try {
            if(stream != null){
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int findBestSampleSize(int actualWidth, int actualHeight,
                                          int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }

    private static int getResizedDimension(int maxPrimary, int maxSecondary,
                                           int actualPrimary, int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling
        // ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        Log.e("gac","ratio:"+ratio);
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    /**
     * 获取文件的路径
     *通过uri 获取文件路径
     * @param
     * @return
     */
    private String getFilePath(Context context, Uri uri) {

        String filePath = null;

        if (CONTENT.equalsIgnoreCase(uri.getScheme())) {

            Cursor cursor = context.getContentResolver().query(uri,
                    new String[] { MediaStore.Images.Media.DATA }, null, null, null);

            if (null == cursor) {
                return null;
            }

            try {
                if (cursor.moveToNext()) {
                    filePath = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                }
            } finally {
                cursor.close();
            }
        }

        // 从文件中选择
        if (FILE.equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }

        return filePath;
    }
}