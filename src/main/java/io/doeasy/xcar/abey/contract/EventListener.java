package io.doeasy.xcar.abey.contract;


import java.io.IOException;

/**
 * @author kris.wang
 */
public interface EventListener {

    /**
     * 处理链上事件
     */
    void handle() throws IOException, InterruptedException;
}
