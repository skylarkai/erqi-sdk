package cn.skylarkai.sdk;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;


 
/**
* 上报统计类
*
* @version 1.0.0
* @since jdk1.8
* @author open.erqikefu.com
*
*/
public class SnsStat
{
    // 上报服务器的Name
    private static final String STAT_SVR_NAME = "skylarkai.cn";
    // 上报服务器的端口
    private static final int STAT_SVR_PORT = 9998;

    /** 
     * 统计上报
     *
     * @param startTime 请求开始时间(毫秒单位)
     * @param serverName 服务器名称
     * @param method POST/GET
     * @param protocol 协议
     * @param scriptName scriptname
     * @param rc 次数
     * @param params 上报参数
     */
    public static void statReport(
            long startTime, 
			String serverName,
            HashMap<String, Object> params,
            String method, 
            String protocol,
            int rc,
			String scriptName
            ) 
    {
        try
        {

        // 统计时间
        long endTime = System.currentTimeMillis();
        double timeCost = (endTime - startTime) / 1000.0;

        // 转化为json
        String sendStr = String.format("{\"appid\":%s, \"pf\":%s,\"rc\":%d,\"svr_name\":\"%s\",\"local_ip\":\"%s\", \"interface\":\"%s\",\"protocol\":\"%s\",\"method\":\"%s\",\"time\":%.4f,\"timestamp\":%d,\"collect_point\":\"sdk-java-v1\"}",
                params.get("appid"),
                params.get("pf"),
                rc,
                serverName,
                InetAddress.getLocalHost().getHostAddress(),
				scriptName,
                protocol,
                method,
                timeCost,
                endTime
                );

            // UDP上报
            DatagramSocket client = new DatagramSocket();
            byte[] sendBuf =  sendStr.getBytes();

            // 获取实际上报IP
            String reportSvrIp = STAT_SVR_NAME;
            int reportSvrport = STAT_SVR_PORT;

            InetAddress addr = InetAddress.getByName(reportSvrIp);
            DatagramPacket sendPacket
                = new DatagramPacket(sendBuf, sendBuf.length, addr, reportSvrport);

            client.send(sendPacket);
        }
        catch(Exception e)
        {
        }
    }

}
