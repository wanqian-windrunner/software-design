package com.qkl.edu_system.pojo.ppt;

import lombok.Data;

@Data
public class PptCreateOutlineRequest {
    private String query;
    private String businessId;
    private String language;
    private Boolean search;
}
