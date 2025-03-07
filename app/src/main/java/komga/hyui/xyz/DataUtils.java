package komga.hyui.xyz;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.WebResourceResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DataUtils {
    private Map<String, String> site1Map;
    private Map<String, String> site2Map;
    private Map<String, String> site3Map;

    public DataUtils() {
        site1Map = new HashMap<>();
        site2Map = new HashMap<>();
        site3Map = new HashMap<>();
        initSite1Data();
        initSite2Data();
        initSite3Data();
    }

    private void initSite1Data() {
        String cssDir = "css/";
        String jsDir = "js/";
        String fontDir = "fonts/";
        String imgDir = "img/";

        site1Map.put("https://komga.hyui.xyz/css/.*\\.css", cssDir + "$0");
        site1Map.put("https://komga.hyui.xyz/js/.*\\.js", jsDir + "$0");
        site1Map.put("https://komga.hyui.xyz/js/.*\\.map", jsDir + "$0");
        site1Map.put("https://komga.hyui.xyz/fonts/.*", fontDir + "$0");
        site1Map.put("https://komga.hyui.xyz/img/.*\\.svg", imgDir + "$0");
    }

    private void initSite2Data() {
        String cssDir = "css/";
        String jsDir = "js/";
        String fontDir = "fonts/";
        String imgDir = "img/";

        site2Map.put("https://maniax.hyui.xyz/css/.*\\.css", cssDir + "$0");
        site2Map.put("https://maniax.hyui.xyz/js/.*\\.js", jsDir + "$0");
        site2Map.put("https://maniax.hyui.xyz/js/.*\\.map", jsDir + "$0");
        site2Map.put("https://maniax.hyui.xyz/fonts/.*", fontDir + "$0");
        site2Map.put("https://maniax.hyui.xyz/img/.*\\.svg", imgDir + "$0");
    }

    private void initSite3Data() {
        String cssDir = "css/";
        String jsDir = "js/";
        String fontDir = "fonts/";
        String imgDir = "img/";

        site3Map.put("https://komga.hk.hyui.xyz/css/.*\\.css", cssDir + "$0");
        site3Map.put("https://komga.hk.hyui.xyz/js/.*\\.js", jsDir + "$0");
        site3Map.put("https://komga.hk.hyui.xyz/js/.*\\.map", jsDir + "$0");
        site3Map.put("https://komga.hk.hyui.xyz/fonts/.*", fontDir + "$0");
        site3Map.put("https://komga.hk.hyui.xyz/img/.*\\.svg", imgDir + "$0");
    }

    private void initSite4Data() {
        String cssDir = "css/";
        String jsDir = "js/";
        String fontDir = "fonts/";
        String imgDir = "img/";

        site3Map.put("https://maniax.hk.hyui.xyz/css/.*\\.css", cssDir + "$0");
        site3Map.put("https://maniax.hk.hyui.xyz/js/.*\\.js", jsDir + "$0");
        site3Map.put("https://maniax.hk.hyui.xyz/js/.*\\.map", jsDir + "$0");
        site3Map.put("https://maniax.hk.hyui.xyz/fonts/.*", fontDir + "$0");
        site3Map.put("https://maniax.hk.hyui.xyz/img/.*\\.svg", imgDir + "$0");
    }

    public boolean hasLocalResource(String url) {
        return site1Map.containsKey(url) || site2Map.containsKey(url) || site3Map.containsKey(url);
    }

    public WebResourceResponse getReplacedWebResourceResponse(Context context, String url) {
        String localResourcePath = getLocalResourcePath(url);
        if (TextUtils.isEmpty(localResourcePath)) {
            return null;
        }
        InputStream is = null;
        try {
            is = context.getApplicationContext().getAssets().open(localResourcePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String mimeType = getMimeType(url);
        return new WebResourceResponse(mimeType, "utf-8", is);
    }

    private String getLocalResourcePath(String url) {
        String localPath = matchResource(url, site1Map);
        if (TextUtils.isEmpty(localPath)) {
            localPath = matchResource(url, site2Map);
        }
        if (TextUtils.isEmpty(localPath)) {
            localPath = matchResource(url, site3Map);
        }
        return localPath;
    }

    private String matchResource(String url, Map<String, String> siteMap) {
        for (Map.Entry<String, String> entry : siteMap.entrySet()) {
            String pattern = entry.getKey();
            String localPath = entry.getValue();
            if (Pattern.matches(pattern, url)) {
                return localPath.replaceFirst(pattern, "");
            }
        }
        return null;
    }

    private String getMimeType(String url) {
        if (url.endsWith(".css")) {
            return "text/css";
        } else if (url.endsWith(".js")) {
            return "application/javascript";
        } else if (url.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (url.endsWith(".js.map")) {
            return "application/json";
        } else if (url.endsWith(".woff") || url.endsWith(".woff2") || url.endsWith(".ttf") || url.endsWith(".otf")) {
            return "application/font-woff";
        } else {
            return "application/octet-stream";
        }
    }
}