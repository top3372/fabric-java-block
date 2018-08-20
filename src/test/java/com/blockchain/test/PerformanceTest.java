package com.blockchain.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;


public class PerformanceTest {

    String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzNDMiLCJyb2xlcyI6InVzZXIiLCJpYXQiOjE1MjkzODg1OTMsImV4cCI6MTUyOTM5NzU5M30.O3NTCKpEs4H9qXvXV9hP6MVu3mEhXBX7tgd9Shb75_Q";

    final static SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //final static  String baseUrl = "http://192.168.20.68:8031/";

    final static String baseUrl = "http://localhost:8031/";

    Set<DataModel> dataSet = new HashSet<DataModel>();
    ConcurrentLinkedQueue<DataModel> queue;

    static AtomicInteger scCount = new AtomicInteger(0); //成功的

    static AtomicInteger flCount = new AtomicInteger(0); // 插入失败的

    ConcurrentLinkedQueue<String> errQ = new ConcurrentLinkedQueue<String>();

    int thCount = 50;
    int dataCount = 100;

    {

        for (int i = 0; i < dataCount; i++) {
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String dateString = formatter.format(currentTime);
            String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            StringBuilder sb = new StringBuilder(6);
            for (int k = 0; k < 6; k++) {
                char ch = str.charAt(new Random().nextInt(str.length()));
                sb.append(ch);
            }
            String adName = "ad" + dateString + sb.toString();
            // System.out.println(adName);
            dataSet.add(new DataModel(adName, "ssp" + (i % 3 + 1), ft.format(new Date()), "pv", i));
        }

        queue = new ConcurrentLinkedQueue<DataModel>(dataSet);
    }


    /**
     * post请求（用于请求json格式的参数）
     *
     * @param url
     * @param params
     * @return
     */
    public String doPost(String url, String params, String Authorization) throws Exception {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        if (Authorization != null) {
            httpPost.setHeader("Authorization", Authorization);
        }
        String charSet = "UTF-8";
        StringEntity entity = new StringEntity(params, charSet);
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                String jsonString = EntityUtils.toString(responseEntity);
                return jsonString;
            } else {
                System.err.println("请求返回:" + state + "(" + url + ")");
            }
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //    void initChainCode(BufferedWriter bw ) throws IOException  {
//        int ww = 111;
//        for (int i = ww; i < 151; i++) {
//            long s = System.currentTimeMillis();
//            try {
//                createChannel(i);
//            } catch (Exception e) {
//                System.err.println("通道ad"+i + " 创建失败！！！！");
//                bw.write("通道ad"+i + " 创建失败！！！！");
//                bw.newLine();
//                e.printStackTrace();
//                break;
//            }
//            try {
//                installChaincode(i);
//            } catch (Exception e) {
//                System.err.println("合约 ad "+i + " 安装失败！！！！");
//                bw.write("合约 ad "+i + " 安装失败！！！！");
//                bw.newLine();
//                e.printStackTrace();
//                break;
//
//            }
//            try {
//                instantiateChaincode(i);
//            } catch (Exception e) {
//                System.err.println("合约 ad "+i + " 初始化失败！！！！");
//                bw.write("合约 ad "+i + " 初始化失败！！！！");
//                bw.newLine();
//                e.printStackTrace();
//               break;
//            }
//            bw.write("合约ad "+i + "初始化耗时："+(System.currentTimeMillis()-s));
//            bw.newLine();
//        }
//    }
    @SuppressWarnings("unused")
    public static void main(String[] args) throws Exception {
        PerformanceTest performanceTest = new PerformanceTest();
        System.out.println(performanceTest.dataSet.size());
//      performanceTest.enroll();
//        if (false){
//            FileWriter writer = new FileWriter("initChainCode.txt",true);
//            BufferedWriter bw = new BufferedWriter(writer);
//            bw.newLine();
//            bw.write(ft.format(new Date()));
//            bw.newLine();
//
//            performanceTest.initChainCode(bw);
//
//            bw.close();
//            writer.close();
//        }else{

        FileWriter writer = new FileWriter("inserttime.txt", true);
        BufferedWriter bw = new BufferedWriter(writer);
        bw.newLine();
        bw.write("开始插入时间：" + ft.format(new Date()));
        bw.newLine();
        performanceTest.invokeChaincode(bw, performanceTest.thCount);
        bw.close();
        writer.close();
//        }
    }

//    private String enroll() throws Exception {
////        String url = "http://localhost:8031/enroll";
//        String url = baseUrl+"enroll";
//        String params = "{\n" +
//                "  \"passWord\": \"chenyong\",\n" +
//                "  \"peerWithOrgs\": [\n" +
//                "    \"peerOrg1\"\n" +
//                "  ],\n" +
//                "  \"userName\": \"chenyong\"\n" +
//                "}";
//        if (!token.startsWith("Bearer ")) {
//            String ret = doPost(url, params, null);
//            JSONObject jsonObject = JSONObject.parseObject(ret);
//            if ("000000".equals(jsonObject.getString("code"))) {
//                token = jsonObject.getString("data");
//            } else {
//                throw new Exception(jsonObject.getString("msg"));
//            }
//        }
//        return token;
//    }

//    private void createChannel(int i) throws Exception {
//        String url = baseUrl+"api/construct";
//
//        String params = "{\"channelName\": \"ad" + i + "\", \"peerWithOrg\": \"peerOrg1\"}";
//        System.out.println(params);
//        String ret = doPost(url, params, token);
//
//        JSONObject jsonObject = JSONObject.parseObject(ret);
//        if ("000000".equals(jsonObject.getString("code"))) {
//            System.out.println("ad" + i + " 通道创建成功！");
//        } else {
//            throw new Exception(jsonObject.getString("msg"));
//        }
//        System.out.println(ret);
//
//    }
//
//    private void installChaincode(int i) throws Exception {
//        String url = baseUrl+"api/install";
//
//        String param = "{\n" +
//                "  \"chainCodeName\": \"ad"+i+"\",\n" +
//                "  \"chainCodeVersion\": \"1.0\",\n" +
//                "  \"channelName\": \"ad"+i+"\",\n" +
//                "  \"peerWithOrgs\": [\n" +
//                "         \"peerOrg1\"\n" +
//                "    ]\n" +
//                "  }\n" +
//                "";
//        System.out.println(param);
//        String ret = doPost(url, param, token);
//
//        JSONObject jsonObject = JSONObject.parseObject(ret);
//        if ("000000".equals(jsonObject.getString("code"))) {
//            System.out.println("ad" + i + " 安装合约成功！");
//        } else {
//            System.err.println("ad" + i + " 安装合约失败！");
//            throw new Exception(jsonObject.getString("msg"));
//        }
//
//    }
//
//    private void instantiateChaincode(int i) throws Exception {
//        String url = baseUrl+"api/instantiate";
//
//            String param = "{ \"args\": [ \"\" ],\n" +
//                    "  \"chainCodeName\": \"ad"+i+"\",\n" +
//                    "  \"chainCodeVersion\": \"1.0\",\n" +
//                    "  \"channelName\": \"ad"+i+"\",\n" +
//                    "  \"function\": \"\",\n" +
//                    "  \"peerWithOrgs\": [\n" +
//                    "         \"peerOrg1\"\n" +
//                    "  ]}\n";
//
//            System.out.println(param);
//            String ret = doPost(url, param, token);
//
//            JSONObject jsonObject = JSONObject.parseObject(ret);
//            if ("000000".equals(jsonObject.getString("code"))) {
//                System.out.println("ad" + i + " 实例化成功！");
//            } else {
//                System.err.println("ad" + i + " 实例化失败！");
//                throw new Exception(jsonObject.getString("msg"));
//            }
//    }

