package com.example.bai.utils.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by kangbaibai on 2018/12/20.
 */

public class ApplicationStatus {
    private static Application sApplication;

    public static void initialize(Application application) {
        sApplication = application;
    }

    public static Context getApplicationContext() {
        return sApplication != null ? sApplication.getApplicationContext() : getApplicationByReflect();
    }

    private static Application getApplicationByReflect() {
        try {
            Application application = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
            if (application != null) {
                return application;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Application application = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null, (Object[]) null);
            if (application != null) {
                return application;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
