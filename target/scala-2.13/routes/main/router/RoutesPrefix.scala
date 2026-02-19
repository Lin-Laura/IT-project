// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/wanglin/Desktop/5074 IT+ Team Project/ITSD-DT2025-26-Template/conf/routes
// @DATE:Mon Feb 16 14:24:48 GMT 2026


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
