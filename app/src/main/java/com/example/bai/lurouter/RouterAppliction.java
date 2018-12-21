package com.example.bai.lurouter;

import android.app.Application;

import com.example.bai.utils.router.Router;
import com.example.bai.utils.utils.ApplicationStatus;


/**
 * Created by kangbaibai on 2018/11/13.
 */

public class RouterAppliction extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationStatus.initialize(this);
        Router.init(getApplicationContext());
    }
}
