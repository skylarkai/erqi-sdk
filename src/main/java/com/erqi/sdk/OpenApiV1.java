package com.erqi.sdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;
import org.apache.commons.httpclient.methods.multipart.FilePart;

/**
 * OpenApiV1
 *
 * @author chuhl
 * @date 2018/12/28
 */
public class OpenApiV1 {
    private String appid;
    private String appkey;
    private String serverName;
    private String appsecret;
    private String accessToken;


    /**
     * 构造函数
     *
     * @param appid 应用的ID
     * @param appkey 应用的密钥
     */
    public OpenApiV1(String appid, String appkey, String appsecret)
    {
        this.appid = appid;
        this.appkey = appkey;
        this.appsecret = appsecret;
    }

    /**
     * 设置OpenApi服务器的地址
     *
     * @param serverName OpenApi服务器的地址
     */
    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    /**
     * 设置accesstoken
     *
     * @param accessToken accesstoken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = "Bearer "+accessToken;
    }
    /**
     * 获取access_token
     *
     * @param params (client_id、client_secret、grant_type、username、password)
     * @param protocol
     * @return
     * @throws OpensnsException
     */
    public String getToken(HashMap<String, Object> params,String protocol) throws OpensnsException {
        StringBuilder sb = new StringBuilder(64);
        sb.append(protocol).append("://").append(this.serverName).append("/uaa/oauth/token");
        String url = sb.toString();
        params.put( "grant_type","password" );
        String respToken =   SnsNetwork.postRequest(url,params,null,protocol);
        return respToken;
    }

    /**
     * checkToken
     *
     * @param params (token)
     * @param protocol
     * @return
     */
    public String checkToken(HashMap<String, Object> params,String protocol){
        StringBuilder sb = new StringBuilder(64);
        sb.append(protocol).append("://").append(this.serverName).append("/uaa/oauth/check_token");
        String url = sb.toString();
        String respToken = null;
        try {
            respToken = SnsNetwork.postRequest(url,params,null,protocol);
        } catch (OpensnsException e) {
            e.printStackTrace();
        }
        return respToken;
    }

    /**
     * refreshToken
     *
     * 请求所需参数：grant_type、refresh_token、client_id、client_secret 其中grant_type为固定值：grant_type=refresh_token
     *
     * @param params (refresh_token,client_id,client_secret)
     * @param protocol
     * @return
     */
    public String refreshToken(HashMap<String, Object> params,String protocol){
        StringBuilder sb = new StringBuilder(64);
        sb.append(protocol).append("://").append(this.serverName).append("/uaa/oauth/token");
        String url = sb.toString();
        String respToken = null;
        params.put( "grant_type","refresh_token" );
        try {
            respToken = SnsNetwork.postRequest(url,params,null,protocol);
        } catch (OpensnsException e) {
            e.printStackTrace();
        }
        return respToken;
    }

    /**
     * 执行API调用
     *
     * @param scriptName OpenApi CGI名字 ,如/usertenant/user/getUser
     * @param params OpenApi的参数列表
     * @param protocol HTTP请求协议 "http" / "https"
     * @return 返回服务器响应内容
     */
    public String api(String method,String scriptName, HashMap<String, Object> params, String protocol) throws OpensnsException
    {
        ReturnMap ret = new ReturnMap();
        if("".equals(accessToken ) || accessToken.isEmpty() ){
            ret.setFlg( 1 );
            ret.setMessage( "ACCESS_TOKEN 不能为空！" );
            return ret.ReturnMap();
        }else {
            // 无需传sig,会自动生成
            params.remove("sig");
            // 签名密钥
            String secret = this.appkey + "&";
            // 添加固定参数
            params.put( "appid", this.appid );

            // 计算签名
            String sig = SnsSigCheck.makeSig( method, scriptName, params, secret );

            params.put( "sig", sig );

            StringBuilder sb = new StringBuilder( 64 );
            sb.append( protocol ).append( "://" ).append( this.serverName ).append( scriptName );
            String url = sb.toString();

            // cookie
            HashMap<String, String> cookies = null;
            params.put( "access_token", accessToken );
            // 发送请求
            String resp = "";
            printRequest(url,method,params);
            if ("get".equals( method )) {
                resp = SnsNetwork.getRequest( url, params, cookies, protocol );
            } else {
                resp = SnsNetwork.postRequest( url, params, cookies, protocol );
            }
            long startTime = System.currentTimeMillis();
            int rc = 0;
            if(!"".equals( resp ) ){
                JSONObject jsonObject = JSONObject.fromObject( resp );
                rc= Integer.valueOf( jsonObject.get( "code" ).toString() ) ;
            }
            // 统计上报
            SnsStat.statReport(startTime, serverName, params, method, protocol, rc,scriptName);
            //通过调用以下方法，可以打印出调用openapi请求的返回码以及错误信息，默认注释
            printRespond(resp);

            return resp;
        }
    }


