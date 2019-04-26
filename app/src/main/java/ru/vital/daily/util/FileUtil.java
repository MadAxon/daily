package ru.vital.daily.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public final class FileUtil {

    public static String getMimeType(@Nullable Context context, Uri uri) {
        String mimeType = null;
        if (context != null && uri.getScheme() != null) {
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
            if (type.contains("image"))
                return "image";
            if (type.contains("video"))
                return "video";
            if (type.contains("audio"))
                return "audio";
        }
        return "";
    }

    @Nullable
    public static File createTempFile(@Nullable Context context, String prefix, String suffix) {
        File file = null;
        if (context != null) {
            File filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

    public static boolean copyImageFile(@Nullable Context context, Uri src, @Nullable File dest) {
        //noinspection ConstantConditions
        if (context != null && dest != null)
            try (
                    InputStream inputStream = context.getContentResolver().openInputStream(src);
                    BufferedInputStream source = new BufferedInputStream(inputStream);
                    FileOutputStream destination = new FileOutputStream(dest);
            ) {
                //noinspection ConstantConditions
                byte[] buffer = new byte[1024 * 1024];
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

}
