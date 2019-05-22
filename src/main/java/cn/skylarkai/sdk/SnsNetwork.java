package cn.skylarkai.sdk;

import cn.skylarkai.sdk.https.HttpsContext;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.ws.soap.Addressing;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.apache.http.conn.ssl.SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;


/**
 * 发送HTTP网络请求类
 *
 * @version 1.0.0
 * @since jdk1.8
 * @author open.erqikefu.com
 *
 */
 
 
public class SnsNetwork
{
    /** 
     * 发送POST请求
     * 
     * @param url 请求URL地址 
     * @param params 请求参数
     * @param cookies cookie
     * @param mediatype JSON/TEXT
     * @return 服务器响应的请求结果
     * @throws OpensnsException 网络故障时抛出异常。
     */
    public static String postRequest(
            String url, 
            HashMap<String, Object> params,
            HashMap<String, String> cookies,
            String mediatype) throws OpensnsException, UnsupportedEncodingException {

        RequestConfig requestConfig =RequestConfig.custom().setConnectTimeout( CONNECTION_TIMEOUT ).setConnectionRequestTimeout( READ_DATA_TIMEOUT ).setSocketTimeout( READ_DATA_TIMEOUT ).build();
        SSLContext sc =  new HttpsContext().HttpsContext();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sc))
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        HttpClientBuilder builder = HttpClients.custom().setConnectionManager( connManager ).setDefaultRequestConfig( requestConfig ).setRetryHandler(new StandardHttpRequestRetryHandler() ).setMaxConnTotal( 1000 );


        HttpClient httpClient = builder.build();
        HttpPost httpPost = new HttpPost( url );


        String accessToken = null;
        if(params.get( "access_token" ) != null ) {
            accessToken = params.get( "access_token" ).toString();
            params.remove( "access_token" );
        }
        // 设置请求参数
        if (params != null && !params.isEmpty())
        {
            if(mediatype.equals( "JSON" )){
                StringEntity stringEntity = new StringEntity( JSON.toJSONString( params ),CONTENT_CHARSET);
                stringEntity.setContentType( "application/json" );
                httpPost.setEntity( stringEntity );

            }else{
                List<NameValuePair> data = new ArrayList<NameValuePair>(  );
                Iterator iter = params.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    data.add(new BasicNameValuePair( (String) entry.getKey(), String.valueOf( entry.getValue() ) ));
                }
                UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity( data,CONTENT_CHARSET);
                httpPost.setEntity( encodedFormEntity );
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
            
            // 设置cookie内容
            httpPost.setHeader( "Cookie", buffer.toString() );
        }

        if(accessToken != null) {
            httpPost.setHeader( "Authorization", accessToken );
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        }

        try 
        {
            try
            {
                HttpResponse httpResponse = httpClient.execute(httpPost);
    
                if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                {
                    throw new OpensnsException(ErrorCode.NETWORK_ERROR, "Request [" + url + "] failed:" + httpResponse.getStatusLine());
                }

                InputStream inputStream =httpResponse.getEntity().getContent();
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
                httpPost.releaseConnection();
            }
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
     * @param cookies cookie
     * @return 服务器响应的请求结果
     * @throws OpensnsException 网络故障时抛出异常。
     */
    public static String getRequest(
            String url,
            HashMap<String, Object> params,
            HashMap<String, String> cookies) throws OpensnsException
    {
        RequestConfig requestConfig =RequestConfig.custom().setConnectTimeout( CONNECTION_TIMEOUT ).setConnectionRequestTimeout( READ_DATA_TIMEOUT ).setSocketTimeout( READ_DATA_TIMEOUT ).build();
        SSLContext sc =  new HttpsContext().HttpsContext();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sc))
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        HttpClientBuilder builder = HttpClients.custom().setConnectionManager( connManager ).setDefaultRequestConfig( requestConfig ).setRetryHandler(new StandardHttpRequestRetryHandler() ).setMaxConnTotal( 1000 );


        HttpClient httpClient = builder.build();

        HttpGet httpGet = new HttpGet( url );
        String accessToken = null;
        if(params.get( "access_token" ) != null ) {
            accessToken = params.get( "access_token" ).toString();
            params.remove( "access_token" );
        }

        // 设置请求参数
        if (params != null && !params.isEmpty())
        {
            List<NameValuePair> data = new ArrayList<NameValuePair>(  );

            Iterator iter = params.entrySet().iterator();
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next();
                data.add( new BasicNameValuePair((String)entry.getKey(), String.valueOf(entry.getValue())) ) ;
            }
            String param = URLEncodedUtils.format( data,CONTENT_CHARSET );
            httpGet.setURI( URI.create(url+"?"+param));
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
            // 设置cookie内容
            httpGet.setHeader( "Cookie", buffer.toString() );
        }

        if(accessToken != null) {
            httpGet.setHeader(  "Authorization", accessToken);
        }

        try
        {
            try
            {
                HttpResponse httpResponse = httpClient.execute(httpGet);

                if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                {
                    throw new OpensnsException(ErrorCode.NETWORK_ERROR, "Request [" + url + "] failed:" + httpResponse.getStatusLine());
                }

                InputStream inputStream = httpResponse.getEntity().getContent();
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
                httpGet.releaseConnection();
            }
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
