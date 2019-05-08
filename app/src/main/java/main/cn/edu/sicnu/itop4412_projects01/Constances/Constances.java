package main.cn.edu.sicnu.itop4412_projects01.Constances;

/**
 * Created by Kaier on 2019/4/24.
 */

/**
 * 定义本程序需要使用到的一些常量
 */
public class Constances {

    private static String IP = "192.168.31.179";

    private static String port = "8080";

    private static final String LOGIN_SERVER_URL="http://"+IP+":"+port+"/final_project/company/check_login";

    public static final int SUCCESS = 1;

    public static final int FAIL = 2;

    private static int cid = 10001;

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

    public static String getIP() {
        return IP;
    }

    public static String getPort() {
        return port;
    }

    public static int getCid() {
        return cid;
    }

    public static void setCid(int cid) {
        Constances.cid = cid;
    }
}
