package com.qkl.edu_system.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    private Integer examId;
    private String examName;    // 考试名称
    private Integer clazzId;
    private Integer state;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}