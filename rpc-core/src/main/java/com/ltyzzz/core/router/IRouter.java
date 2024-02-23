package com.ltyzzz.core.router;

import com.ltyzzz.core.common.ChannelFutureWrapper;
import com.ltyzzz.core.registry.URL;

public interface IRouter {

    /**
     * 刷新路由数组
     *
     * @param selector
     */
    void refreshRouteArr(Selector selector);

    /**
     * 获取到请求到连接通道
     *
     * @return
     */
    ChannelFutureWrapper select(Selector selector);

    /**
     * 更新权重信息
     *
     * @param url
     */
    void updateWeight(URL url);
}
