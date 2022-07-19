package com.tianyl.wxbackup;

import com.tianyl.wxbackup.core.Utils;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

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
        if (uri.startsWith("/api")) {
            executeApi(ctx, req);
            return;
        }
        writeAndFlush(ctx, HttpResponseStatus.NOT_FOUND, "404:" + uri);
    }

    private void executeApi(ChannelHandlerContext ctx, FullHttpRequest req) {
        String path = req.uri().substring(4);
        URI uri = Utils.getURI(path);
        Method method = findExeMethod(uri.getPath().replace("/", ""));
        if (method == null) {
            writeAndFlush(ctx, HttpResponseStatus.NOT_FOUND, "404:" + req.uri());
            return;
        }
        Map<String, String> param = Utils.decodeQuery(req.uri());
//        System.out.println("param:" + param);
        Object[] methodParams = parseMethodParam(method, param);

        try {
            Object result = method.invoke(ApiRequestHandler.getInstance(), methodParams);
            writeJson(ctx, Result.success(result));
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("method invoke error", e);
            writeJson(ctx, Result.fail(e.getMessage()));
        }

//        HttpHeaders headers = req.headers();
//        for (Map.Entry<String, String> entry : headers) {
//            System.out.println(entry.getKey() + ":" + entry.getValue());
//        }

//        ByteBuf byteBuf = req.content();
//        if (byteBuf != null) {
//            if (byteBuf.isReadable()) {
//                System.out.println("body:" + byteBuf.toString(StandardCharsets.UTF_8));
//            }
//        }

//        writeJson(ctx, "ok");
    }

    private Object[] parseMethodParam(Method method, Map<String, String> param) {
        Class<?>[] classes = method.getParameterTypes();
        Object[] result = new Object[classes.length];
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < classes.length; i++) {
            Class<?> clazz = classes[i];
            Parameter parameter = parameters[i];
            String value = param.get(parameter.getName());
            if (clazz == String.class) {
                result[i] = value;
            } else if (clazz == Integer.class) {
                if (Utils.isEmpty(value)) {
                    result[i] = null;
                } else {
                    result[i] = Integer.parseInt(value.trim());
                }
            } else if (clazz == int.class) {
                if (Utils.isEmpty(value)) {
                    result[i] = 0;
                } else {
                    result[i] = Integer.parseInt(value.trim());
                }
            } else if (clazz == Long.class) {
                if (Utils.isEmpty(value)) {
                    result[i] = null;
                } else {
                    result[i] = Long.valueOf(value.trim());
                }
            } else if (clazz == long.class) {
                if (Utils.isEmpty(value)) {
                    result[i] = 0L;
                } else {
                    result[i] = Long.parseLong(value.trim());
                }
            } else {
                throw new RuntimeException("不支持的参数类型:" + clazz.getName());
            }
        }
        return result;
    }

    private Method findExeMethod(String methodName) {
        Method[] methods = ApiRequestHandler.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
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

    private void writeJson(ChannelHandlerContext ctx, Object obj) {
        String msg = Utils.toJSONString(obj);
        writeAndFlush(ctx, HttpResponseStatus.OK, msg, "application/json; charset=UTF-8");
    }

    private void writeAndFlush(ChannelHandlerContext ctx, String msg) {
        writeAndFlush(ctx, HttpResponseStatus.OK, msg);
    }

    private void writeAndFlush(ChannelHandlerContext ctx, HttpResponseStatus status, String msg) {
        writeAndFlush(ctx, status, msg, "text/html; charset=UTF-8");
    }

    private void writeAndFlush(ChannelHandlerContext ctx, HttpResponseStatus status, String msg, String contentType) {
        // 创建http响应
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        // 设置头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
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
