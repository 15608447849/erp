package framework.server;

import properties.abs.ApplicationPropertiesBase;
import properties.annotations.PropertiesFilePath;
import properties.annotations.PropertiesName;

import java.util.HashMap;

@PropertiesFilePath("/iceapplication.properties")
public class IceProperties extends ApplicationPropertiesBase {

  @PropertiesName("ice.application.package")
  public static String pkgSrv ;

  @PropertiesName("ice.push.message.allow.server.name")
  public static String allowPushMessageServer;

  @PropertiesName("ice.server.rep.group")
  public static String repSrv ;

  private static HashMap<String,String> repSrvMap;

  public static HashMap<String,String> getRepSrvMap(){
      if (repSrvMap == null){
          HashMap<String,String> map = new HashMap<>();
          try {
              String[] arr = repSrv.split(";");
              for (String str : arr){
                  String[] tarr = str.split(":");
                  String v = tarr[0];
                  String[] trarr = tarr[1].split(",");
                  for (String k : trarr){
                      map.put(k,v);
                  }
              }
              repSrvMap = map;
          } catch (Exception ignored) {
          }
      }

    return repSrvMap;
  }





}
