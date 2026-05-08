package com.qkl.edu_system.pojo.ppt;

import lombok.Data;

import java.util.List;

@Data
public class PptOutline {
    private String title;
    private String subTitle;
    private List<Chapter> chapters;

    @Data
    public static class Chapter {
        private String chapterTitle;
        private List<Chapter> chapterContents;
    }
}
