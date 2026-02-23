// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/wanglin/Documents/GitHub/IT project/conf/routes
// @DATE:Mon Feb 23 00:19:06 GMT 2026

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseGameScreenController GameScreenController = new controllers.ReverseGameScreenController(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseAssets Assets = new controllers.ReverseAssets(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseGameScreenController GameScreenController = new controllers.javascript.ReverseGameScreenController(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseAssets Assets = new controllers.javascript.ReverseAssets(RoutesPrefix.byNamePrefix());
  }

}
