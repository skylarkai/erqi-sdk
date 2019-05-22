package cn.skylarkai.sdk;


/**
* 定义错误码。
*
* @version 1.0.0
* @since jdk1.8
* @author open.erqikefu.com
*
*/
 
public class ErrorCode {
	
	// 序列化UID
	private static final long serialVersionUID = -1679458253208555786L;

	/**
	 * 必填参数为空。
	 */
	public final static int PARAMETER_EMPTY = 1801;
	
	/**
	 * 必填参数无效。
	 */
	public final static int PARAMETER_INVALID = 1802;
	
	/**
	 * 服务器响应数据无效。
	 */
	public final static int RESPONSE_DATA_INVALID = 1803;
	
	/**
	 * 生成签名失败。
	 */
	public final static int MAKE_SIGNATURE_ERROR = 1804;

	/**
	 * 网络错误。
	 */
	public final static int NETWORK_ERROR = 1900;
}
