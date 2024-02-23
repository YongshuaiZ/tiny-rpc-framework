package com.ltyzzz.core.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static com.ltyzzz.core.constants.RpcConstants.MAGIC_NUMBER;

public class RpcDecoder extends ByteToMessageDecoder {

    /**
     * 协议开头部分的标准长度
     */
    public final int BASE_LENGTH = 2 + 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        if (byteBuf.readableBytes() >= BASE_LENGTH) {
            // 不是魔数开头，说明是非法的客户端发来的数据包
            if (!(byteBuf.readShort() == MAGIC_NUMBER)) {
                ctx.close();
                return;
            }
            // 说明剩余的数据包不是完整的，这里需要重置下读索引
            int length = byteBuf.readInt();
            if (byteBuf.readableBytes() < length) {
                ctx.close();
                return;
            }
            // 这里其实就是实际的RpcProtocol对象的content字段
            byte[] body = new byte[length];
            byteBuf.readBytes(body);
            RpcProtocol rpcProtocol = new RpcProtocol(body);
            out.add(rpcProtocol);
        }
    }
}
