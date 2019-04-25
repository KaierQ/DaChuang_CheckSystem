package main.cn.edu.sicnu.itop4412_projects01.Constances;

/**
 * Created by Kaier on 2019/4/24.
 */

/**
 * 定义本程序需要使用到的一些常量
 */
public class Constances {

    private static final String LOGIN_SERVER_URL="http://192.168.8.124:8080/final_project/company/check_login";

    public static final int SUCCESS = 1;

    public static final int FAIL = 2;

    /**
     * 获取登陆验证的url
     * @return
     */
    public static String getLOGIN_SERVER_URL() {
        return LOGIN_SERVER_URL;
    }

    public static final int getSuccess(){
        return SUCCESS;
    }

    public static final int getFail(){
        return FAIL;
    }
}
