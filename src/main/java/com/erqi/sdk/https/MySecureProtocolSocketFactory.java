package com.erqi.sdk.https;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ControllerThreadSocketFactory;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

 
/**
* 自定义SecureProtocolSocketFactory类 (辅助https实现接受任意来源证书)
*
* @version 1.0.0
* @since jdk1.8
* @author open.erqikefu.com
* @copyright © 2018, Skylarkai Corporation. All rights reserved.
*/


public class MySecureProtocolSocketFactory implements
        ProtocolSocketFactory
{

	private SSLContext sslContext = null;

	/**
	 * Constructor for MySecureProtocolSocketFactory.
	 */
	public MySecureProtocolSocketFactory() 
    {
	}

	/**
	 * 
	 * @return
	 */
	private static SSLContext createEasySSLContext() 
    {
		try 
        {
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(null, new TrustManager[] { new MyX509TrustManager() },
					null);
			return context;
		} 
        catch (Exception e) 
        {
			throw new HttpClientError(e.toString());
		}
	}

	/**
	 * 
	 * @return
	 */
	private SSLContext getSSLContext() 
    {
		if (this.sslContext == null) 
        {
			this.sslContext = createEasySSLContext();
		}
		return this.sslContext;
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress clientHost,
							   int clientPort) throws IOException, UnknownHostException
    {

		return getSSLContext().getSocketFactory().createSocket(host, port,
				clientHost, clientPort);
	}


	@Override
	public Socket createSocket(final String host, final int port,
							   final InetAddress localAddress, final int localPort,
							   final HttpConnectionParams params) throws IOException,
			UnknownHostException, ConnectTimeoutException 
    {
		if (params == null) 
        {
			throw new IllegalArgumentException("Parameters may not be null");
		}
		int timeout = params.getConnectionTimeout();
		if (timeout == 0) 
        {
			return createSocket(host, port, localAddress, localPort);
		} 
        else 
        {			
			return ControllerThreadSocketFactory.createSocket(this, host, port,
					localAddress, localPort, timeout);
		}
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException,
			UnknownHostException 
    {
		return getSSLContext().getSocketFactory().createSocket(host, port);
	}


	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException 
    {
		return getSSLContext().getSocketFactory().createSocket(socket, host,
				port, autoClose);
	}
}
