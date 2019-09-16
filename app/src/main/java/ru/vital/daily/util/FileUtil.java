package ru.vital.daily.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ru.vital.daily.enums.FileType;

public final class FileUtil {

    public static int getAudioDuration(Context context, Uri uri) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Integer.parseInt(durationStr);
    }

    public static boolean exists(String url) {
        /*if (URLUtil.isNetworkUrl(url))
            return false;
        else return new File(url).exists();*/
        return true;
    }

    public static String getMimeType(@Nullable Context context, Uri uri) {
        String mimeType = null;
        if (context != null && uri != null && uri.getScheme() != null) {
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                ContentResolver cr = context.getContentResolver();
                mimeType = cr.getType(uri);
            } else {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                        .toString());
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        fileExtension.toLowerCase());
            }
        }
        return mimeType;
    }

    public static String getFileType(String type) {
        if (type != null) {
            if (type.contains(FileType.image.name()))
                return FileType.image.name();
            if (type.contains(FileType.video.name()))
                return FileType.video.name();
            if (type.contains(FileType.audio.name()))
                return FileType.audio.name();
        }
        return "";
    }

    @Nullable
    public static File createTempFile(@Nullable Context context, String prefix, String suffix) {
        File file = null;
        if (context != null) {
            File filesDir = context.getExternalCacheDir();
            //File file = new File(filesDir, prefix + suffix);
            //if (file.exists()) file.delete();
            try {
                file = File.createTempFile(prefix, suffix, filesDir);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            file.deleteOnExit();
        }
        return file;
    }

    public static boolean copyFile(@Nullable Context context, Uri src, @Nullable File dest) {
        //noinspection ConstantConditions
        if (context != null && dest != null)
            try (
                    InputStream inputStream = context.getContentResolver().openInputStream(src);
                    BufferedInputStream source = new BufferedInputStream(inputStream);
                    FileOutputStream destination = new FileOutputStream(dest);
            ) {
                //noinspection ConstantConditions
                byte[] buffer = new byte[8912/*1024 * 1024*/];
                int read;
                while ((read = source.read(buffer)) != -1) {
                    destination.write(buffer, 0, read);
                }
                destination.close();
                source.close();
                inputStream.close();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        return false;
    }

    public static boolean writeToFile(InputStream inputStream, File file) {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        byte[] buffer = new byte[8192];
        int bytesRead;
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, bytesRead);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                outputStream.close();
                inputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }
        try {
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
