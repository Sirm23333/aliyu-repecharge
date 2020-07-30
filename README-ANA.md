为了统一日志格式和内容，以及方便分析

java-mini-faas-ana-1.0-SNAPSHOT.jar是日志输出和分析的程序

使用：
- 将java-mini-faas-ana工程 git clone到本地，打包(mvn package)
- 在update-ana.sh中将dir更改为打包生成的jar路径
- 运行update-ana.sh
- 在主项目pom.xml中引入依赖
```$xml
<dependency>
    <groupId>com.java.mini.faas.ana</groupId>
    <artifactId>java-mini-faas-ana</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
更新：
- 若在java-mini-faas-ana工程中添加新的功能，及时git push同步，本地直接运行update-ana.sh即可使用更新后的jar包