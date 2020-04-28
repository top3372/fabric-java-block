package org.xialing.fabric.context;

import org.xialing.fabric.model.ChannelBean;
import org.hyperledger.fabric.sdk.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leon
 * @version 1.0
 * @date 2020/3/6 13:56
 */
public class ChannelContext {

    private static Map<String, ChannelBean> channelBeanContext = new HashMap<>();

    private static Map<String,String> chainCodeEventHandlerContext = new HashMap<>();

    public static void addChannelContext(String channelName, ChannelBean channelbean){
        channelBeanContext.put(channelName,channelbean);
    }

    public static ChannelBean getChannelContext(String channelName){
        return channelBeanContext.get(channelName);
    }

    public static void removeChannelContext(String channelName){
        channelBeanContext.remove(channelName);
    }

    public static void clearChannelContext(){
        channelBeanContext.clear();}

    public static int sizeForChannelContext(){return channelBeanContext.size();}


    public static void addChainCodeEventHandlerContext(String channelEventName, String handler){
        chainCodeEventHandlerContext.put(channelEventName,handler);
    }

    public static String getChainCodeEventHandlerContext(String channelEventName){
        return chainCodeEventHandlerContext.get(channelEventName);
    }

    public static void removeChainCodeEventHandlerContext(String channelEventName){
        chainCodeEventHandlerContext.remove(channelEventName);
    }

    public static void clearChainCodeEventHandlerContext(){
        chainCodeEventHandlerContext.clear();}

    public static int sizeForChainCodeEventHandlerContext(){return chainCodeEventHandlerContext.size();}


}