    /**
     * 执行API调用
     *
     * @param scriptName OpenApi CGI名字 ,如/usertenant/user/getUser
     * @param params OpenApi的参数列表
     * @param fp 上传的文件
     * @param protocol HTTP请求协议 "http" / "https"
     * @return 返回服务器响应内容
     */
    public String apiUploadFile(String method,String scriptName, HashMap<String, Object> params, FilePart fp, String protocol) throws OpensnsException
    {

        ReturnMap ret = new ReturnMap();
        if("".equals(accessToken ) || accessToken.isEmpty() ){
            ret.setFlg( 1 );
            ret.setMessage( "ACCESS_TOKEN 不能为空！" );
            return ret.ReturnMap();
        }else {
            // 无需传sig,会自动生成
            params.remove("sig");

            // 添加固定参数
            params.put( "appid", this.appid );
            // 签名密钥
            String secret = this.appkey + "&";

            // 计算签名
            String sig = SnsSigCheck.makeSig( method, scriptName, params, secret );

            params.put( "sig", sig );

            StringBuilder sb = new StringBuilder( 64 );
            sb.append( protocol ).append( "://" ).append( this.serverName ).append( scriptName );
            String url = sb.toString();

            // cookie
            HashMap<String, String> cookies = null;
            //通过调用以下方法，可以打印出最终发送到openapi服务器的请求参数以及url，默认注释
            printRequest(url,method,params);
            params.put( "access_token", accessToken );
            // 发送请求
            String resp = SnsNetwork.postRequestWithFile( url, params, cookies, fp, protocol );

            int rc = 0;
            if(!"".equals( resp ) ){
                JSONObject jsonObject = JSONObject.fromObject( resp );
                rc= Integer.valueOf( jsonObject.get( "code" ).toString() ) ;
            }

            long startTime = System.currentTimeMillis();
                // 统计上报
            SnsStat.statReport(startTime, serverName, params, method, protocol, rc,scriptName);


            //通过调用以下方法，可以打印出调用openapi请求的返回码以及错误信息，默认注释
            printRespond(resp);

            return resp;
        }
    }


    /**
     * 辅助函数，打印出完整的请求串内容
     *
     * @param url 请求cgi的url
     * @param method 请求的方式 get/post
     * @param params OpenApi的参数列表
     */
    private void printRequest(String url,String method,HashMap<String, Object> params) throws OpensnsException
    {
        System.out.println("==========Request Info==========\n");
        System.out.println("method:  " + method);
        System.out.println("url:  " + url);
        System.out.println("params:");
        System.out.println(params);
        System.out.println("querystring:");
        StringBuilder buffer = new StringBuilder(128);
        Iterator iter = params.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            try
            {
                buffer.append( URLEncoder.encode((String)entry.getKey(), "UTF-8").replace("+", "%20").replace("*", "%2A")).append("=").append(URLEncoder.encode( String.valueOf( entry.getValue() ) , "UTF-8").replace("+", "%20").replace("*", "%2A")).append("&");
            }
            catch(UnsupportedEncodingException e)
            {
                throw new OpensnsException(ErrorCode.MAKE_SIGNATURE_ERROR, e);
            }
        }
        String tmp = buffer.toString();
        tmp = tmp.substring(0,tmp.length()-1);
        System.out.println(tmp);
        System.out.println();
    }

    /**
     * 辅助函数，打印出完整的执行的返回信息
     *
     * @return 返回服务器响应内容
     */
    private void printRespond(String resp)
    {
        System.out.println("===========Respond Info============");
        System.out.println(resp);
    }

}
