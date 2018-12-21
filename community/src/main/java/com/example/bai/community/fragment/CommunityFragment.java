package com.example.bai.community.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bai.community.R;
import com.example.bai.utils.annotation.RouterExport;

/**
 * Created by kangbaibai on 2018/12/20.
 */
@RouterExport
public class CommunityFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community, null);
    }
}
