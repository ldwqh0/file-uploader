看似一个简简单单的下载会涉及到这么多的道道。  
content-type处理，缓存处理，断点续传，AIO等，这些东西要处理起来其实还是非常繁琐的。
nginx是款高效的http服务器，我们可以通过它实现文件的下载。    
但在某些情况下，需要在下载前进行一些其它的处理，比如权限校验等。这时，我们可以通过和nginx配合，实现文件的下载的前置处理。并通过nginx进行下载。
具体实现步骤如下：
1. 构建一个Spring web项目，编写文件的下载代码
```java
@RestController
@RequestMapping("files4")
public class NginxDownload {

    private final String prefix = "/ngdownload";

    @GetMapping("{filename}")
    public ResponseEntity<?> download(@PathVariable("filename") String filename) {
        // 在这之前进行一些必要的处理，比如鉴权，或者其它的处理逻辑。
        // 通过X-Accel-Redirect返回在nginx中的实际下载地址
        return ResponseEntity.ok().header("X-Accel-Redirect", prefix + "/" + filename).build();
    }
}
```
整个代码的核心关键点就是返回响应头，在响应头中增加X-Accel-Redirect，它指向了一个在nginx中配置的路径  
2. 在nginx中启用如下配置
```
location /ngdownload {
    alias D:/uploader;
    internal;
}
location /files4 {
    proxy_pass http://localhost:8080/files4;
}
```
配置中有两个location配置，其中的ngdownload中使用alias 将路径映射到了文件的实际存储目录。而这条规则被配置为internal。意即这条规则只能被nginx内部使用。我们直接在浏览器中输入http://host/ngdownload/xxx是无法访问到相关资源的。  
第二条配置将所有/files请求都代理到了后台服务器的/files。  

整个请求流程如下：  
1. 浏览器请求 http://nginx/files4/filename.xxx,
2. nginx将请求转发至 http://localhost:8080/files4/filename.xxx
3. 后台服务器**进行必要**的校验，或者其它操作。如果操作成功，返回带X-Accel-Redirect的响应。响应头中包含nginx配置的internal访问路径
4. nginx检测后台服务器返回的响应，如果响应中包含X-Accel-Redirectx,则将该请求内部转发至指定location。
5. nginx向客户端输出数据。

在整个流程中，后台服务器只需要处理一些必要的校验逻辑，而消耗时间和资源的io操作则交给nginx去处理（基于servlet的Controller,在下载完成之前会一直占用一个线程，而nginx在处理类似的请求时资源消耗会小很多）。这样可以大大提高后台服务的处理效率，提高系统吞吐量。

项目代码 
[https://github.com/ldwqh0/file-uploader](https://github.com/ldwqh0/file-uploader)

欢迎吐槽