package com.erqi.sdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.erqi.sdk.https.MySecureProtocolSocketFactory;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;


 /**
 * 发送HTTP网络请求类
 *
 * @version 1.0.0
 * @since jdk1.8
 * @author open.erqikefu.com
 * @copyright © 2018, Skylarkai Corporation. All rights reserved.
 *
 */
 
 
public class SnsNetwork
{

    /** 
     * 发送POST请求
     * 
     * @param url 请求URL地址 
     * @param params 请求参数 
     * @param protocol 请求协议 "http" / "https"
     * @return 服务器响应的请求结果
     * @throws OpensnsException 网络故障时抛出异常。
     */
    public static String postRequest(
            String url, 
            HashMap<String, Object> params,
            HashMap<String, String> cookies,
            String protocol) throws OpensnsException
    {
        if (protocol.equalsIgnoreCase("https"))
        {
            Protocol httpsProtocol = new Protocol("https",
                new MySecureProtocolSocketFactory(), 443);
            Protocol.registerProtocol("https", httpsProtocol);
        }

        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);
        String accessToken = null;
        if(params.get( "access_token" ) != null ) {
            accessToken = params.get( "access_token" ).toString();
            params.remove( "access_token" );
        }
        // 设置请求参数
        if (params != null && !params.isEmpty())
        {
        NameValuePair[] data = new NameValuePair[params.size()];

        Iterator iter = params.entrySet().iterator();
        int i=0;
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            data[i] = new NameValuePair((String)entry.getKey(), String.valueOf(entry.getValue()));
            ++i;
        }

