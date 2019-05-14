package com.erqi.sdk;


 
/**
* 定义异常。
*
* @version 1.0.0
* @since jdk1.8
* @author open.erqikefu.com
* @copyright © 2018, Skylarkai Corporation. All rights reserved.
*
*/
 
public class OpensnsException extends Exception {
	
	
	/**
	 * 构造异常
	 * 
	 * @param code 异常状态码
	 * @param msg 异常讯息
	 */
	public OpensnsException(int code, String msg) {
		super(msg);
		this.code = code;
	}
	
	/**
	 * 构造异常
	 * 
	 * @param code 异常状态码
	 * @param ex 异常来源
	 */
	public OpensnsException(int code, Exception ex) {
		super(ex);
		this.code = code;
	}
	
	/**
	 * 
	 * @return 异常状态码。
	 */
	public int getErrorCode() {
		return code;
	}

	// 序列化UID
	private static final long serialVersionUID = 8243127099991355146L;
	// 错误码
	private int code;
}