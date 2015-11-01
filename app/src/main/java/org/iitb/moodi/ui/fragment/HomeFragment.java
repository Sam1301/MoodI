package org.iitb.moodi.ui.fragment;
;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.iitb.moodi.R;
import org.iitb.moodi.ui.widget.ToolbarWidgetLayout;

/**
 * Created by udiboy on 22/10/15.
 */
public class HomeFragment extends BaseFragment {
    public static HomeFragment newInstance() {
        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.mID = R.layout.fragment_main;
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        return v;
    }

    @Override
    public void customizeToolbarLayout(ToolbarWidgetLayout toolbarLayout) {
        super.customizeToolbarLayout(toolbarLayout);

        View v = mActivity.getLayoutInflater().inflate(R.layout.widget_countdown_clock,mActivity.toolbarContainer,false);
        toolbarLayout.setWidget(v);
    }
}
