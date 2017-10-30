package com.jinqiang.host;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @autor: jinqiang
 * @time: 2017/10/27 16:13
 */

public class DownloadInterceptor implements Interceptor{
    private DownloadListener listener;

    public DownloadInterceptor(DownloadListener listener){
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new DownloadResponseBody(originalResponse.body(), listener))
                .build();
    }
}
