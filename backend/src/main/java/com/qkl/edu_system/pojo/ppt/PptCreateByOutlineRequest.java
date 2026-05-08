package com.qkl.edu_system.pojo.ppt;

import lombok.Data;

@Data
public class PptCreateByOutlineRequest {
    private String query;
    private String outlineSid;
    private PptOutline outline;
    private String templateId;
    private String businessId;
    private String author;
    private Boolean isCardNote;
    private Boolean search;
    private String language;
    private String fileUrl;
    private String fileName;
    private Boolean isFigure;
    private String aiImage;
}
