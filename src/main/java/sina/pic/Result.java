package sina.pic;

import io.vertx.core.json.Json;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result {
  private Object data;
  private Integer code;
  private String msg;
  private String img;

  public static Result ok(Object o){
    return  Result.builder().code(1).msg("操作成功").data(o).build();
  }

  public static Result ok(){
    return  Result.builder().code(1).msg("操作成功").data(null).build();
  }

  public static Result erro(Integer code,String msg){
    return  Result.builder().code(code).msg(msg).build();
  }

  public static Result success(String img){
    return  Result.builder().code(1).msg("操作成功").img(img).build();
  }

  public static Result erro(String msg){
    return  Result.builder().code(-1).msg(msg).build();
  }
  @Override
  public String toString() {
    return Json.encode(this);
  }
}
