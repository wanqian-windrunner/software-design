package com.qkl.edu_system.controller;

import com.qkl.edu_system.pojo.ppt.*;
import com.qkl.edu_system.service.PptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ppt")
public class PptController {

    @Autowired
    private PptService pptService;

    @PostMapping(value = "/templates", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> listTemplates(@RequestBody PptTemplateQuery query) {
        return pptService.listTemplates(query);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> createPpt(@ModelAttribute PptCreateRequest request) {
        return pptService.createPpt(request);
    }

    @PostMapping(value = "/createOutline", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> createOutline(@ModelAttribute PptCreateOutlineRequest request) {
        return pptService.createOutline(request);
    }

    @PostMapping(value = "/createOutlineByDoc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> createOutlineByDoc(@ModelAttribute PptCreateOutlineByDocRequest request) {
        return pptService.createOutlineByDoc(request);
    }

    @PostMapping(value = "/createPptByOutline", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> createPptByOutline(@RequestBody PptCreateByOutlineRequest request) {
        return pptService.createPptByOutline(request);
    }

    @GetMapping("/progress")
    public Map<String, Object> getProgress(@RequestParam String sid) {
        return pptService.getProgress(sid);
    }
}
