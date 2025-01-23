package komga.hyui.xyz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class DownloadTask extends AsyncTask<String, Void, Void> {
    // 传递两个参数：URL 和 目标路径
    private String url;
    private String destPath;

    private Context context;

    public void CustomAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        System.out.println("开始下载");
    }

    @Override
    protected Void doInBackground(String... params) {
        url = params[0];
        destPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath() + File.separator;
        System.out.println("doInBackground. url:{}, dest:{}"+ params[0] +","+destPath);
        OutputStream out = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            InputStream in = urlConnection.getInputStream();
            out = new FileOutputStream(params[1]);
            byte[] buffer = new byte[10 * 1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            in.close();
        } catch (IOException e) {
            //log.warn(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    //log.warn(e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        System.out.println("完成下载");
        Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = getMIMEType(url);
        Uri uri = Uri.fromFile(new File(destPath));
        //log.debug("mimiType:{}, uri:{}", mimeType, uri);
        handlerIntent.setDataAndType(uri, mimeType);
        startActivity(handlerIntent);
    }

    private void startActivity(Intent handlerIntent) {
    }

    private String getMIMEType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        //log.debug("extension:{}", extension);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}

