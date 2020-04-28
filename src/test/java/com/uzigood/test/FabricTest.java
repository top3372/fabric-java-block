package org.xialing.test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author leon
 * @version 1.0
 * @date 2020/3/3 14:41
 */
public class FabricTest {
    public static void main(String[] args) {

        String byteString = Arrays.toString("11111".getBytes(StandardCharsets.UTF_8));
        System.out.println(byteString);
        Properties properties = new Properties();
        properties.put("pemBytes", "11111".getBytes(StandardCharsets.UTF_8));
        byte[] permbytes = (byte[]) properties.get("pemBytes");
        System.out.println(new String(permbytes));


    }
}