        postMethod.setRequestBody(data);
    }

        // 设置cookie
        if (cookies !=null && !cookies.isEmpty())
        {
            Iterator iter = cookies.entrySet().iterator();
            StringBuilder buffer = new StringBuilder(128);
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next(); 
                buffer.append((String)entry.getKey()).append("=").append((String)entry.getValue()).append("; ");
            }
            // 设置cookie策略
            postMethod.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
            
            // 设置cookie内容
            postMethod.setRequestHeader("Cookie", buffer.toString());
        }

        if(accessToken != null) {
            postMethod.setRequestHeader( "Authorization", accessToken );
        }
        // 设置建立连接超时时间
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);

        // 设置读数据超时时间
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(READ_DATA_TIMEOUT);

        // 设置编码
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
                CONTENT_CHARSET); 

        //使用系统提供的默认的恢复策略
        postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler());

        try 
        {
            try
            {
                int statusCode = httpClient.executeMethod(postMethod);
    
                if (statusCode != HttpStatus.SC_OK) 
                {
                    throw new OpensnsException(ErrorCode.NETWORK_ERROR, "Request [" + url + "] failed:" + postMethod.getStatusLine());
                }

                InputStream inputStream = postMethod.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String str= "";
                while((str = br.readLine()) != null){
                    stringBuffer .append(str );
                }
    
                return stringBuffer.toString();
            }
            finally
            {
                //释放链接
                postMethod.releaseConnection();
            }
        } 
        catch (HttpException e) 
        {
            //发生致命的异常，可能是协议不对或者返回的内容有问题
            throw new OpensnsException(ErrorCode.NETWORK_ERROR, "Request [" + url + "] failed:" +  e.getMessage());
        } 
        catch (IOException e) 
        {
            //发生网络异常
            throw new OpensnsException(ErrorCode.NETWORK_ERROR, "Request [" + url + "] failed:" +  e.getMessage());
        } 
    }

    /**
     * 发送GET请求
     *
     * @param url 请求URL地址
     * @param params 请求参数
     * @param protocol 请求协议 "http" / "https"
     * @return 服务器响应的请求结果
     * @throws OpensnsException 网络故障时抛出异常。
     */
    public static String getRequest(
            String url,
            HashMap<String, Object> params,
            HashMap<String, String> cookies,
            String protocol) throws OpensnsException
    {
        if (protocol.equalsIgnoreCase("https"))
        {
            Protocol httpsProtocol = new Protocol("https",
                    new MySecureProtocolSocketFactory(), 443);
            Protocol.registerProtocol("https", httpsProtocol);
        }

        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(url);
        String accessToken = null;
        if(params.get( "access_token" ) != null ) {
            accessToken = params.get( "access_token" ).toString();
            params.remove( "access_token" );
        }

        // 设置请求参数
        if (params != null && !params.isEmpty())
        {
            NameValuePair[] data = new NameValuePair[params.size()];

            Iterator iter = params.entrySet().iterator();
            int i=0;
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next();
                data[i] = new NameValuePair((String)entry.getKey(), String.valueOf(entry.getValue()));
                ++i;
            }

            getMethod.setQueryString( data );
        }

        // 设置cookie
        if (cookies !=null && !cookies.isEmpty())
        {
            Iterator iter = cookies.entrySet().iterator();
            StringBuilder buffer = new StringBuilder(128);
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next();
                buffer.append((String)entry.getKey()).append("=").append((String)entry.getValue()).append("; ");
            }
            // 设置cookie策略
            getMethod.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);

            // 设置cookie内容
            getMethod.setRequestHeader("Cookie", buffer.toString());
        }

        if(accessToken != null) {
            getMethod.setRequestHeader( "Authorization", accessToken );
        }
        // 设置建立连接超时时间
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);

        // 设置读数据超时时间
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(READ_DATA_TIMEOUT);

        // 设置编码
        getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
                CONTENT_CHARSET);

        //使用系统提供的默认的恢复策略
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler());

        try
        {
            try
            {
                int statusCode = httpClient.executeMethod(getMethod);

                if (statusCode != HttpStatus.SC_OK)
                {
                    throw new OpensnsException(ErrorCode.NETWORK_ERROR, "Request [" + url + "] failed:" + getMethod.getStatusLine());
                }

                InputStream inputStream = getMethod.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String str= "";
                while((str = br.readLine()) != null){
                    stringBuffer .append(str );
                }

                return stringBuffer.toString();
            }
            finally
            {
                //释放链接
                getMethod.releaseConnection();
            }
        }
        catch (HttpException e)
        {
            //发生致命的异常，可能是协议不对或者返回的内容有问题
            throw new OpensnsException(ErrorCode.NETWORK_ERROR, "Request [" + url + "] failed:" +  e.getMessage());
        }
        catch (IOException e)
        {
            //发生网络异常
            throw new OpensnsException(ErrorCode.NETWORK_ERROR, "Request [" + url + "] failed:" +  e.getMessage());
        }
    }

	/** 
     * 发送POST请求
     * 
     * @param url 请求URL地址 
     * @param params 请求参数 
     * @param protocol 请求协议 "http" / "https"
	 * @param fp 上传的文件 
     * @return 服务器响应的请求结果
     * @throws OpensnsException 网络故障时抛出异常。
     */
    public static String postRequestWithFile(
            String url,
            HashMap<String, Object> params,
            HashMap<String, String> cookies, 
			FilePart fp,
            String protocol) throws OpensnsException 
    {
        if (protocol.equalsIgnoreCase("https"))
        {
            Protocol httpsProtocol = new Protocol("https",
                new MySecureProtocolSocketFactory(), 443);
            Protocol.registerProtocol("https", httpsProtocol);
        }

        HttpClient httpClient = new HttpClient();
		//上传文件要使用MultipartPostMethod类
        MultipartPostMethod postMethod = new MultipartPostMethod(url);
        String accessToken = params.get( "access_token" ).toString();
        params.remove( "access_token"  );
		//将FilePart类型的文件加到参数里
		postMethod.addPart(fp);
		
        // 设置请求参数
        if (params != null && !params.isEmpty())
        {
            Iterator iter = params.entrySet().iterator(); 
            while (iter.hasNext()) 
            { 
                Map.Entry entry = (Map.Entry) iter.next();
				//设置参数为UTF-8编码集，解决中文乱码问题，并且将参数加到post中
				postMethod.addPart(new StringPart((String)entry.getKey(), (String)entry.getValue(), CONTENT_CHARSET));
            } 
        }
		
        // 设置cookie
        if (cookies !=null && !cookies.isEmpty())
        {
            Iterator iter = cookies.entrySet().iterator();
            StringBuilder buffer = new StringBuilder(128);
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next(); 
                buffer.append((String)entry.getKey()).append("=").append((String)entry.getValue()).append("; ");
            }
            // 设置cookie策略
            postMethod.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
            // 设置cookie内容
            postMethod.setRequestHeader("Cookie", buffer.toString());
        }

        // 设置User-Agent
		postMethod.setRequestHeader("User-Agent", "Java OpenApiV3 SDK Client");

        // 设置建立连接超时时间
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);

        // 设置读数据超时时间
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(READ_DATA_TIMEOUT);

        //使用系统提供的默认的恢复策略
		postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        if(accessToken != null) {
            postMethod.setRequestHeader( "Authorization", accessToken );
        }
		//发送请求
        try 
        {
            try
            {	
				postMethod.getParams().setContentCharset("UTF-8");   
                int statusCode = httpClient.executeMethod(postMethod);
    
                if (statusCode != HttpStatus.SC_OK) 
                {
                    throw new OpensnsException(ErrorCode.NETWORK_ERROR, "Request [" + url + "] failed:" + postMethod.getStatusLine());
                }
    
                //读取内容 
                byte[] responseBody = postMethod.getResponseBody();
    
                return new String(responseBody, CONTENT_CHARSET);
            }
            finally
            {
                //释放链接
                postMethod.releaseConnection();
            }
        } 
        catch (HttpException e) 
        {
            //发生致命的异常，可能是协议不对或者返回的内容有问题
            throw new OpensnsException(ErrorCode.NETWORK_ERROR, "Request [" + url + "] failed:" +  e.getMessage());
        } 
        catch (IOException e) 
        {
            //发生网络异常
            throw new OpensnsException(ErrorCode.NETWORK_ERROR, "Request [" + url + "] failed:" +  e.getMessage());
        } 
    }
    
    // 编码方式
    private static final String CONTENT_CHARSET = "UTF-8";

    // 连接超时时间
    private static final int CONNECTION_TIMEOUT = 30000;

    // 读数据超时时间
    private static final int READ_DATA_TIMEOUT = 30000;
}
