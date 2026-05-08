package com.qkl.edu_system.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Score {
    private Integer testId;
    private Integer examId;
    private Integer id;          // 学生ID
    private BigDecimal accuracy;
    private String aiReview;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 扩展字段（用于关联查询）
    private String username;
    private String examName;
}