package com.ltyzzz.core.event;

import com.ltyzzz.core.client.ConnectionHandler;
import com.ltyzzz.core.common.ChannelFutureWrapper;
import com.ltyzzz.core.registry.URL;
import com.ltyzzz.core.registry.zookeeper.ProviderNodeInfo;
import com.ltyzzz.core.router.Selector;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ltyzzz.core.cache.CommonClientCache.CONNECT_MAP;
import static com.ltyzzz.core.cache.CommonClientCache.IROUTER;

/**
 * zk的服务提供者节点发生了变更，需要发送事件通知操作的目的是什么？
 *
 * 这个时候客户端需要更新本地的一个目标服务列表，避免向无用的服务发送请求
 */
public class ServiceUpdateListener implements IRpcListener<IRpcUpdateEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceUpdateListener.class);

    @Override
    public void callBack(Object t) {
        URLChangeWrapper urlChangeWrapper = (URLChangeWrapper) t;
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(urlChangeWrapper.getServiceName());
        List<String> matchProviderUrl = urlChangeWrapper.getProviderUrl();
        Set<String> finalUrl = new HashSet<>();
        List<ChannelFutureWrapper> finalChannelFutureWrappers = new ArrayList<>();
        for (ChannelFutureWrapper channelFutureWrapper : channelFutureWrappers) {
            String oldServerAddress = channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort();
            if (matchProviderUrl.contains(oldServerAddress)) {
                finalChannelFutureWrappers.add(channelFutureWrapper);
                finalUrl.add(oldServerAddress);
            }
        }

        //此时老的url已经被移除了，开始检查是否有新的url
        //ChannelFutureWrapper其实是一个自定义的包装类，将netty建立好的ChannelFuture做了一些封装

        List<ChannelFutureWrapper> newChannelFutureWrapper = new ArrayList<>();
        for (String newProviderUrl : matchProviderUrl) {
            if (!finalUrl.contains(newProviderUrl)) {
                ChannelFutureWrapper wrapper = new ChannelFutureWrapper();
                String host = newProviderUrl.split(":")[0];
                Integer port = Integer.valueOf(newProviderUrl.split(":")[1]);
                wrapper.setHost(host);
                wrapper.setPort(port);
                String urlStr = urlChangeWrapper.getNodeDataUrl().get(newProviderUrl);
                ProviderNodeInfo providerNodeInfo = URL.buildURLFromUrlStr(urlStr);
                wrapper.setWeight(providerNodeInfo.getWeight());
                wrapper.setGroup(providerNodeInfo.getGroup());
                ChannelFuture channelFuture = null;
                try {
                    channelFuture = ConnectionHandler.createChannelFuture(host, port);
                    wrapper.setChannelFuture(channelFuture);
                    newChannelFutureWrapper.add(wrapper);
                    finalUrl.add(newProviderUrl);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        finalChannelFutureWrappers.addAll(newChannelFutureWrapper);
        CONNECT_MAP.put(urlChangeWrapper.getServiceName(), finalChannelFutureWrappers);
        Selector selector = new Selector();
        selector.setProviderServiceName(urlChangeWrapper.getServiceName());
        IROUTER.refreshRouteArr(selector);
    }
}
