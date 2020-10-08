package com.xyyh.fileuploader.simple.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xyyh.fileuploader.simple.dto.AsDto;

@RestController
@RequestMapping("as")
public class AsController {

    @PostMapping
    public AsDto test(@RequestBody AsDto request) {
        return request;
    }
}
