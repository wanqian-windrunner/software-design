package com.qkl.edu_system.pojo.ppt;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PptCreateOutlineByDocRequest {
    private String query;
    private MultipartFile file;
    private String fileUrl;
    private String fileName;
    private String businessId;
    private String language;
    private Boolean search;
}
