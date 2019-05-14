package main.cn.edu.sicnu.itop4412_projects01.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import main.cn.edu.sicnu.itop4412_projects01.Constances.Constances;
import main.cn.edu.sicnu.itop4412_projects01.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 注册页面
 * Created by Kaier on 2019/5/5.
 */

public class RightRegisterFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "RightRegisterFragment";
    private EditText name;
    private EditText occupation;
    private EditText salary;
    private EditText department;
    private Button register;
    private ProgressBar progressBar;

    //照片服务器
    private static String PHOTO_URL = "http://"+Constances.getIP()+":"+Constances.getPort()+"/final_project/employee/addEmployee";
    //获取自定义的SD卡存储路径
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"dachuang";
    //生成该路径的文件
    private File photoFile = new File(path);
    //返回值
    private String resultValue;

    private String photoPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.right_register_frag, container, false);
        name = view.findViewById(R.id.name);
        occupation = view.findViewById(R.id.occupation);
        salary = view.findViewById(R.id.salary);
        department = view.findViewById(R.id.department);
        register = view.findViewById(R.id.register);
        progressBar = view.findViewById(R.id.progressbar_register);
        register.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        //将所有输入禁用
        if("".equals(name.getText().toString().trim())
                ||"".equals(occupation.getText().toString().trim())
                ||"".equals(salary.getText().toString().trim())
                ||"".equals(department.getText().toString().trim())){
            Toast.makeText(getActivity(), "请输入完整信息!!!", Toast.LENGTH_SHORT).show();
            return ;
        }
        name.setEnabled(false);
        occupation.setEnabled(false);
        salary.setEnabled(false);
        department.setEnabled(false);
        register.setEnabled(false);
        switch (view.getId()){
            case R.id.register:
                progressBar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        takePhoto();
                    }
                }).start();
                break;
            default:break;
        }
    }

    /**
     * 调用拍照功能
     */
    public void takePhoto(){
        //判断该文件是否存在
        if(!photoFile.exists()){
            photoFile.mkdirs();
        }
        //创建存储图像的路径
        photoFile = new File(path,"/temp.jpg");
        photoPath = path+"/temp.jpg";
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(photoFile!=null){
            Log.d(TAG, "takePhoto: "+photoFile.getAbsolutePath());
            //启动相机拍照
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            //1作为页面标识
            startActivityForResult(captureIntent,1);
        }
    }

    /**
     * 回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
//            Toast.makeText(getActivity(), "上传中,请稍后...", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onActivityResult: "+PHOTO_URL);
            //将文件分割
            //Android版本不同所用到的MultipartBody  MultipartBuilder对象不同  我用的版本android 3.0以上 所以用的MultipartBuilder
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("cid", Constances.getCid() + "")
                    .addFormDataPart("name", name.getText().toString().trim())
                    .addFormDataPart("title", occupation.getText().toString())
                    .addFormDataPart("salary", salary.getText().toString())
                    .addFormDataPart("department", department.getText().toString())
                    .addFormDataPart("img", "temp.jpg", RequestBody.create(MediaType.parse("application/octet-stream"), photoFile));
            RequestBody requestBody = builder.build();
            Request request = new Request.Builder()
                    .url(PHOTO_URL)     //服务器URL
                    .post(requestBody)
                    .build();
            executeRequest(request);
        }
    }


    //服务器回调
    private void executeRequest(Request request) {

        //3.将Request封装为Call
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS).build();
        Call call = client.newCall(request);
        //4.执行call
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
                Message message = new Message();
                message.what = Constances.FAIL;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String relsult = response.body().string();//接收服务器返回来的信息
                try {
                    JSONObject jsonObject = new JSONObject(relsult);
                    String rel = jsonObject.getString("result");
                    resultValue = rel;
                    Message message = new Message();
                    if(!rel.equals("false")){
                        message.what = Constances.SUCCESS;
                    }else {
                        message.what = Constances.FAIL;
                    }
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //恢复注册按钮和EditText
            name.setEnabled(true);
            occupation.setEnabled(true);
            salary.setEnabled(true);
            department.setEnabled(true);
            register.setEnabled(true);
            //通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //将进度条设置为消失
            progressBar.setVisibility(View.GONE);
            switch (msg.what){
                case Constances.SUCCESS:
                    //注册成功
                    //设置title图标
                    builder.setIcon(R.mipmap.success);
                    //设置Title内容
                    builder.setTitle("结果");
                    //设置信息
                    builder.setMessage("注册成功!您的员工号为:"+resultValue+" 请牢记!!!");
                    //设置一个PositiveButton
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                    break;
                case Constances.FAIL:
                    //注册失败
                    //设置title图标
                    builder.setIcon(R.mipmap.fail);
                    //设置Title内容
                    builder.setTitle("结果");
                    //设置信息
                    builder.setMessage("注册失败!");
                    //设置一个PositiveButton
                    builder.setPositiveButton("重新注册", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Toast.makeText(getActivity(), "请重新输入!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //设置一个NegativeButton
                    builder.setNegativeButton("取消注册", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            name.setText("");
                            occupation.setText("");
                            salary.setText("");
                            department.setText("");
                        }
                    });
                    builder.show();
                    break;
                default:break;
            }
        }
    };

}
