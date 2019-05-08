package main.cn.edu.sicnu.itop4412_projects01.fragment;

import android.annotation.SuppressLint;
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

import com.baidu.aip.face.AipFace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import main.cn.edu.sicnu.itop4412_projects01.Constances.Constances;
import main.cn.edu.sicnu.itop4412_projects01.R;
import main.cn.edu.sicnu.itop4412_projects01.utils.Sample;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Kaier on 2019/5/4.
 */

public class RightCheckOutFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "RightCheckOutFragment";
    private EditText eId;
    private Button checkOutImg;
    private ProgressBar progressBar;

    //照片服务器图片获取地址
    private static String PHOTO_URL = "http://"+ Constances.getIP()+":"+Constances.getPort()+"/final_project/employee/checkIn_getImg";
    //获取自定义的SD卡存储路径
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"checkout";
    //生成该路径的文件
    private File photoFile = new File(path);
    //图像检测服务器请求端
    private AipFace aipFaceClient;
    private String photoPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.right_checkout_frag, container, false);
        eId = view.findViewById(R.id.check_out_eid);
        checkOutImg = view.findViewById(R.id.img_checkout);
        progressBar = view.findViewById(R.id.prograssbar_checkout);
        checkOutImg.setOnClickListener(this);

        // 初始化一个AipFace
        aipFaceClient = new AipFace(Sample.APP_ID, Sample.API_KEY, Sample.SECRET_KEY);
        // 可选：设置网络连接参数
        aipFaceClient.setConnectionTimeoutInMillis(2000);
        aipFaceClient.setSocketTimeoutInMillis(60000);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_checkout:
                progressBar.setVisibility(View.VISIBLE);
                takePhoto();
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
            Toast.makeText(getActivity(), "检测中,请稍后...", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onActivityResult: "+PHOTO_URL);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //发起服务器连接
                    OkHttpClient client = new OkHttpClient();
                    //传入参数
                    RequestBody requestBody = new FormBody.Builder()
                            .add("cid", String.valueOf(Constances.getCid()))
                            .add("eid",eId.getText().toString().trim()).build();
                    Request request = new Request.Builder()
                            .url(PHOTO_URL)
                            .post(requestBody).build();
                    try {
                        Response response = client.newCall(request).execute();
                        byte[] bytes = response.body().bytes();
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(path,"raw.jpg"));
                        fileOutputStream.write(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //进行图像比对
                    ImageCompare(aipFaceClient);
                    Message message = new Message();
                    message.what = Constances.SUCCESS;
                    handler.sendMessage(message);
                }
            }).start();
        }
    }

    /**
     * 图像检测
     * @param client
     */
    public void ImageCompare(AipFace client) {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("ext_fields", "qualities");
        options.put("image_liveness", ",faceliveness");
        options.put("types", "7,13");

        //参数为本地图片路径列表
        String path1 = path+"/raw.jpg";
        String path2 = path+"/temp.jpg";
        ArrayList<String> images = new ArrayList<String>();
        images.add(path1);
        images.add(path2);
        //进行图像对比
        JSONObject res = client.match(images, options);
        try {
            JSONObject result = res.getJSONObject("result");
            JSONArray jsonArray = result.getJSONArray(result.toString());
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String score = jsonObject.getString("score");
            Integer integer = Integer.valueOf(score);
            Message message = new Message();
            if(integer>=80){
                message.what = Constances.SUCCESS;
                handler.sendMessage(message);
            }else{
                message.what = Constances.FAIL;
                handler.sendMessage(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "ImageCompare: "+res.toString());
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //恢复注册按钮和EditText
            switch (msg.what){
                case Constances.SUCCESS:
                    //注册成功
                    //将进度条设置为消失
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "打卡成功", Toast.LENGTH_SHORT).show();
                    break;
                case Constances.FAIL:
                    //注册失败
                    //进度条消失
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),"打卡失败！",Toast.LENGTH_LONG).show();
                    break;
                default:break;
            }
        }
    };

}
