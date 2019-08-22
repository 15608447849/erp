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

}
