package com.erqi.sdk.https;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

 
/**
* 自定义签名证书管理类 (接受任意来源证书)
*
* @version 1.0.0
* @since jdk1.8
* @author open.erqikefu.com
* @copyright © 2018, Skylarkai Corporation. All rights reserved.
*/

 
public class MyX509TrustManager implements X509TrustManager 
{

	@Override
	public void checkClientTrusted(X509Certificate[] arg0, String arg1)
		throws CertificateException 
    {

	}

	@Override
	public void checkServerTrusted(X509Certificate[] arg0, String arg1)
		throws CertificateException 
    {

	}

	@Override
	public X509Certificate[] getAcceptedIssuers()
    {
		return null;
	}

}
