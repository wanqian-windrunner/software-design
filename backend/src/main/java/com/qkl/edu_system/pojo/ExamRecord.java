package com.qkl.edu_system.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamRecord {
    private Integer id;
    private Integer studentId;
    private Integer examId;
    private Double accuracy;
    private Double accomplish;
    private String aiReview;
    private Integer state;
    private LocalDateTime createTime;
}