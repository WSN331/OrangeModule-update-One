package com.qiuyi.cn.orangemoduleNew.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/5/7.
 */
public class ShareFile {

    public static void shareMultipleFiles(Context context, ArrayList<Uri> uris) {

        boolean multiple = uris.size() > 1;

        Intent intent = new Intent(
                multiple ? android.content.Intent.ACTION_SEND_MULTIPLE
                        : android.content.Intent.ACTION_SEND);

        if (multiple) {
            intent.setType("*/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        } else {
            Uri value = uris.get(0);
            String ext = MimeTypeMap.getFileExtensionFromUrl(value.toString());
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
            if(mimeType==null){
                mimeType = "*/*";
            }
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_STREAM, value);
        }
        context.startActivity(Intent.createChooser(intent, "Share"));
    }

}
