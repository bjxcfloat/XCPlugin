package xc.lib.common.httpclient.test;

import android.util.Log;
import android.widget.Toast;

import xc.lib.common.httpclient.Bridge;
import xc.lib.common.httpclient.CallBack;
import xc.lib.common.httpclient.Cancelable;
import xc.lib.common.httpclient.OkHttpClient;
import xc.lib.common.httpclient.RequestInfo;
import xc.lib.common.httpclient.json.GsonStrategy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.RequestBody;


public class TestOkHttp {

    public static class TestBean {
        public TestBean() {
        }

        public String h1 = "";
    }

    public   void test() {

        Log.e("a", "net req start");
        HashMap<String,String> asd = new HashMap<>();
        asd.put("h1","123123123");

        OkHttpClient clint = new OkHttpClient();
        clint.setReadTimeout(60);
        clint.setConnectTimeout(60);
        clint.setCallTimeout(60);
        clint.setHeaders(asd);
        clint.init();

        Bridge.Builder builder = new Bridge.Builder().setHttpClient(clint)
                .setJsonStrategy(new GsonStrategy()).setBaseUrl("https://www.baidu.com");
        Bridge bridge = builder.build();

        LoginService serv = Bridge.getInstance().createService(LoginService.class,this);

        File file1 = new File("");
        File file2 = new File("");

//        RequestBody.create()
        Cancelable cancelable = serv.login(new File[]{file1,file2}, 1,"123312",
                new CallBack<String>() {
                    @Override
                    public void onFailure(Exception ex) {


                        Log.e("a", "net req falures-"+ex.toString());
                    }

                    @Override
                    public void onSuccess(String testBean) {
                        Log.e("a", "net req success-" + testBean);
                    }
                });

//        RequestInfo renfo = new RequestInfo();
//        renfo.baseUrl = "https://www.baidu.com";
//        clint.request(renfo, new CallBack<TestBean>() {
//            @Override
//            public void onFailure(Exception ex) {
//
//                Log.e("a","net req falures");
//            }
//
//            @Override
//            public void onSuccess(TestBean testBean) {
//                Log.e("a","net req success-"+testBean.h1);
//            }
//        });

    }


}
