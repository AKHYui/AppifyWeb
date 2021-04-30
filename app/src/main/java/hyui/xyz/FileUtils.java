package hyui.xyz;

import android.util.Log;

import java.io.File;

public class FileUtils {
    private static final String TAG = "FileUtils";
    public static final int SUCCESS_FLAG = 1; //成功
    public static final int EXITS_FLAG = 2; //已存在
    public static final int FAILED_FLAG = 3; //失败

    public static int CreateDir(String Path){

        File dir = new File(Path);
        if(dir.exists()){
            Log.w(TAG,"该目录[" + Path + "]已存在");
            return EXITS_FLAG;
        }
        if (!Path.endsWith(File.separator)){
            Path = Path + File.separator;
        }
        if(dir.mkdirs()){
            Log.d(TAG,"创建[" + Path + "]成功");
            return SUCCESS_FLAG;
        }

        Log.e(TAG,"创建[" + Path + "]失败");
        return FAILED_FLAG;
    }
}
