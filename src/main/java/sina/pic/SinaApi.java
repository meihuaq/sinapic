package sina.pic;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SinaApi {
  public static String uploadImg(String imgBase64,String ck) {


    String url = "http://picupload.service.weibo.com/interface/pic_upload.php?cb=https%3A%2F%2Fweibo.com%2Faj%2Fstatic%2Fupimgback.html%3F_wv%3D5%26callback%3DSTK_ijax_1551096206285100&mime=image%2Fjpeg&data=base64&url=weibo.com%2Fu%2F5734329255&markpos=1&logo=1&nick=&marks=0&app=miniblog&s=rdxt&pri=0&file_source=2";



    String ret = "";

    URL u = null;
    HttpURLConnection con = null;
    InputStream inputStream = null;
    //尝试发送请求
    try {

      //ck="SUB=_2A25xd6e5DeRhGeNJ6FYS8ifOzjmIHXVSBJ5xrDV8PUNbmtAKLXnHkW9NS8PUgB08tOVmchSobLjjnfy2-EM-svCC;";

      u = new URL(url);
      con = (HttpURLConnection) u.openConnection();
      con.setRequestMethod("POST");
      con.setDoOutput(true);
      con.setDoInput(true);
      con.setUseCaches(false);
      con.setRequestProperty("Cookie", ck);


      PrintWriter printWriter = new PrintWriter(con.getOutputStream());
      // 发送请求参数
      printWriter.write("b64_data="+ URLEncoder.encode(imgBase64));//post的参数 xx=xx&yy=yy
      // flush输出流的缓冲
      printWriter.flush();

      //读取返回内容
      inputStream = con.getInputStream();

      //https://weibo.com/aj/static/upimgback.html?_wv=5&callback=STK_ijax_1551096206285100&ret=1&pid=006g4EZxgy1g0iz2blozoj30u01aogo9

      InputStreamReader isr = new InputStreamReader(inputStream);
      BufferedReader bufr = new BufferedReader(isr);
      String str;



      while ((str = bufr.readLine()) != null) {
        ret+=str;
      }

      System.out.println(con.getHeaderField("location"));

      String retCode = VoneUtil.getSubString(con.getHeaderField("location")+"&","&ret=","&");
      if (retCode.equals("1")){
        ret = VoneUtil.getSubString(con.getHeaderField("location")+"&","&pid=","&");

        if (!ret.equals("")){
          ret = "http://wx1.sinaimg.cn/large/"+ret+".jpg";
        }
      }else {
        ret = retCode;
      }





    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      return ret;
    }
  }


  public static String login(String user, String pass) {
    String url = "https://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.15)&_=";
    String post = "entry=sso&gateway=1&from=null&savestate=30&useticket=0&pagerefer=&vsnf=1"
      + "&su="+ Base64.encode(user.getBytes())+"&service=sso&sp="+pass
      + "&sr=1024*768&encoding=UTF-8&cdult=3&domain=sina.com.cn&prelt=0&returntype=TEXT";
    String ret = "";
    URL u = null;
    HttpURLConnection con = null;
    InputStream inputStream = null;
    //尝试发送请求
    try {
      u = new URL(url);
      con = (HttpURLConnection) u.openConnection();
      con.setRequestMethod("POST");
      con.setDoOutput(true);
      con.setDoInput(true);
      con.setUseCaches(false);


      PrintWriter printWriter = new PrintWriter(con.getOutputStream());
      printWriter.write(post);
      printWriter.flush();

      //读取返回内容
      inputStream = con.getInputStream();


      InputStreamReader isr = new InputStreamReader(inputStream);
      BufferedReader bufr = new BufferedReader(isr);
      String str;



      while ((str = bufr.readLine()) != null) {
        ret+=str;
      }
      System.out.println(ret);
      //获取cookie
      Map<String, List<String>> map=con.getHeaderFields();
      Set<String> set=map.keySet();
      for (Iterator iterator = set.iterator(); iterator.hasNext();) {
        String key = (String) iterator.next();
        //System.out.println(key);
        if (key!=null && key.equals("Set-Cookie")) {
          //System.out.println("key=" + key+",开始获取cookie");
          List<String> list = map.get(key);
          StringBuilder builder = new StringBuilder();
          for (String str1 : list) {
            builder.append(str1).toString();
          }
          ret=builder.toString();
          //System.out.println("得到的cookie="+ret);
        }
      }

      ret = VoneUtil.getSubString(ret,"SUB=",";");
      if (!ret.equals("")){
        ret = "SUB="+ret;
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      return ret;
    }
  }
}
