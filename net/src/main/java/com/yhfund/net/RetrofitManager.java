package com.yhfund.net;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.yhfund.net.defconfig.DefManagerCookie;
import com.yhfund.net.utils.RetrofitUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscription;

/**
 * Created by wudi on 17-5-2.
 *
 * retrofit 管理者
 */
public class RetrofitManager {

    private static volatile Retrofit mRetrofit;
    private static OkHttpClient mOkHttpClient;
    // 公共参数
    private static Map<String, Subscription> mRequestMap = new ConcurrentHashMap<>();

    public static RetrofitBuilder init(Context context,String baseUrl) {
        //[1]判空
        RetrofitUtils.checkNull("Context",context);
        RetrofitUtils.checkNullOrEmpty("BaseUrl",baseUrl);
        return new RetrofitBuilder(context,baseUrl);
    }

    static void build(RetrofitBuilder retrofitBuilder) {
        if (mRetrofit == null) {
            synchronized (Object.class) {
                if (mRetrofit == null) {
                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(new Cache(new File(retrofitBuilder.getContext().getExternalCacheDir(), "yh_cache"), 1024 * 1024 * 100))
                            .connectTimeout(retrofitBuilder.getConnectTimeoutSeconds(), TimeUnit.SECONDS)
                            .readTimeout(retrofitBuilder.getConnectTimeoutSeconds(), TimeUnit.SECONDS)
                            .writeTimeout(retrofitBuilder.getConnectTimeoutSeconds(), TimeUnit.SECONDS)
                            .addInterceptor(retrofitBuilder.getInterceptorParams())
                            .addInterceptor(retrofitBuilder.getInterceptorCommon())
                            .addInterceptor(retrofitBuilder.getInterceptorLogging())
                            .cookieJar(retrofitBuilder.getManagerCookie())
                            .build();

                    mRetrofit = new Retrofit.Builder()
                            .client(mOkHttpClient)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl(retrofitBuilder.getBaseUrl())
                            .build();

                }
            }
        }
    }

    /**
     * 创建api
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T create(Class<T> tClass) {
        if (mRetrofit == null) {
            throw new NullPointerException("需要先build");
        }
        return mRetrofit.create(tClass);
    }

/*
    *//**
     * 清楚缓存
     *//*
    public void clearCookie() {
        ((DefManagerCookie) mOkHttpClient.cookieJar()).clearCookie();
    }*/



    /**
     * 添加observable到Map
     *
     * @param subscription
     */
    public static void addSubscription(String code, Subscription subscription) {
        if (code == null) {
            return;
        }
        mRequestMap.put(code,subscription);
    }

    /**
     * 取消某个observable
     *
     * @param code
     */
    public static boolean cancelSubscription(String code) {
        if (code == null) {
            //取消整个context请求
            for (String key : mRequestMap.keySet()) {
                Subscription subscription = mRequestMap.get(key);
                if (subscription == null) {
                    continue;
                }

                if (!subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                }

                mRequestMap.remove(key);
            }
            Logger.d("取消页面所有请求！");
            return true;
        } else {
            //取消一个请求
            if (mRequestMap.containsKey(code)) {
                Subscription subscription = mRequestMap.get(code);
                if (subscription != null) {
                    if (!subscription.isUnsubscribed())
                        subscription.unsubscribe();
                }
                mRequestMap.remove(code);
            }
            Logger.d("取消请求:"+code+"!");
        }
        return true;
    }
}
