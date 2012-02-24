package com.example;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

public class Util {
    public static boolean isDebugMode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(context.getPackageName(), 0);
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (NameNotFoundException e) {
        }
        return true;
    }
    
    public static boolean useStrictMode(Context context) {
        return isDebugMode(context) && Build.VERSION.SDK_INT >= 9;
    }
}
