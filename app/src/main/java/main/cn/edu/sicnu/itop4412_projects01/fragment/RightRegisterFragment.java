package main.cn.edu.sicnu.itop4412_projects01.fragment;

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
        name.setEnabled(false);
        occupation.setEnabled(false);
        salary.setEnabled(false);
        department.setEnabled(false);
        register.setEnabled(false);
        switch (view.getId()){
            case R.id.register:
                Log.d(TAG, "onClick: click");
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
            //这种情况下data为null,因为自定义了路径,所以通过这个路径来获取
            //Bitmap bitmap = BitmapUtil.getSmallBitmap(photoPath);
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

//            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),photoFile);
//            //addPart用来传入文件参数
//            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"img\";filename=\""+photoFile.getName()+"\""),fileBody);
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
                    Message message = new Message();
                    if(rel.equals("true")){
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
            switch (msg.what){
                case Constances.SUCCESS:
                    //注册成功
                    //将进度条设置为消失
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_SHORT).show();
                    break;
                case Constances.FAIL:
                    //注册失败
                    //进度条消失
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),"网络连接失败！",Toast.LENGTH_LONG).show();
//                    Toast.makeText(getActivity(), "注册失败", Toast.LENGTH_SHORT).show();
                    break;
                default:break;
            }
        }
    };

}
