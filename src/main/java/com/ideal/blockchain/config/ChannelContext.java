package com.ideal.blockchain.config;

import org.hyperledger.fabric.sdk.Channel;

/**
 * @author Ma
 */
public class ChannelContext {

    private static ThreadLocal<Channel> holder = new ThreadLocal<>();

    public static void set(Channel channel){
        holder.set(channel);
    }

    public static Channel get(){
        return holder.get();
    }

    public static void clear(){
        holder.remove();
    }
}