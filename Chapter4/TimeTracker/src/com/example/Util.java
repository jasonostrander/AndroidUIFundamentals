package com.example;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

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
}
