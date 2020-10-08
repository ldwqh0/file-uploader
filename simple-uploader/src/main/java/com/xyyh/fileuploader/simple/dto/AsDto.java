package com.xyyh.fileuploader.simple.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;
import lombok.Getter;

@Data
public class AsDto {

    private Long id = 1L;

    @Getter(onMethod_ = { @JsonProperty(access = Access.WRITE_ONLY) })
    private String name = "张三";

    @Getter(onMethod_ = { @JsonProperty(access = Access.READ_ONLY) })
    private String age = "12";

}