    class InvokeTask implements Runnable {

        String taskName;

        public InvokeTask(String taskName) {
            super();
            this.taskName = taskName;
        }

        public void run() {
            {
                while (!queue.isEmpty()) {
                    System.out.println("task: " + taskName + " 余下数据量：" + queue.size());
                    DataModel data = queue.poll();
                    if (data == null) {
                        break;
                    }
                    try {
                        invoke(taskName, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void invokeChaincode(BufferedWriter bw, int threadCount) throws IOException {

        errQ.clear();

        ExecutorService executor = (ExecutorService) Executors.newFixedThreadPool(threadCount);

        long starttime = System.currentTimeMillis();
        try {
            for (int i = 0; i < threadCount; i++) {
                executor.submit(new InvokeTask("task_" + i));
            }

            if (executor != null) {
                executor.shutdown();
                while (true) {
                    if (executor.isTerminated()) {
                        break;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            long endtime = System.currentTimeMillis();
            long ss = endtime - starttime;
            bw.write(threadCount + " 个线程插入 " + dataSet.size() + " 条数据耗时：" + ss + "。" + "插入成功：" + scCount + " 条 ; 失败：" + flCount + " 条");
            bw.newLine();

            if (!errQ.isEmpty()) {
                FileWriter writer = new FileWriter("errData.txt", true);
                BufferedWriter bw1 = new BufferedWriter(writer);
                bw1.write(threadCount + " 个线程插入 " + dataSet.size() + " 条数据耗时：" + ss);
                bw1.newLine();
                while (!errQ.isEmpty()) {
                    String data = errQ.poll();
                    if (data == null) {
                        break;
                    }
                    bw1.write(data);
                    bw1.newLine();
                }
                bw1.close();
                writer.close();
            }

        }
    }

    private void invoke(String taskName, DataModel data) throws Exception {
        String url = baseUrl + "api/invoke";
        String params = "{\n" +
                "  \"args\": [\n" +
                "   \"submit\", \"" + data.adName + "\",\"" + data.toString() + "\"\n" +
                "  ],\n" +
                "  \"chainCodeName\": \"haikou\",\n" +
                "  \"chainCodeVersion\":  \"1.0\",\n" +
                "  \"channelName\": \"mychannel\",\n" +
                "  \"function\": \"invoke\",\n" +
                "  \"peerWithOrgs\": [ \"peerOrg1\"]\n" +
                "}\n" +
                "";


//        System.out.println(taskName + "=========" + data.adName);
//        System.out.println(params);
//        System.out.println("数据序号："+data.radom);

        String ret = doPost(url, params, token);

        JSONObject jsonObject = JSONObject.parseObject(ret);
        if ("000000".equals(jsonObject.getString("code"))) {
            System.out.println(data.adName + " 合约调用成功！ ");
            scCount.addAndGet(1);
        } else {
            System.err.println(data.adName + " 合约调用失败！");
            flCount.addAndGet(1);
            errQ.offer(data.toString() + "==>" + jsonObject.getString("msg"));
            throw new Exception(jsonObject + " " + data.toString());
        }
    }
}
