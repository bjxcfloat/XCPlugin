package xc.lib.common.httpclient.test;

import xc.lib.common.httpclient.CallBack;
import xc.lib.common.httpclient.Cancelable;
import xc.lib.common.httpclient.annations.Field;
import xc.lib.common.httpclient.annations.FileParm;
import xc.lib.common.httpclient.annations.Get;
import xc.lib.common.httpclient.annations.Head;
import xc.lib.common.httpclient.annations.Multipart;

import java.io.File;

public interface LoginService {


    @Head({"h1=1", "h2=34"})
    @Multipart
    @Get("https://www.baidu.com")
    Cancelable login(

            @FileParm({"file1","file2"}) File[] files,
            @Field("username") int username,
            @Field("password") String password,
            CallBack<String> callBack);


}
