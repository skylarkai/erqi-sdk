import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.erqi.sdk.OpenApiV1;
import com.erqi.sdk.OpensnsException;

/**
 * TestOpenApiV1
 *
 * @author chuhl
 * @date 2018/12/28
 */
public class TestOpenApiV1 {

    public static void main(String args[]){

        String appid= "1";
        String appkey="421621764201";
        String appSecret="047917712880364406";
        String servername="localhost:9001";

        String username="yqzx";
        String password="yqzx";

        // 指定HTTP请求协议类型
        String protocol = "http";
        OpenApiV1 sdk = new OpenApiV1( appid,appkey,appSecret );
        sdk.setServerName( servername );
        HashMap<String,Object> params = new HashMap<String, Object>(  );
        params.put("appid",appid);
        params.put( "client_id",appkey );
        params.put( "client_secret",appSecret );
        params.put( "username",username );
        params.put( "password",password );

        String tokenJson = null;
        try {
            tokenJson = sdk.getToken( params,protocol );
        } catch (OpensnsException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = JSON.parseObject( tokenJson );
        String access_token = jsonObject.get( "access_token" ).toString();
        sdk.setAccessToken(access_token  );
//        testGetUser(sdk);
//        testAysnApi(sdk);
        testCallback(sdk);
    }

    /**
     * 同步调用API样例
     *
     * @param sdk
     */
    public static void testGetUser(OpenApiV1 sdk){
        // 指定OpenApi Cgi名字
        String scriptName = "/usertenant/user/selectByUsername";
        String method="post";
        String protocol="http";
        HashMap<String,Object> params = new HashMap<String, Object>(  );
        params.put( "name","yqzx" );
        String ret=null;
        try {
            ret =  sdk.api(method,scriptName,params,protocol ,"JSON");
        } catch (OpensnsException e) {
            e.printStackTrace();
        }
    }

    public static void testAysnApi(OpenApiV1 sdk){
        String scriptName = "/openapi/v1/getAsynApi";
        String method="post";
        String protocol="http";
        HashMap<String,Object> params = new HashMap<String, Object>(  );
        params.put( "apiname", "http://localhost:9990/openapi/v1/getKGFieldByTempName" );
        HashMap<String,Object> values = new HashMap<>(  );
        values.put( "tenanid",1 );
        values.put( "tempname","店铺信息" );
        params.put( "param",JSON.toJSONString( values )  );
        params.put( "callbackMethod","POST");
        params.put( "callbackUrl","http://localhost:9990/openapi/getCallbackResult" );

        try {
            sdk.api(method,scriptName,params,protocol,"JSON" );
        } catch (OpensnsException e) {
            e.printStackTrace();
        }

    }

    /**
     * 指定回调函数样例
     *
     * @param sdk
     */
    public static void testCallback(OpenApiV1 sdk){
        //调用异步api
        String scriptName = "/openapi/v1/WebApiSendMsgToKf";
        String method="post";
        String protocol="http";
        //设置api参数
        HashMap<String,Object> values = new HashMap<String, Object>(  );
        values.put( "msg","engineer" );
        values.put( "tenanid","1" );
        values.put( "webapiid","zcojpxZV" );
        values.put( "fromUsername","游客3399" );
        HashMap<String,Object> params = new HashMap<String, Object>(  );
        params.put( "param", JSONObject.toJSONString(values  )  );
        params.put( "csid",sdk.getCsid() );

        //设置回调api
        params.put( "callbackUrl","http://localhost:9990/openapi/getCallbackResult" );
        params.put( "callbackMethod","POST");
        try {
            sdk.api(method,scriptName,params,protocol,"JSON" );
        } catch (OpensnsException e) {
            e.printStackTrace();
        }
    }
}
