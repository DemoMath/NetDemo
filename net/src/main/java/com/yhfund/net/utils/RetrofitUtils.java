package com.yhfund.net.utils;

/**
 * Created by wudi on 17-4-6.
 * 判空工具类
 */
public class RetrofitUtils {

  public static void checkNull (String message, Object object) {
    if (object == null) {
      throw new NullPointerException(message+ "不能为null");
    }
  }

  public static void checkNullOrEmpty(String message, String value) {
    if (isEmpty(value)) {
      throw new NullPointerException(message+ "不能为null或者空");
    }
  }

  public static boolean isEmpty(String text) {
    return text == null || text.trim().length() == 0;
  }
}
