package com.jinqiang.host;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.didi.virtualapk.PluginManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @autor: jinqiang
 * @time: 2017/10/27 14:29
 */

public class PluginService extends Service {
    private static final String TAG = "PluginService";

    private String baseUrl = "http://192.168.199.114:8889/";
    private String apkUrl = "com.jinqiang.dalimap.apk";
    private File outputFile;
    private String path = "";
    private Retrofit retrofit;
    private DownloadListener listener;
    OkHttpClient client;

//    public PluginService() {
//        super("PluginService");
//    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.v(TAG,"onstart");
        initFile();
        initListener();
        initNetModel();
        downloadMapPlugin();
    }


//    @Override
//    public void onCreate() {
//        super.onCreate();
//    }

//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//        initFile();
//        initListener();
//        initNetModel();
//        downloadMapPlugin();
//    }

    private void initListener() {
        listener = new DownloadListener() {
            @Override
            public void state(long bytesRead, long contentLength, boolean done) {
                String size = getDataSize(bytesRead) + "/" + getDataSize(contentLength);
                Log.v(TAG,size);

            }
        };
    }


    private void initNetModel() {
        DownloadInterceptor interceptor = new DownloadInterceptor(listener);
        client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    private void initFile() {
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CityBao/" + getResources().getString(R.string.app_name);
        Log.v(TAG,path);
        outputFile = new File(path, "pluginMap.apk");
    }

    private void downloadMapPlugin() {
        if (outputFile.exists()) {
            outputFile.delete();
        }
//        OkHttpClient client = new OkHttpClient.Builder()
//                .retryOnConnectionFailure(true)
//                .build();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .client(client)
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .build();

        PluginNet pluginNet = retrofit.create(PluginNet.class);
        pluginNet.downloadDaliMapPlugin(apkUrl)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Func1<ResponseBody, InputStream>() {
                    @Override
                    public InputStream call(ResponseBody responseBody) {
                        return responseBody.byteStream();
                    }
                })
                .observeOn(Schedulers.computation())
                .doOnNext(new Action1<InputStream>() {
                    @Override
                    public void call(InputStream inputStream) {
                        writeInputStreamFile(outputFile,inputStream);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<InputStream>() {
                    @Override
                    public void onCompleted() {
                        initPluginAPK();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(InputStream inputStream) {

                    }
                });

    }

    public void writeInputStreamFile(File file, InputStream input){
        FileOutputStream outputStream = null;
        if (file != null && file.exists())
            file.delete();
        try {
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 4];
            int temp=-1;
            while ((temp = input.read(buffer))!=-1){
                outputStream.write(buffer,0,temp);
            }
            outputStream.flush();

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initPluginAPK(){
        PluginManager pluginManager = PluginManager.getInstance(this);
        File apk = outputFile;
        if (apk.exists()) {
            try {
                pluginManager.loadPlugin(apk);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(this, "SDcard目录未检测到插件", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getDataSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("###.00");
        return var0 < 1024L ? var0 + "bytes" : (var0 < 1048576L ? var2.format((double) ((float) var0 / 1024.0F))
                + "KB" : (var0 < 1073741824L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F))
                + "MB" : (var0 < 0L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F / 1024.0F))
                + "GB" : "error")));
    }
}
