package sina.pic;

import io.vertx.core.json.JsonObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class ApiServer {
  private static SinaMo sinaMo = null;
  private static SinaApi sinaApi = new SinaApi();
  public Result doUpload(String key, String imgBase64){
    if (sinaMo == null){
      getmo();
    }

    if(sinaMo.getUsekey()){
      if (!key.equals(sinaMo.getKey())){
        return Result.erro("通讯密钥错误");
      }
    }



    if (sinaMo.getType() == 1){
      return Result.erro("搜狗图床已停止服务");
//            String res = SougouApi.uploadImg(imgBase64);
//            if (res.indexOf("http")>=0){
//                return ApiResultUtil.success(res);
//            }else{
//                return ApiResultUtil.error("上传失败");
//            }
    }else if (sinaMo.getType() == 2){
      String ck = sinaMo.getSinaCookie();
      long lastTime = 0;
      try {
        lastTime = Long.valueOf(sinaMo.getSinaUpdateTime());
      }catch (Exception e){
        lastTime = 0;
      }

      if (ck.equals("") || lastTime+10800000<new Date().getTime()){
        ck = sinaApi.login(sinaMo.getSinaUser(),sinaMo.getSinaPass());

        if (ck.equals("")){
          return Result.erro("新浪账号密码有误");
        }

        sinaMo.setSinaCookie(ck);
        if (lastTime+10800000<new Date().getTime()){
          sinaMo.setSinaUpdateTime(String.valueOf(new Date().getTime()));
        }

      }

      String res = SinaApi.uploadImg(imgBase64,ck);
      if (res.indexOf("http")>=0){
        return Result.success(res);
      }else{
        if (res.equals("-1")){
          sinaMo.setSinaCookie("");
          return Result.erro("新浪账号密码有误");
        }else {
          return Result.erro("服务器繁忙，错误代码："+res);
        }
      }
    }


    return Result.erro("类型错误");
  }

  private void getmo() {
    if(sinaMo ==null){
      try {
        JsonObject my = new JsonObject(FileUtils.readFileToString(new File("conf.json").getAbsoluteFile(), "UTF-8"));
        String key = my.getString("key");
        SinaMo sinaMo1 = new SinaMo();
        sinaMo1.setSinaCookie("");
        sinaMo1.setType(2);
        sinaMo1.setSinaUser(my.getString("username"));
        sinaMo1.setKey(key);
        sinaMo1.setSinaPass(my.getString("password"));
        sinaMo1.setUsekey(my.getBoolean("usekey",false));
    sinaMo=sinaMo1;
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
  }

}
