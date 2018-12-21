package com.example.bai.utils.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;


import com.example.bai.utils.router.Router;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentHandler {


    private class FragmentInfo {
        public String tag;
        public int idContainer;
        public String moduleName;
        public String methodName;
        public Bundle args;

        public FragmentInfo(String tag, int idContainer, String moduleName, String methodName, Bundle args) {
            this.tag = tag;
            this.idContainer = idContainer;
            this.moduleName = moduleName;
            this.methodName = methodName;
            this.args = args;
        }
    }

    private Map<String, FragmentInfo> mapFragmentInfo = new HashMap<String, FragmentInfo>();
    private FragmentManager fragmentManager;

    private FragmentHandler() {
    }

    public FragmentHandler(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public boolean registerFragment(String tag, int idContainer, String moduleName, String methodName, Bundle bundle) {
        if (TextUtils.isEmpty(moduleName) || TextUtils.isEmpty(methodName))
            return false;

        if (mapFragmentInfo.containsKey(tag))
            return false;

        mapFragmentInfo.put(tag, new FragmentInfo(tag, idContainer, moduleName, methodName, bundle));
        return true;
    }

    public boolean unregisterFragment(String tag) {
        if (tag == null)
            return false;

        if (!mapFragmentInfo.containsKey(tag))
            return false;

        mapFragmentInfo.remove(tag);
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean switchToFragment(String tag, boolean addToBackStack) {
        return switchToFragment(tag, addToBackStack, -1, -1, -1, -1);
    }

    // Fragment切换时不销毁，提高切换Fragment的效率；
    // 因此需要注意切换Fragment时onResume、onPause、onDestroyView、onCreateView等消息不一定会触发
    @SuppressWarnings("unchecked")
    public boolean switchToFragment(String tag, boolean addToBackStack, int enterAnimation, int exitAnimation, int popEnterAnimation, int popExitAnimation) {
        if (tag == null) {
            return false;
        }

        if (!mapFragmentInfo.containsKey(tag)) {
            return false;
        }

        FragmentInfo info = mapFragmentInfo.get(tag);
        if (info == null || TextUtils.isEmpty(info.moduleName) || TextUtils.isEmpty(info.methodName)) {
            return false;
        }

        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            try {
                fragment = Router.createFragment(ApplicationStatus.getApplicationContext(), info.moduleName, info.methodName, info.args);
                fragmentManager.beginTransaction().add(info.idContainer, fragment, tag).commit();
            } catch (Exception e) {
                return false;
            }
        }

        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (enterAnimation != -1 && exitAnimation != -1) {
            if (popEnterAnimation == -1 || popExitAnimation == -1) {


                ft.setCustomAnimations(enterAnimation, exitAnimation);
            } else {
                ft.setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation);
            }
        }

        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment iter : fragments) {
                if (iter == null || iter == fragment || iter.isHidden())
                    continue;
                ft.hide(iter);
                iter.setUserVisibleHint(false);
            }
        }

        ft.show(fragment);
        fragment.setUserVisibleHint(true);
        if (addToBackStack)
            ft.addToBackStack(null);

        ft.commitAllowingStateLoss();
        return true;
    }

    // 需要注意的是，getCurrentFragment和isHomeFragment都基于一个假设，即同一时刻，只有一个顶级Fragment处于显示状态，对于目前的App这个假设是合理的，
    // 但是后续如果有横屏或其他的需求导致的改动，则不应仍有以上假设
    public Fragment getCurrentFragment() {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment iter : fragments) {
                if (iter == null || iter.isHidden())
                    continue;

                return iter;
            }
        }
        return null;
    }

    public void addFragment(Class<? extends Fragment> fragmentClass) {
        if (fragmentClass == null)
            return;

        if (!mapFragmentInfo.containsKey(fragmentClass.getName())) {
            return;
        }

        FragmentInfo info = mapFragmentInfo.get(fragmentClass.getName());
        if (info == null || TextUtils.isEmpty(info.methodName) || TextUtils.isEmpty(info.moduleName)) {
            return;
        }

        Fragment fragment = fragmentManager.findFragmentByTag(fragmentClass.getName());
        if (fragment == null) {
            try {
                fragment = Router.createFragment(ApplicationStatus.getApplicationContext(), info.moduleName, info.methodName, info.args);
                fragmentManager.beginTransaction().add(info.idContainer, fragment, fragmentClass.getName()).hide(fragment).commitAllowingStateLoss();
            } catch (Exception e) {
            }
        }
    }

    public <T extends Fragment> T getFragment(Class<T> fragmentClass) {
        if (!mapFragmentInfo.containsKey(fragmentClass.getName())) {
            return null;
        }
        return fragmentClass.cast(fragmentManager.findFragmentByTag(fragmentClass.getName()));
    }
}
