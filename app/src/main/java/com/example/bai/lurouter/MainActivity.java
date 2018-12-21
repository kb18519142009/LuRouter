package com.example.bai.lurouter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.bai.utils.utils.FragmentHandler;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    public static final String TAG_HOME = "HOME";
    public static final String TAG_COMMUNITY = "COMMUNITY";
    public static final String TAG_USER = "USER";

    private FragmentHandler mFragmentHandler;
    private RadioGroup mTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTab = findViewById(R.id.rg_tab_bar);
        mTab.setOnCheckedChangeListener(this);

        mFragmentHandler = new FragmentHandler(getSupportFragmentManager());
        registerFragment();
        checkTab(R.id.rb_tab_home_hot);
    }

    private void registerFragment() {
        mFragmentHandler.registerFragment(TAG_HOME, R.id.fragment_stub, "home", "home", null);
        mFragmentHandler.registerFragment(TAG_COMMUNITY, R.id.fragment_stub, "community", "community", null);
        mFragmentHandler.registerFragment(TAG_USER, R.id.fragment_stub, "mine", "mine", null);
    }

    public void checkTab(int index) {
        RadioButton b;
        switch (index) {
            case R.id.rb_tab_home_hot:
                b = mTab.findViewById(R.id.rb_tab_home_hot);
                break;
            case R.id.rb_tab_home_community:
                b = mTab.findViewById(R.id.rb_tab_home_community);
                break;
            case R.id.rb_tab_home_profile:
                b = mTab.findViewById(R.id.rb_tab_home_profile);
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (b != null && !b.isChecked()) {
            b.setChecked(true);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (R.id.rb_tab_home_hot == checkedId) {
            mFragmentHandler.switchToFragment(TAG_HOME, false);
        } else if (R.id.rb_tab_home_community == checkedId) {
            mFragmentHandler.switchToFragment(TAG_COMMUNITY, false);
        } else if (R.id.rb_tab_home_profile == checkedId) {
            mFragmentHandler.switchToFragment(TAG_USER, false);
        }
    }
}
