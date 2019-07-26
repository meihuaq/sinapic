package sina.pic;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static io.vertx.core.http.HttpHeaders.*;

public class MainVerticle extends AbstractVerticle {
  File file = new File("conf.json").getAbsoluteFile();
  private static String user;
  private static String pass;
  private ApiServer apiServer = new ApiServer();



  public static void main(String[] args) {

   Runner.runExample(MainVerticle.class);
  }

  @Override
  public void start() throws Exception {

   final Router router = Router.router(vertx);
    router.route().handler(ctx->{
      ctx.response().headers().add(CONTENT_TYPE, "application/json; charset=utf-8");
      ctx.response().headers().add(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      ctx.response().headers().add(ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS, PUT, DELETE, HEAD");
      ctx.response().headers().add(ACCESS_CONTROL_ALLOW_HEADERS,
        "X-PINGOTHER, Origin,Content-Type, Accept, X-Requested-With, Dev, Authorization, Version, Token");
      ctx.response().headers().add(ACCESS_CONTROL_MAX_AGE, "1728000");
      ctx.next();
    });
    router.route().handler(BodyHandler.create());
    JsonObject my = new JsonObject(FileUtils.readFileToString(file, "UTF-8"));
    router.post("/api").handler(a->{
      String key = a.request().getFormAttribute("key");
      String imgBase64 = a.request().getFormAttribute("imgBase64");
      String onlyUrl = a.request().getFormAttribute("onlyUrl");
      boolean str = false;
      if (onlyUrl!=null && onlyUrl.equals("1")){
        str = true;
      }
      //是否需要key
      if(false){
        if (key==null || key.equals("")){
          if (str){
            a.response().end("null");
          }else{ a.response().end(Result.erro("请传入通讯密钥").toString()); }
        }
      }
      if (imgBase64 == null || imgBase64.equals("")){
        if (str){
          a.response().end("null");
        }else{
          a.response().end(Result.erro("请传入图片的Base64编码").toString());
        }
      }

      Result apiRes = apiServer.doUpload(key, imgBase64);

      if (str){
        a.response().end(apiRes.getImg());
      }else{
        a.response().end(apiRes.toString());
      }



    });
    router.route("/*").handler(StaticHandler.create());

    vertx.createHttpServer().requestHandler(router).listen(my.getInteger("port",8081));


  }


}
