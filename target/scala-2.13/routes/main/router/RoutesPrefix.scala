// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/wanglin/Documents/GitHub/IT project/conf/routes
// @DATE:Mon Feb 23 00:19:06 GMT 2026


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
