每次部署新jar包，访问系统时，浏览器若之前访问过则会留有缓存。访问的就可能不是最新的jar的页面，而是之前缓存的页面。访问的链接也可能是之前旧的url。

**解决办法**：

  给页面的js或css设置和jar包一样的版本号，并全局配置url，给url加入当前时间

参考https://github.com/wangliu1102/wl-guns 中的代码，其他模板框架思想类似

# 设置版本号

首先，POM文件设置版本号：

 

```
    <groupId>com.wl.guns</groupId>
    <artifactId>wl-guns</artifactId>
    <version>2.0.0</version>
```

application.yml引用POM文件中的版本号：

 

```
guns:
  version: @project.version@
```

在Properties中配置：

 

```
/**
 * guns项目配置
 *
 * @author 王柳
 * @Date 2017/5/23 22:31
 */
@Data
@Component
@ConfigurationProperties(prefix = GunsProperties.PREFIX)
public class GunsProperties {

    public static final String PREFIX = "guns";

    private String version = "1.0";
    
    ...
}

```

Guns项目使用的是 Beetl模板，在Beetl配置初始化全局变量version

 

```
/**
 * beetl拓展配置,绑定一些工具类,方便在模板中直接调用
 *
 * @author 王柳
 * @Date 2018/2/22 21:03
 */
public class BeetlConfiguration extends BeetlGroupUtilConfiguration {

    ......
    
    @Override
    public void initOther() {

        //全局共享变量
        Map<String, Object> shared = new HashMap<>();
        shared.put("version", SpringContextHolder.getBean(GunsProperties.class).getVersion());
        groupTemplate.setSharedVars(shared);
        
        ......
}
```

在html中引用js或css的地方，加入v=${version}后缀即可

 

```
<script src="${ctxPath}/static/js/jquery.min.js?v=${version}"></script>
```

# 设置全局Url时间后缀

因为guns所以业务功能的html页面都是基于_container.html的，

所以在_container.html中添加如下代码：

 

```
    <script type="text/javascript">
        //全局配置
        $.ajaxSetup({
            beforeSend: function(jqXHR, settings) {
                settings.url += settings.url.match(/\?/) ? "&" : "?";
                settings.url += "tempDate=" + new Date().getTime();
            },
        });
    </script>   
```