package hyui.xyz;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.WebResourceResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DataUtils {
    private Map<String, String> mMap;

    public DataUtils(){
        mMap = new HashMap<>();
        InitDatas();
    }

    private void InitDatas(){
        String imgDir = "img/";
        String cssDir = "css/";
        String jsDir = "js/";
        String pngSuffix = ".png";
        String jpgSuffix = ".jpg";
        /*
        mMap.put("https://pic.hyui.xyz/content/images/system/home_cover_1608377797525_c44007.jpg",
                imgDir + "home_cover_1608377797525_c44007.jpg");
        mMap.put("https://pic.hyui.xyz/content/images/system/home_cover_1608377835213_ba1795.jpg",
                imgDir + "home_cover_1608377835213_ba1795.jpg");
        mMap.put("https://pic.hyui.xyz/content/images/system/home_cover_1608377788916_cc2bcc.jpg",
                imgDir + "home_cover_1608377788916_cc2bcc.jpg");
        mMap.put("https://pic.hyui.xyz/content/images/system/home_cover_1608379871362_28c809.png",
                imgDir + "home_cover_1608379871362_28c809.png");
        mMap.put("https://pic.hyui.xyz/lib/Peafowl/peafowl.min.css?198068b3cdca651ae033a746f970a50d",
                cssDir + "peafowl.min.css");
        mMap.put("https://pic.hyui.xyz/app/themes/Peafowl/style.min.css?198068b3cdca651ae033a746f970a50d",
                cssDir + "style.min.css");
        mMap.put("https://pic.hyui.xyz/lib/Peafowl/js/scripts.min.js?198068b3cdca651ae033a746f970a50d",
                cssDir + "scripts.min.js");
        mMap.put("https://pic.hyui.xyz/lib/Peafowl/peafowl.min.js?198068b3cdca651ae033a746f970a50d",
                jsDir + "peafowl.min.js");
        mMap.put("https://pic.hyui.xyz/app/lib/chevereto.min.js?198068b3cdca651ae033a746f970a50d",
                jsDir + "chevereto.min.js");
        mMap.put("https://ajax.cloudflare.com/cdn-cgi/scripts/7d0fa10a/cloudflare-static/rocket-loader.min.js",
                jsDir + "rocket-loader.min.js");
                */
    }

    public boolean hasLocalResource(String url){
        return mMap.containsKey(url);
    }

    public WebResourceResponse getReplacedWebResourceResponse(Context context, String url){
        String localResourcePath = mMap.get(url);
        if (TextUtils.isEmpty(localResourcePath)){
            return null;
        }
        InputStream is = null;
        try {
            is = context.getApplicationContext().getAssets().open(localResourcePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String mimeType;
        if (url.contains("css")){
            mimeType = "text/css";
        }else if(url.contains("jpg")){
            mimeType = "image/jpeg";
        }else if(url.contains("png")){
            mimeType = "image/png";
        }else{
            mimeType = "text/javascript";
        }
        WebResourceResponse response = new WebResourceResponse(mimeType, "utf-8", is);
        return response;
    }
}
