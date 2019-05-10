package main.cn.edu.sicnu.itop4412_projects01.fragment;

import android.annotation.SuppressLint;
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

import com.baidu.aip.face.AipFace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import main.cn.edu.sicnu.itop4412_projects01.Constances.Constances;
import main.cn.edu.sicnu.itop4412_projects01.R;
import main.cn.edu.sicnu.itop4412_projects01.utils.AuthService;
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
    //获取用户信息的地址
    private static String EMPLOYEE_URL = "http://"+ Constances.getIP()+":"+Constances.getPort()+"/final_project/employee/isEmployeeExist";
    //提交下班打卡的参数
    private static String CHECK_OUT_URL = "http://"+ Constances.getIP()+":"+Constances.getPort()+"/final_project/attendanceDetail/checkOut";
    //获取自定义的SD卡存储路径
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"checkout";
    //生成该路径的文件
    private File photoFile = new File(path);
    //分数
    private float resultScore;
    //打卡结果
    private String resultValue;
    //相机拍照的路径
    private String photoPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.right_checkout_frag, container, false);
        eId = view.findViewById(R.id.check_out_eid);
        checkOutImg = view.findViewById(R.id.img_checkout);
        progressBar = view.findViewById(R.id.prograssbar_checkout);
        checkOutImg.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_checkout:
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "启动摄像头中", Toast.LENGTH_SHORT).show();
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
                    if(!isEmployeeExist()){
                        Message message = new Message();
                        message.what = Constances.NONE;
                        handler.sendMessage(message);
                        return ;
                    }
                    //发起服务器连接
                    OkHttpClient client = new OkHttpClient();
                    //传入参数
                    RequestBody requestBody = new FormBody.Builder()
                            .add("cid", String.valueOf(Constances.getCid()))
                            .add("eid",eId.getText().toString().trim()).build();
                    Request request = new Request.Builder()
                            .url(PHOTO_URL)
                            .post(requestBody).build();
                    byte[] bytes = null;
                    try {
                        Response response = client.newCall(request).execute();
                        //获得图片字节数组
                        bytes = response.body().bytes();
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ImageCompare(bytes,path+File.separator+"temp.jpg");
                }
            }).start();
        }
    }

    /**
     * 判断用户存在与否
     * @return
     */
    private boolean isEmployeeExist(){
        //发起服务器连接
        OkHttpClient client = new OkHttpClient();
        //传入参数
        RequestBody requestBody = new FormBody.Builder()
                .add("cid", String.valueOf(Constances.getCid()))
                .add("eid",eId.getText().toString().trim()).build();
        Request isEmployeeExistRequest = new Request.Builder()
                .url(EMPLOYEE_URL)
                .post(requestBody).build();
        try {
            Response response = client.newCall(isEmployeeExistRequest).execute();
            String retString = response.body().string();
            if(retString.equals("true")){
                return true;
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean commitCheckOutParam(){
        //发起服务器连接
        OkHttpClient client = new OkHttpClient();
        //传入参数
        RequestBody requestBody = new FormBody.Builder()
                .add("cid", String.valueOf(Constances.getCid()))
                .add("eid",eId.getText().toString().trim()).build();

        Request checkInRequest = new Request.Builder()
                .url(CHECK_OUT_URL)
                .post(requestBody).build();
        Response response = null;
        try {
            response = client.newCall(checkInRequest).execute();
            String retString = response.body().string();
            JSONObject jsonObject = new JSONObject(retString);
            resultValue = jsonObject.getString("result");
            response.close();
            if("true".equals(resultValue)){
                return true;
            }else{
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 进行人脸对比
     * @param rawImgPath
     * @param identifyImgPath
     */
    public void ImageCompare(String rawImgPath,String identifyImgPath){

        String result = AuthService.match(rawImgPath, identifyImgPath);
        float score = AuthService.twoImgIsSame(result);
        Message message = new Message();
        resultScore = score;
        if(score>=90){
            boolean b = commitCheckOutParam();
            message.what = Constances.SUCCESS;
            handler.sendMessage(message);
        }else{
            message.what = Constances.FAIL;
            handler.sendMessage(message);
        }//else

    }

    /**
     * 进行人脸对比
     * @param rawImgBytes
     * @param identifyImgPath
     */
    public void ImageCompare(byte[] rawImgBytes,String identifyImgPath){
        String result = AuthService.match(rawImgBytes, identifyImgPath);
        Message message = new Message();
        //判断result是否是空
        if(result==null){
            message.what = Constances.FAIL;
            handler.sendMessage(message);
            return ;
        }
        float score = AuthService.twoImgIsSame(result);
        resultScore = score;
        if(score>=90){
            boolean ret = commitCheckOutParam();
            if(ret){
                message.what = Constances.SUCCESS;
                handler.sendMessage(message);
            }else{
                message.what = Constances.NO_CHECK_IN;
                handler.sendMessage(message);
            }
        }else{
            message.what = Constances.FAIL;
            handler.sendMessage(message);
        }//else
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //进度条消失
            progressBar.setVisibility(View.GONE);
            switch (msg.what){
                case Constances.SUCCESS:
                    //注册成功
                    //设置title图标
                    builder.setIcon(R.mipmap.success);
                    //设置Title内容
                    builder.setTitle("识别结果");
                    //设置信息
                    builder.setMessage("打卡成功!识别分数:"+resultScore+".");
                    //    设置一个PositiveButton
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Toast.makeText(getActivity(), "打卡成功", Toast.LENGTH_SHORT).show();
                            eId.setText("");
                        }
                    });
                    builder.show();
                    resultScore = 0;
                    break;
                case Constances.FAIL:
                    //注册失败
                    //设置Title的图标
                    builder.setIcon(R.mipmap.fail);
                    //设置Title内容
                    builder.setTitle("识别结果");
                    //设置Content来显示一个信息
                    builder.setMessage("识别失败!识别分数:"+resultScore);
                    //设置一个PositiveButton
                    builder.setPositiveButton("重新识别", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Toast.makeText(getActivity(),"请稍后！",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.VISIBLE);
                            takePhoto();
                        }
                    });
                    //设置一个NegativeButton
                    builder.setNegativeButton("取消打卡", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Toast.makeText(getActivity(),"打卡失败！",Toast.LENGTH_LONG).show();
                            eId.setText("");
                        }
                    });
                    builder.show();
                    break;
                case Constances.NONE:
                    //设置Title的图标
                    builder.setIcon(R.mipmap.fail);
                    //设置Title内容
                    builder.setTitle("警告");
                    //    设置Content来显示一个信息
                    builder.setMessage("请输入正确员工号!");
                    //    设置一个PositiveButton
                    builder.setPositiveButton("重新输入", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            eId.setText("");
                            return ;
                        }
                    });
                    builder.show();
                    break;
                case Constances.NO_CHECK_IN:
                    //设置Title的图标
                    builder.setIcon(R.mipmap.fail);
                    //设置Title内容
                    builder.setTitle("警告");
                    //    设置Content来显示一个信息
                    builder.setMessage("您还未上班打卡,无法进行下班打卡");
                    //    设置一个PositiveButton
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            eId.setText("");
                            return ;
                        }
                    });
                    builder.show();
                    break;
                default:break;
            }
            //删除临时打卡照片
            File photoFile = new File(photoPath);
            if(photoFile.exists()){
                photoFile.delete();
            }
        }
    };

}
