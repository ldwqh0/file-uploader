package com.xyyh.fileuploader.simple.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 最简单的文件上传和下载
 * @author LiDong
 *
 */
@RequestMapping("files")
@RestController
public class FileController {

    private String path = "d:" + File.separator + "uploader";

    @PostMapping
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file)
            throws IllegalStateException, IOException {
        // 获取原始文件名
        String filename = file.getOriginalFilename();
        // 构建保存目标
        File target = new File(path + File.separator + filename);
        // 将文件转移到指定目录
        file.transferTo(target);
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("target", target.getAbsolutePath());
        return response;
    }

    @GetMapping("{filename}")
    public ResponseEntity<InputStreamSource> download(@PathVariable("filename") String filename) {
        // 构建下载路径
        File target = new File(path + File.separator + filename);
        // 构建响应体
        if (target.exists()) {
            FileSystemResource resource = new FileSystemResource(target);
            return ResponseEntity.ok()
                    // 指定文件的contentType
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } else {
            // 如果文件不存在，返回404响应
            return ResponseEntity.notFound().build();
        }
    }

    @PostConstruct
    public void initStorage() {
        File target = new File(path);
        if (!target.exists()) {
            if (target.mkdirs()) {
                System.out.println("文件存储目录创建成功。");
            } else {
                System.err.println("文件存储目录创建失败。");
            }
        }
    }

}
