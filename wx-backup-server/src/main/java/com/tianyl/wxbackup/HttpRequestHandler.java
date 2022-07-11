package com.tianyl.wxbackup;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        if (HttpUtil.is100ContinueExpected(req)) {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.CONTINUE,
                    Unpooled.EMPTY_BUFFER);
            ctx.write(response);
            return;
        }
        String uri = req.uri();
        if (uri.startsWith("/ui")) {
            renderUI(ctx, req);
            return;
        }

        System.out.println(uri);
        System.out.println(req.method().name());
        String msg = "<html><head><title>test</title></head><body>你请求uri为：" + uri+"</body></html>";
        // 创建http响应
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        // 设置头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        // 将html write到客户端
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void renderUI(ChannelHandlerContext ctx, FullHttpRequest req) {
        String uri = req.uri().substring(3);
        String fileName = "";
        if (uri.length() == 0 || uri.equals("/")) {
            fileName = "/index.html";
        } else {
            fileName = uri;
        }
        File file = new File(Config.UI_DIR + fileName);
        if (!file.exists()) {
            logger.error("file not found:" + file.getAbsolutePath());
            writeAndFlush(ctx, HttpResponseStatus.NOT_FOUND, "404:" + req.uri());
        } else {
            try {
                byte[] bs = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                writeAndFlush(ctx, new String(bs, StandardCharsets.UTF_8));
            } catch (IOException e) {
                logger.error("文件读取失败:" + file.getAbsolutePath(), e);
                writeAndFlush(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, "server error:" + e.getMessage());
            }
        }
    }

    private void writeAndFlush(ChannelHandlerContext ctx, String msg) {
        writeAndFlush(ctx, HttpResponseStatus.OK, msg);
    }

    private void writeAndFlush(ChannelHandlerContext ctx, HttpResponseStatus status, String msg) {
        // 创建http响应
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        // 设置头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        // 将html write到客户端
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("出现异常");
        cause.printStackTrace();
        ctx.close();
    }

}
