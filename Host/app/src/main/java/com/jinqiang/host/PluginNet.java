package com.jinqiang.host;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @autor: jinqiang
 * @time: 2017/10/24 11:41
 */

public interface PluginNet {
    //下载大理地图apk
    @Streaming
    @GET
    Observable<ResponseBody> downloadDaliMapPlugin(@Url String url);
}
