package com.devdroid.sleepassistant.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.net.URLDecoder;

/**
 * Created by Gaolei on 2017/4/26.
 */

public class FileUtils {
    public static String getPath(Context context, Uri uri) {
        if(uri == null) return null;
        try {
            boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;//19
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) { // 【DocumentProvider】
                if (isExternalStorageDocument(uri)) { // 【ExternalStorageProvider】
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type))
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else if (isDownloadsDocument(uri)) { // 【DownloadsProvider】
                    String id = DocumentsContract.getDocumentId(uri);
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri);
                } else if (isMediaDocument(uri)) { // 【MediaProvider】
                    String scheme = uri.getScheme();
                    if (scheme.equals("content")) {
                        String docId = DocumentsContract.getDocumentId(uri);
                        String[] split = docId.split(":");
                        String type = split[0];
                        Uri contentUri = null;
                        if ("image".equals(type))
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        else if ("video".equals(type))
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        else if ("audio".equals(type))
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        return getDataColumn(context, contentUri);
                    } else {
                        return URLDecoder.decode(uri.getEncodedPath(), "UTF-8");
                    }
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) { // 【MediaStore (and general)】
                return getDataColumn(context, uri);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) { // 【File】
                return uri.getPath();
            }
        }catch (Exception e){
            e.printStackTrace();
            String path = uri.getPath();
            if(path.startsWith("/root")){
                return path.substring(5);
            }
        }
        return null;
    }
    /**
     * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other file-based ContentProviders.
     */
    private static String getDataColumn(Context context, Uri uri) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
