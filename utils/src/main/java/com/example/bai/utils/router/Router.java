package com.example.bai.utils.router;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.example.bai.utils.router.bundle.ModuleManager;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class Router {
    private static final String TAG = "Router";

    public interface Initializer {
        void onInit(Context applicationContext);
    }

    public interface HttpSchemeDelegate {
        void onRequest(Context context, String url);
    }

    public static final int UNKNOWN_SCHEME = -1;
    public static final int PRIVATE_URL_SCHEME = 1;
    public static final int HTTP_URL_SCHEME = 2;
    public static final int HTTPS_URL_SCHEME = 3;

    public static final String ROUTER_TYPE_ACTIVITY = "activity";
    public static final String ROUTER_TYPE_SERVICE = "service";
    public static final String ROUTER_TYPE_FRAGMENT = "fragment";

    private static Map<String, Object> sServiceMap = new HashMap<>();
    private static boolean isInitialized = false;
    private static HttpSchemeDelegate httpSchemeDelegate;

    static public void init(final Context applicationContext) {
        ModuleManager.getInstance().loadBundles(applicationContext);
        isInitialized = true;
    }

    /**
     * 用来检查Rounter是否已初始化
     */
    static private void check() {
        if (!isInitialized) {
            throw new RuntimeException("Rounter Uninitialized!!");
        }
    }

    @SuppressWarnings("unchecked")
    static public <T> T getService(String moduleName, String itemName, Class<T> apiClass) {
        check();

        Object service = sServiceMap.get(moduleName + "_" + itemName);
        if (service == null) {
            String serviceClassName = ModuleManager.getInstance().getClass(moduleName, ROUTER_TYPE_SERVICE, itemName);
            if (serviceClassName == null) {
                Log.e(TAG, "Service not found in bundle.xml!");
                return null;
            }
            try {
                Class clazz = Class.forName(serviceClassName);
                service = clazz.newInstance();
                sServiceMap.put(moduleName + "_" + itemName, service);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!apiClass.isInstance(service)) {
            Log.e(TAG, "Instantiate service failed!");
            return null;
        }

        return (T) service;
    }

    static public Fragment createFragment(Context context, String moduleName, String itemName, Bundle args) {
        check();
        String fragmentClassName = ModuleManager.getInstance().getClass(moduleName, ROUTER_TYPE_FRAGMENT, itemName);
        if (fragmentClassName == null) {
            Log.e(TAG, "Fragment not found in bundle.xml!");
            return null;
        }

        try {
            Fragment fragment;
            if (args == null) {
                fragment = Fragment.instantiate(context, fragmentClassName);
            } else {
                ModuleManager.getInstance().checkRequiredArgs(moduleName, ROUTER_TYPE_FRAGMENT, itemName, args);
                fragment = Fragment.instantiate(context, fragmentClassName, args);
            }

            if (fragment == null) {
                Log.e(TAG, "Instantiate fragment failed!");
            }
            return fragment;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    static public void startActivity(Context context, String moduleName, String itemName, Bundle args) {
        check();
        String activityClassName = ModuleManager.getInstance().getClass(moduleName, ROUTER_TYPE_ACTIVITY, itemName);
        if (activityClassName == null) {
            Log.e(TAG, "Activity not found in bundle.xml!");
            return;
        }

        try {
            Intent intent = getIntent(new ComponentName(context, activityClassName), args);

            if (args != null) {
                ModuleManager.getInstance().checkRequiredArgs(moduleName, ROUTER_TYPE_ACTIVITY, itemName, args);
                intent.putExtras(args);
            }

            context.startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static public void invokeUrl(Context context, String openUrl) {
        invokeUrl(context, null, openUrl);
    }

    static public Observable<String> invokeUrl(Context context, Object handlerInstance, String openUrl) {
        check();

        if (context == null || TextUtils.isEmpty(openUrl)) {
            return Observable.just(null);
        }

        try {
            int scheme = getScheme(openUrl);

            if (scheme == PRIVATE_URL_SCHEME) {
                return ModuleManager.getInstance().invokeUrl(context, handlerInstance, openUrl);
            } else if (scheme == HTTP_URL_SCHEME || scheme == HTTPS_URL_SCHEME) {
                if (httpSchemeDelegate != null) {
                    httpSchemeDelegate.onRequest(context, openUrl);
                }
                return Observable.just(null);
            } else {
                Log.e(TAG, "Unknown scheme!");
            }
        } catch (Throwable e) {
            Log.e(TAG, "Invoke url failed!");
            e.printStackTrace();
        }

        return Observable.just(null);
    }

    static public void startActivityForResult(Activity activity, int requestCode, String moduleName, String itemName, Bundle args) {
        check();
        String activityClassName = ModuleManager.getInstance().getClass(moduleName, ROUTER_TYPE_ACTIVITY, itemName);
        if (activityClassName == null) {
            Log.e(TAG, "Activity not found in bundle.xml!");
            return;
        }

        try {
            Intent intent = getIntent(new ComponentName(activity, activityClassName), args);
            activity.startActivityForResult(intent, requestCode);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private static Intent getIntent(ComponentName component, Bundle args) {
        Intent intent = new Intent();
        intent.setComponent(component);

        if (args != null) {
            intent.putExtras(args);
        }
        return intent;
    }

    public static int getScheme(String openUrl) {
        if (TextUtils.isEmpty(openUrl)) {
            return UNKNOWN_SCHEME;
        }

        Uri requestURI = Uri.parse(openUrl);
        String scheme = !TextUtils.isEmpty(requestURI.getScheme()) ? requestURI.getScheme().toLowerCase() : "";
        if ("private".equals(scheme)) {
            return PRIVATE_URL_SCHEME;
        } else if ("http".equals(scheme)) {
            return HTTP_URL_SCHEME;
        } else if ("https".equals(scheme)) {
            return HTTPS_URL_SCHEME;
        } else {
            return UNKNOWN_SCHEME;
        }
    }

    public static void registerHttpSchemeDelegate(HttpSchemeDelegate delegate) {
        httpSchemeDelegate = delegate;
    }
}
