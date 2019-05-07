package main.cn.edu.sicnu.itop4412_projects01.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import main.cn.edu.sicnu.itop4412_projects01.R;

/**
 * Created by Kaier on 2019/5/5.
 */

public class RightNotifyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.right_notify_frag, container, false);
        return view;
    }

}
