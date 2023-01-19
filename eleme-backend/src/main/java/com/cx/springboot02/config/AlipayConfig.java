package com.cx.springboot02.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2021000121697034";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCHAMMG5VuRMdmhK9J/k5ynqW8Kxzz21s4xSBFIOmDb8DwngmoqyTxrhbaCgPXbznYE2ztqYbkv/lGvMXZNL+nOw9mCigybNqeczWZPovn/a8odJOTPpTU5m9XeCGa+3OPMUEmjTVUZSNIRCMlIdEbb51I0tFPrTKMScemA/miEt1nQvK7TDym0FQo/cU9UmObU6UX/hAXL73LQod/K9EIY+Nl243XlaTcuAsP8r+9BWe96hXODs7GpJ4QZ13Qn/5t9csnRqVO8eKh6tJKvuYWSmT/aswJTrH3gXu6kNZR1bICWFIVzVkQFq1ANy81eXG6hf+crZG7EV3nq4v/y/p8vAgMBAAECggEAB1iFRCTKvLXHr7tgssxnY8AWaLG6oXvCsG/JcPKsPott2oCAIl/cx8T4z1URC6rKRS1gqEM0/mWKMEwysbR/tl51wWKdmPx6Vk/e10p46+sDjl5pjNricIYINxKh9JS0WHOt7hLRJJUPyRl/Sk/5YxHNKUz+m/kJjoFgQUKWkVIMJdja/nOfXUc5kYY3Gd+tHnEXYPSzjZdPxbQCcHt9CqYhx5B4xAqnEckrf8FD/kqr75F5jQisMHlCHVrblyCTcubU/B3HMX30Lj13ba3EKhRyCNVDCOmHmjYWfKEXk8WJowmwWxt++wzO2Nk+dtMvqpk0tudicnJhEXMNi4vG0QKBgQDeD5iCaX6FvVRJFdiiTIBPt4NBWgErNYmIlNo/2igJ5tCqg4C+aucgeHEqbfkVQh1p/hqVSr6pCUEeRL+1HX8gKpLBTMLEoCdVMSwkYh8hjJeu3pCEbBT5jZy/oSGGC4QslzJ1fJWiwEwGqpvJXb609kzgKj+EY1bxc5YHhcD2+wKBgQCbour4Dlk7hJDh2DP7kAJDr/oPaVia9hpLgbfSKF4hEelD71juAF1lgJI5uXIYfD7NZseKDJNQC1YPNfAxwtgqq5YZP+2k1+Rfejs1pq3tud2p4C//ZlS3T+jBjc4ebrWLO2C97Gzq4y4L9esA+3jvGGZViKzJgviHCuPxffHSXQKBgH4Ey7/2rl6ne1WemDpCv+59bnd/5HnpAAwKrXDfFSnJW1HpHv7xHAlJUK+LiXeO9bJZCiz/JgQ3xESxLzpRavuOLYqVTVLYsHmgVjSvmosnhk0u8US1MZIN047MmoG/t1q202PpN+IMzxhZad902+1GeYodb75pwcV0mwfZ2BBRAoGALsr0yk/GFFbvGHfgaSoLBD7MeAW8VhrYHQuLMOxiIJ85+urjWetSKUpWrHRNEX+Jgkf8hkzxnYdLAJe+97cVNv8Pydemo4ElcG9XjfHnKSpQstpsk+U4Y0TrPgZcilnOy2W22GNU++oB1ibB2THCUmCLo/vLt2phPnjPv7OWGSECgYB+GTzoEdVhYHXCZMbdDvoOWjzTg3pjmiZ4vS7+i35uwMukW4pCroqAPaxH3GVnfqxuU7y5Wpo38QSrmKH00dYP5BB3vp3ITy3wzFSg0ggv6jZVrQu+YhIhbwuFVf2u5qdtIOiyN6Wmud93uRiPyTuUe1MEinEGLNR+ERrok/Djkg==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlDKFKSbmMoQ/Dg/lxkNz9WH+iOx1JtcdokViCM9yV0Qb1iIz+pQQRjGqc1cfrZhwMmFUDrQxx3SKuWebLCWU/IIRb8UBev1yCb8quBsoinXwEWBbaIPnx9jsTW3vJHQ/3//moh6fx7GHR8u6VVPmU/rciGq5a9I0mY1Mtp9/Bs6aISSgSY8Juj6iAVElVpmAFeWAEQVR8t5kqNaOuo1JM0rBmSoX5lxzeuekNLCpfd/jEQhRImXt/8EZz9p174d5wnFpRwQK/PhoCDAE8SESR0sFsZDGKqIvCx5A1PolPNZ2a0+Ppq4d5AQGfjVgpKpjbYCH7BTBIozuilf1So8qowIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://cx-eleme.natapp1.cc/boot/order/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8080/boot/success.html";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    //
    public static String log_path = "D:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /** 
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}