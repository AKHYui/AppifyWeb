package hyui.xyz;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

import static android.view.KeyEvent.KEYCODE_BACK;

public class MainActivity extends AppCompatActivity {
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;

    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        // 找到WebView的ID
        webview = (WebView) findViewById(R.id.webView1);
        assert webview != null;
        checkPermission();
        WebviewSettings();
        // download();
        uploadPic();
        SetDownload();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            int createResult = CreateDir();
            if (createResult == FileUtils.SUCCESS_FLAG) {
                Toast.makeText(this, "当前已获取读写存储权限，下载路径为Download/Hpic", Toast.LENGTH_SHORT).show();
            } else if (createResult == FileUtils.EXITS_FLAG) {
                Toast.makeText(this, "当前已获取读写存储权限，已有下载路径为Download/Hpic", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "当前已获取读写存储权限，但下载路径未创建", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else {
            Toast.makeText(this, "请前往设置赋予本应用读写手机存储权限", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    // webview设定
    public void WebviewSettings() {
        // 开启JavaScript
        webview.getSettings().setJavaScriptEnabled(true);
        // 允许JavaScript打开新窗口
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // 启用缓存模式
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        webview.getSettings().setDomStorageEnabled(true);
        // 设置WebView的Client
        webview.setWebChromeClient(new WebChromeClient());
        // 这个要有，因为不加这个会唤起系统自带浏览器
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            // 命中本地资源并替代
            DataUtils mDataUtils = new DataUtils();

            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (mDataUtils.hasLocalResource(url)) {
                    WebResourceResponse response = mDataUtils.getReplacedWebResourceResponse(getApplicationContext(),
                            url);
                    if (response != null) {
                        return response;
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (mDataUtils.hasLocalResource(url)) {
                    WebResourceResponse response = mDataUtils.getReplacedWebResourceResponse(getApplicationContext(),
                            url);
                    if (response != null) {
                        return response;
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }
        });
        // 加载网址
        String targetUrl = "https://maniax.hyui.xyz";
        webview.loadUrl(targetUrl);

    }

    // 旧下载功能
    public void download() {
        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                    long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    /* 图片上传 */
    public void uploadPic() {
        webview.setWebChromeClient(new WebChromeClient() {
            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                /*
                 * uploadMessageAboveL = filePathCallback;
                 * openImageChooserActivity();
                 * return true;
                 */
                if (uploadMessageAboveL != null) {
                    uploadMessageAboveL.onReceiveValue(null);
                    uploadMessageAboveL = null;
                }
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }
        });

    }

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[] { Uri.parse(dataString) };
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

    // 按返回键控制网页后退
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 下载功能
    private void SetDownload() {
        final String dirname = "Hpic";
        webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType,
                    long contentLength) {
                String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
                String destPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .getAbsolutePath() + File.separator + dirname + File.separator + fileName;
                System.out.println(destPath);
                new DownloadTask().execute(url, destPath);
                File file = new File(destPath);
                String fileType = FilenameUtils.getExtension(fileName).toLowerCase();
                addImageGallery(file, fileType);
                Toast.makeText(MainActivity.this, "下载完成，文件下载至" + destPath, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 保存后刷新相册
    private void addImageGallery(File file, String fileType) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        String type = "";
        type = FileUtils.filesType(fileType);
        values.put(MediaStore.Images.Media.MIME_TYPE, type);
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    // 创建目录
    private int CreateDir() {
        String dirname = "Hpic";
        String dirpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath() + File.separator + dirname;
        int result = FileUtils.CreateDir(dirpath);
        return result;
    }

}
