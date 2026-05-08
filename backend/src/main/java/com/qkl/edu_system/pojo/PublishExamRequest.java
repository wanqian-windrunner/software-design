package com.qkl.edu_system.pojo;

import lombok.Data;
import java.util.List;

@Data
public class PublishExamRequest {
    private Integer examId;
    private List<Integer> studentIds;
}