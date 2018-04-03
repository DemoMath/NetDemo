package com.yhfund.net.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Created by wudi on 17-4-18.
 *
 * <p>获取网络状态</p>
 */
public class NetWorkUtils {

    /**
     * 获取当前网络类型
     *
     * @return 0：未连接   1：WIFI网络   2：Mobile网络    3：ETHERNET网络 （网线）
     */
    private static final int NETTYPE_NULL = 0;
    private static final int NETTYPE_WIFI = 1;
    private static final int NETTYPE_MOBILE = 2;
    private static final int NETTYPE_ETHERNET网络 = 3;


    /**
     * 判断是否链接网络
     *
     * @param context manager中的context环境
     * @return
     */
    public static boolean isNetConnect(Context context) {
        int net_status = getNetworkType(context);
        if (net_status == 0) {
            return false;
        }
        return true;
    }


    /**
     * 得到网络类型
     *
     * @param context manager中的context环境
     * @return
     */
    private static int getNetworkType(Context context) {
        int netType = NETTYPE_NULL;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            netType = NETTYPE_MOBILE;
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }
}
