package main.cn.edu.sicnu.itop4412_projects01.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import main.cn.edu.sicnu.itop4412_projects01.R;

/**
 * 注册页面
 * Created by Kaier on 2019/5/5.
 */

public class RightRegisterFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "RightRegisterFragment";
    private EditText name;
    private EditText occupation;
    private EditText salary;
    private EditText departmentId;
    private Button commit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.right_register_frag, container, false);
        name = v.findViewById(R.id.name);
        occupation = v.findViewById(R.id.occupation);
        salary = v.findViewById(R.id.salary);
        departmentId = v.findViewById(R.id.department_id);
        commit = v.findViewById(R.id.commit);
        commit.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: commit");
    }

}
