package com.qkl.edu_system.service;

import com.qkl.edu_system.pojo.ppt.*;

import java.util.Map;

public interface PptService {
    Map<String, Object> listTemplates(PptTemplateQuery query);

    Map<String, Object> createPpt(PptCreateRequest request);

    Map<String, Object> createOutline(PptCreateOutlineRequest request);

    Map<String, Object> createOutlineByDoc(PptCreateOutlineByDocRequest request);

    Map<String, Object> createPptByOutline(PptCreateByOutlineRequest request);

    Map<String, Object> getProgress(String sid);
}
