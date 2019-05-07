package main.cn.edu.sicnu.itop4412_projects01.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import main.cn.edu.sicnu.itop4412_projects01.R;

/**
 * Created by Kaier on 2019/5/4.
 */

public class RightCheckInFragment extends Fragment implements View.OnClickListener{

    private EditText eId;
    private Button checkInImg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.right_checkin_frag, container, false);
        eId = v.findViewById(R.id.check_in_eid);
        checkInImg = v.findViewById(R.id.img_checkin);
        return v;
    }

    @Override
    public void onClick(View view) {

    }

}
