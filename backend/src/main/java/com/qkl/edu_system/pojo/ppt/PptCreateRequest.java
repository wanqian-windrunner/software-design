package com.qkl.edu_system.pojo.ppt;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PptCreateRequest {
    private String query;
    private MultipartFile file;
    private String fileUrl;
    private String fileName;
    private String templateId;
    private String businessId;
    private String author;
    private Boolean isCardNote;
    private Boolean search;
    private String language;
    private Boolean isFigure;
    private String aiImage;
}
