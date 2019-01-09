import com.erqi.sdk.OpenApiV1;
import com.erqi.sdk.OpensnsException;
import net.sf.json.JSONObject;

import java.util.HashMap;

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
        String servername="dev.skylarkai.cn";

        String username="yqzx";
        String password="yqzx";

        // 指定HTTP请求协议类型
        String protocol = "https";
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

        JSONObject jsonObject = JSONObject.fromObject( tokenJson );
        String access_token = jsonObject.get( "access_token" ).toString();
        sdk.setAccessToken(access_token  );
        testGetUser(sdk);
    }

    public static void testGetUser(OpenApiV1 sdk){
        // 指定OpenApi Cgi名字
        String scriptName = "/usertenant/user/getUser";
        String method="get";
        String protocol="https";
        HashMap<String,Object> params = new HashMap<String, Object>(  );
        params.put( "id",1 );
        String ret=null;
        try {
            ret =  sdk.api(method,scriptName,params,protocol );
        } catch (OpensnsException e) {
            e.printStackTrace();
        }
    }
}
