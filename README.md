This java app was built by [@rjrudin](https://github.com/rjrudin) to try to reproduce connection errors using only the MarkLogic Java API that some users have been seeing while deploying project using ml-gradle or the MarkLogic Data Hub Framework. My error looked like this, but others have seen different network-related errors like broken pipes.

```
java.lang.RuntimeException: Error occurred while loading REST modules: Error occurred while loading modules; host: localhost; port: 18010; cause: java.io.IOException: unexpected end of stream on Connection{localhost:8010, proxy=DIRECT hostAddress=localhost/0:0:0:0:0:0:0:1:8010 cipherSuite=none protocol=http/1.1}
 at com.marklogic.client.ext.modulesloader.impl.DefaultModulesLoader.rethrowRestModulesFailureIfOneExists(DefaultModulesLoader.java:172)
```

If you're running into a similar issue while deploying with ml-gradle, and you also have issues running this program against the same MarkLogic instance from the same client machine, add your comments to https://project.marklogic.com/jira/browse/DHFPROD-1967 (MarkLogic internal only)

To run the test, change the connection information in gradle.properties then run `./gradlew loadModules`
