package com.xyyh.fileuploader.simple.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 * 增加了缓存的下载
 * 
 * @author LiDong
 *
 */
@RequestMapping("files3")
@RestController
public class FileController3 {

    private String path = "d:" + File.separator + "uploader";

    private final Map<String, String> mediaTypes;

    public FileController3() {
        mediaTypes = new HashMap<String, String>();
        mediaTypes.put("mp4", "video/mp4");
        mediaTypes.put("jpeg", "image/jpeg");
        mediaTypes.put("jpg", "jpg");
        mediaTypes.put("png", "image/png");
    }

    @GetMapping("{filename}")
    public ResponseEntity<InputStreamSource> download(@PathVariable("filename") String filename,
            WebRequest request)
            throws FileNotFoundException {
        // 构建下载路径
        File target = new File(path + File.separator + filename);
        // 构建响应体
        if (target.exists()) {
            // 获取文件的最后修改时间
            long lastModified = target.lastModified();
            if (request.checkNotModified(lastModified)) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }
            // 获取文件扩展名
            String ext = filename.substring(filename.lastIndexOf(".") + 1);
            // 根据文件扩展名获取mediaType
            String mediaType = mediaTypes.get(ext);
            // 如果没有找到对应的扩展名，使用默认的mediaType
            if (Objects.isNull(mediaType)) {
                mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            InputStreamSource resource = new FileSystemResource(target);
            return ResponseEntity.ok()
                    // 指定文件的缓存时间，这里指定60秒，高速浏览器在60秒之内不用重新请求
                    .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                    // 返回文件的最后修改时间
                    .lastModified(lastModified)
                    // 指定文件的contentType
                    // contentType方法只能支持Spring内置的一些mediaType类型
                    // 但我们会由一些其它的MediaType类型，比如video/mp4等，这时我们需要直接通过字符串设置响应头
                    // .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Type", mediaType)
                    .body(resource);
        } else {
            // 如果文件不存在，返回404响应
            return ResponseEntity.notFound().build();
        }
    }
}
