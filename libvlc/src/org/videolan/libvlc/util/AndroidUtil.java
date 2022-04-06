/*****************************************************************************
 * AndroidUtil.java
 *****************************************************************************
 * Copyright © 2015 VLC authors, VideoLAN and VideoLabs
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package org.videolan.libvlc.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.widget.Toast;

import org.videolan.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

public class AndroidUtil {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss_S");
    public static final boolean isROrLater = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    public static final boolean isPOrLater = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    public static final boolean isOOrLater = isPOrLater || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    public static final boolean isNougatMR1OrLater = isOOrLater || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1;
    public static final boolean isNougatOrLater = isNougatMR1OrLater || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    public static final boolean isMarshMallowOrLater = isNougatOrLater || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    public static final boolean isLolliPopOrLater = isMarshMallowOrLater || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    public static final boolean isKitKatOrLater = isLolliPopOrLater || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    public static final boolean isJellyBeanMR2OrLater = isKitKatOrLater || android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;

    public static File UriToFile(Uri uri) {
        return new File(uri.getPath().replaceFirst("file://", ""));
    }

    /**
     * Quickly converts path to URIs, which are mandatory in libVLC.
     *
     * @param path The path to be converted.
     * @return A URI representation of path
     */
    public static Uri PathToUri(String path) {
        return Uri.fromFile(new File(path));
    }

    public static Uri LocationToUri(String location) {
        Uri uri = Uri.parse(location);
        if (uri.getScheme() == null)
            throw new IllegalArgumentException("location has no scheme");
        return uri;
    }

    public static Uri FileToUri(File file) {
        return Uri.fromFile(file);
    }

    public static Activity resolveActivity(Context context) {
        if (context instanceof Activity) return (Activity) context;
        if (context instanceof ContextWrapper)
            return resolveActivity(((ContextWrapper) context).getBaseContext());
        return null;

    }

    public static File saveBitmap(Context context, Bitmap bitmap, String parentDirPath) {
        // TODO do it Asynchronously
        if (parentDirPath != null) {
            OutputStream outStream = null;
            File file = new File(parentDirPath, "snapshot_" + dateFormat.format(System.currentTimeMillis()) + ".png");
            try {
                outStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
                Toast.makeText(context, context.getString(R.string.snapshot_taken), Toast.LENGTH_SHORT).show();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
                String errorMessage = e.getMessage();
                Toast.makeText(context, context.getString(errorMessage.toLowerCase().contains("no space") ? R.string.no_space_left : R.string.cant_take_photo), Toast.LENGTH_LONG).show();
            }
        }
        return null;
    }

    public static void showAlertDialog(Context context, int titleResId, int messageResId) {
        new AlertDialog.Builder(context)
                .setTitle(titleResId)
                .setMessage(messageResId)
                .setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) (dialog, which) -> dialog.dismiss())
                .show();
    }
}