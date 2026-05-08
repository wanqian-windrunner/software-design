package com.qkl.edu_system.pojo;

import lombok.Data;

@Data
public class SubmitQuestionRequest {
    private Integer examId;
    private Integer questionId;
    private String answer;
    private Integer studentId;  // 学生ID
}