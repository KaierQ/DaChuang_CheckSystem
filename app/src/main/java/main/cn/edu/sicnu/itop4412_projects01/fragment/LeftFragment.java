package main.cn.edu.sicnu.itop4412_projects01.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import main.cn.edu.sicnu.itop4412_projects01.R;

/**
 * Created by Kaier on 2019/5/4.
 */

public class LeftFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.left_frag, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.title_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        TitleAdapter adapter = new TitleAdapter(getTitles());
        recyclerView.setAdapter(adapter);
        return view;
    }

    /**
     * 预先设置需要的栏目
     * @return
     */
    private List<String> getTitles(){
        List<String> titles = new ArrayList<>();
        titles.add("员工注册");
        titles.add("上班打卡");
        titles.add("下班打卡");
        titles.add("通知");
        return titles;
    }

    /**
     * 自建数据适配器
     */
    class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.ViewHolder>{

        private List<String> titles;

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView titleText;

            public ViewHolder(View view) {
                super(view);
                titleText = (TextView) view.findViewById(R.id.title_text);
            }
        }

        public TitleAdapter(List<String> ts){
            titles = ts;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.title_item, parent, false);
            final ViewHolder holder = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String title = titles.get(holder.getAdapterPosition());
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.right_layout,getFragment(title));
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String title = titles.get(position);
            holder.titleText.setText(title);
        }

        @Override
        public int getItemCount() {
            return titles.size();
        }

        /**
         * 根据用户点击选项返回Fragment
         * @param title
         * @return
         */
        private Fragment getFragment(String title){
            if("员工注册".equals(title)){
                return new RightRegisterFragment();
            }else if("上班打卡".equals(title)){
                return new RightCheckInFragment();
            }else if("下班打卡".equals(title)){
                return new RightCheckOutFragment();
            }else{
                return new RightNotifyFragment();
            }
        }

    }
}


