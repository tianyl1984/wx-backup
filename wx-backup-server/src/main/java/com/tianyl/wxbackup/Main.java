package com.tianyl.wxbackup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        logger.info("start...");
        HttpServer httpServer = new HttpServer(8080);
        httpServer.start();
    }

}
