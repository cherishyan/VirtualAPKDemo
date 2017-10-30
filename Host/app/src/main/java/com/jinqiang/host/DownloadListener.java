package com.jinqiang.host;

/**
 * @autor: jinqiang
 * @time: 2017/10/27 15:49
 */

public interface DownloadListener {
    void state(long bytesRead, long contentLength, boolean done);
}
