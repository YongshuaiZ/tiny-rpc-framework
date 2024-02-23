package com.ltyzzz.core.client;

import com.ltyzzz.core.common.RpcInvocation;
import com.ltyzzz.core.common.RpcProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import static com.ltyzzz.core.cache.CommonClientCache.CLIENT_SERIALIZE_FACTORY;
import static com.ltyzzz.core.cache.CommonClientCache.RESP_MAP;

/**
 *  客户端响应类
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 此时msg已经被解码，直接转为RpcProtocol对象即可
        RpcProtocol rpcProtocol = (RpcProtocol) msg;
        byte[] content = rpcProtocol.getContent();
        // 将RpcProtocol中的内容 content 反序列化
        RpcInvocation rpcInvocation = CLIENT_SERIALIZE_FACTORY.deserialize(content, RpcInvocation.class);
        if (rpcInvocation.getE() != null) {
            rpcInvocation.getE().printStackTrace();
        }
        //如果是单纯异步模式的话，响应Map集合中不会存在映射值
        Object r = rpcInvocation.getAttachments().get("async");
        if (r != null && Boolean.valueOf(String.valueOf(r))) {
            ReferenceCountUtil.release(msg);
            return;
        }
        //通过之前发送的uuid来注入匹配的响应数值
        if (!RESP_MAP.containsKey(rpcInvocation.getUuid())) {
            throw new IllegalArgumentException("server response is error!");
        }
        //将请求的响应结果放入一个Map集合中，集合的key就是uuid，这个 uuid 在发送请求之前就已经初始化好了，所以只需要起一个线程在后台遍历这个map，查看对应的key是否有相应即可。
        //uuid何时放入到map？其实放入的操作我将它封装到了代理类中进行实现
        RESP_MAP.put(rpcInvocation.getUuid(), rpcInvocation);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        }
    }
}
