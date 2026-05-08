package com.qkl.edu_system.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private Integer examId;      // exam_id
    private Integer questionId;  // question_id
    private String question;     // question (text类型)
    private String answer;       // answer (text类型)
    private Integer state;       // state (tinyint, 默认0)
    private LocalDateTime createTime;  // create_time
    private LocalDateTime updateTime;  // update_time
}