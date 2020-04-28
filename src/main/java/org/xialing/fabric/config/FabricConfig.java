package org.xialing.fabric.config;



import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class FabricConfig {


    private static void checkInit(String hostname, String certPath) {
        if (hostname == null || certPath == null) {
            throw new RuntimeException("Please set host name AND cert path");
        }
    }

    public static Properties getOrderProperties(String hostname, String certPath,String domain) {
        checkInit(hostname,certPath);

        Properties ordererProperties = new Properties();

        ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[]{5L, TimeUnit.MINUTES});
        ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[]{8L, TimeUnit.SECONDS});
        ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[]{true});

        ordererProperties.put("pemBytes", certPath.getBytes(StandardCharsets.UTF_8));
//        ordererProperties.setProperty("pemFile", FileUtils.getResourceFilePath(CERT_PATH));
        ordererProperties.setProperty("hostnameOverride", hostname);
        ordererProperties.setProperty("sslProvider", "openSSL");
        ordererProperties.setProperty("negotiationType", "TLS");

        //配置连接属性
        ordererProperties.setProperty("grpc.NettyChannelBuilderOption.userAgent", "/" + domain);

        return ordererProperties;
    }

    public static Properties getPeerProperties(String hostname, String certPath,String domain) {
        checkInit(hostname,certPath);

        Properties peerProperties = new Properties();
        peerProperties.put("pemBytes", certPath.getBytes(StandardCharsets.UTF_8));
//        peerProperties.setProperty("pemFile", FileUtils.getResourceFilePath(CERT_PATH));
        peerProperties.setProperty("hostnameOverride", hostname);
        peerProperties.setProperty("sslProvider", "openSSL");
        peerProperties.setProperty("negotiationType", "TLS");

        //配置连接属性
        peerProperties.setProperty("grpc.NettyChannelBuilderOption.userAgent", "/" + domain);

        return peerProperties;
    }

}
