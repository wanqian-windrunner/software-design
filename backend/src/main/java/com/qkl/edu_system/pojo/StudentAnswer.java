package com.qkl.edu_system.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswer {
    private Integer id;
    private Integer examId;
    private Integer studentId;
    private Integer questionId;
    private String studentAnswer;
    private Integer isCorrect;
    private BigDecimal accuracy;
    private String reason;
    private LocalDateTime createTime;
}