package com.qkl.edu_system.service.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qkl.edu_system.config.PptConfig;
import com.qkl.edu_system.pojo.ppt.*;
import com.qkl.edu_system.service.PptService;
import com.qkl.edu_system.util.PptAuthAlgorithm;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.security.SignatureException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PptServiceImpl implements PptService {

    private static final String API_PREFIX = "/api/ppt/v2";
    private static final int CODE_PARAM_ERROR = 20002;
    private static final int CODE_AUTH_ERROR = 20007;
    private static final int CODE_SYSTEM_ERROR = 9999;

    private final PptConfig pptConfig;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final PptAuthAlgorithm authAlgorithm;

    public PptServiceImpl(PptConfig pptConfig,
                          ObjectMapper objectMapper,
                          RestTemplate restTemplate,
                          PptAuthAlgorithm authAlgorithm) {
        this.pptConfig = pptConfig;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.authAlgorithm = authAlgorithm;
    }

    @Override
    public Map<String, Object> listTemplates(PptTemplateQuery query) {
        if (query == null) {
            return buildError(CODE_PARAM_ERROR, "请求参数不能为空");
        }
        Map<String, Object> body = new LinkedHashMap<>();
        putIfHasText(body, "style", query.getStyle());
        putIfHasText(body, "color", query.getColor());
        putIfHasText(body, "industry", query.getIndustry());
        if (query.getPageNum() != null) {
            body.put("pageNum", query.getPageNum());
        }
        if (query.getPageSize() != null) {
            body.put("pageSize", query.getPageSize());
        }
        if (body.isEmpty()) {
            return buildError(CODE_PARAM_ERROR, "请求参数不能为空");
        }
        String url = buildUrl("/template/list");
        return exchangeJson(url, HttpMethod.POST, body);
    }

    @Override
    public Map<String, Object> createPpt(PptCreateRequest request) {
        if (request == null) {
            return buildError(CODE_PARAM_ERROR, "请求参数不能为空");
        }
        MultipartFile file = normalizeFile(request.getFile());
        String query = normalize(request.getQuery());
        String fileUrl = normalize(request.getFileUrl());
        String fileName = normalize(request.getFileName());

        if (!StringUtils.hasText(query) && file == null && !StringUtils.hasText(fileUrl)) {
            return buildError(CODE_PARAM_ERROR, "query/file/fileUrl 至少传入一项");
        }
        if ((file != null || StringUtils.hasText(fileUrl)) && !StringUtils.hasText(fileName)) {
            fileName = normalize(file == null ? null : file.getOriginalFilename());
            if (!StringUtils.hasText(fileName)) {
                return buildError(CODE_PARAM_ERROR, "fileName 不能为空");
            }
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        addMultipartValue(body, "query", query);
        addMultipartValue(body, "fileUrl", fileUrl);
        addMultipartValue(body, "fileName", fileName);
        addMultipartValue(body, "templateId", normalizeTemplateId(request.getTemplateId()));
        addMultipartValue(body, "businessId", normalize(request.getBusinessId()));
        addMultipartValue(body, "author", normalize(request.getAuthor()));
        addMultipartValue(body, "language", normalize(request.getLanguage()));
        addMultipartValue(body, "aiImage", normalize(request.getAiImage()));
        addMultipartValue(body, "isCardNote", request.getIsCardNote());
        addMultipartValue(body, "search", request.getSearch());
        addMultipartValue(body, "isFigure", request.getIsFigure());

        if (file != null) {
            try {
                body.add("file", new MultipartFileResource(file));
            } catch (IOException e) {
                return buildError(CODE_SYSTEM_ERROR, "文件读取失败");
            }
        }

        String url = buildUrl("/create");
        return exchangeMultipart(url, body);
    }

    @Override
    public Map<String, Object> createOutline(PptCreateOutlineRequest request) {
        if (request == null) {
            return buildError(CODE_PARAM_ERROR, "请求参数不能为空");
        }
        String query = normalize(request.getQuery());
        if (!StringUtils.hasText(query)) {
            return buildError(CODE_PARAM_ERROR, "query 不能为空");
        }
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        addMultipartValue(body, "query", query);
        addMultipartValue(body, "businessId", normalize(request.getBusinessId()));
        addMultipartValue(body, "language", normalize(request.getLanguage()));
        addMultipartValue(body, "search", request.getSearch());

        String url = buildUrl("/createOutline");
        return exchangeMultipart(url, body);
    }

    @Override
    public Map<String, Object> createOutlineByDoc(PptCreateOutlineByDocRequest request) {
        if (request == null) {
            return buildError(CODE_PARAM_ERROR, "请求参数不能为空");
        }
        MultipartFile file = normalizeFile(request.getFile());
        String fileUrl = normalize(request.getFileUrl());
        String fileName = normalize(request.getFileName());

        if (file == null && !StringUtils.hasText(fileUrl)) {
            return buildError(CODE_PARAM_ERROR, "file/fileUrl 至少传入一项");
        }
        if (!StringUtils.hasText(fileName)) {
            fileName = normalize(file == null ? null : file.getOriginalFilename());
        }
        if (!StringUtils.hasText(fileName)) {
            return buildError(CODE_PARAM_ERROR, "fileName 不能为空");
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        addMultipartValue(body, "query", normalize(request.getQuery()));
        addMultipartValue(body, "fileUrl", fileUrl);
        addMultipartValue(body, "fileName", fileName);
        addMultipartValue(body, "businessId", normalize(request.getBusinessId()));
        addMultipartValue(body, "language", normalize(request.getLanguage()));
        addMultipartValue(body, "search", request.getSearch());

        if (file != null) {
            try {
                body.add("file", new MultipartFileResource(file));
            } catch (IOException e) {
                return buildError(CODE_SYSTEM_ERROR, "文件读取失败");
            }
        }

        String url = buildUrl("/createOutlineByDoc");
        return exchangeMultipart(url, body);
    }

    @Override
    public Map<String, Object> createPptByOutline(PptCreateByOutlineRequest request) {
        if (request == null) {
            return buildError(CODE_PARAM_ERROR, "请求参数不能为空");
        }
        String query = normalize(request.getQuery());
        if (!StringUtils.hasText(query) || request.getOutline() == null) {
            return buildError(CODE_PARAM_ERROR, "query/outline 不能为空");
        }
        String fileUrl = normalize(request.getFileUrl());
        String fileName = normalize(request.getFileName());
        if (StringUtils.hasText(fileUrl) && !StringUtils.hasText(fileName)) {
            return buildError(CODE_PARAM_ERROR, "fileName 不能为空");
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("query", query);
        body.put("outline", request.getOutline());
        putIfHasText(body, "outlineSid", normalize(request.getOutlineSid()));
        putIfHasText(body, "templateId", normalizeTemplateId(request.getTemplateId()));
        putIfHasText(body, "businessId", normalize(request.getBusinessId()));
        putIfHasText(body, "author", normalize(request.getAuthor()));
        putIfHasText(body, "language", normalize(request.getLanguage()));
        putIfHasText(body, "fileUrl", fileUrl);
        putIfHasText(body, "fileName", fileName);
        putIfHasText(body, "aiImage", normalize(request.getAiImage()));
        if (request.getIsCardNote() != null) {
            body.put("isCardNote", request.getIsCardNote());
        }
        if (request.getSearch() != null) {
            body.put("search", request.getSearch());
        }
        if (request.getIsFigure() != null) {
            body.put("isFigure", request.getIsFigure());
        }

        String url = buildUrl("/createPptByOutline");
        return exchangeJson(url, HttpMethod.POST, body);
    }

    @Override
    public Map<String, Object> getProgress(String sid) {
        if (!StringUtils.hasText(sid)) {
            return buildError(CODE_PARAM_ERROR, "sid 不能为空");
        }
        String url = buildUrl("/progress?sid=" + sid.trim());
        return exchangeJson(url, HttpMethod.GET, null);
    }

    private Map<String, Object> exchangeJson(String url, HttpMethod method, Object body) {
        AuthHeadersResult headersResult = buildAuthHeaders(MediaType.APPLICATION_JSON);
        if (headersResult.headers == null) {
            return buildError(headersResult.errorCode, headersResult.errorMessage);
        }
        HttpEntity<Object> entity = new HttpEntity<>(body, headersResult.headers);
        return exchange(url, method, entity);
    }

    private Map<String, Object> exchangeMultipart(String url, MultiValueMap<String, Object> body) {
        AuthHeadersResult headersResult = buildAuthHeaders(MediaType.MULTIPART_FORM_DATA);
        if (headersResult.headers == null) {
            return buildError(headersResult.errorCode, headersResult.errorMessage);
        }
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headersResult.headers);
        return exchange(url, HttpMethod.POST, entity);
    }

    private Map<String, Object> exchange(String url, HttpMethod method, HttpEntity<?> entity) {
        if (!StringUtils.hasText(url)) {
            return buildError(CODE_SYSTEM_ERROR, "PPT 接口地址未配置");
        }
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);
            return parseResponse(response.getBody());
        } catch (HttpStatusCodeException e) {
            return parseResponse(e.getResponseBodyAsString());
        } catch (Exception e) {
            return buildError(CODE_SYSTEM_ERROR, "请求失败: " + e.getMessage());
        }
    }

    private Map<String, Object> parseResponse(String body) {
        if (!StringUtils.hasText(body)) {
            return buildError(CODE_SYSTEM_ERROR, "接口无响应");
        }
        try {
            return objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return buildError(CODE_SYSTEM_ERROR, "响应解析失败");
        }
    }

    private AuthHeadersResult buildAuthHeaders(MediaType contentType) {
        if (!StringUtils.hasText(pptConfig.getAppId()) || !StringUtils.hasText(pptConfig.getSecret())) {
            return new AuthHeadersResult(null, CODE_SYSTEM_ERROR, "鉴权信息未配置");
        }
        long timestamp = Instant.now().getEpochSecond();
        String signature;
        try {
            signature = authAlgorithm.getSignature(pptConfig.getAppId(), pptConfig.getSecret(), timestamp);
        } catch (SignatureException e) {
            return new AuthHeadersResult(null, CODE_AUTH_ERROR, "鉴权失败");
        }
        if (!StringUtils.hasText(signature)) {
            return new AuthHeadersResult(null, CODE_AUTH_ERROR, "鉴权失败");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("appId", pptConfig.getAppId());
        headers.set("timestamp", String.valueOf(timestamp));
        headers.set("signature", signature);
        headers.setContentType(contentType);
        return new AuthHeadersResult(headers, null, null);
    }

    private String buildUrl(String path) {
        if (!StringUtils.hasText(pptConfig.getBaseUrl())) {
            return null;
        }
        String base = pptConfig.getBaseUrl().trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return base + API_PREFIX + path;
    }

    private void putIfHasText(Map<String, Object> target, String key, String value) {
        if (StringUtils.hasText(value)) {
            target.put(key, value.trim());
        }
    }

    private void addMultipartValue(MultiValueMap<String, Object> target, String key, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof String stringValue) {
            if (!StringUtils.hasText(stringValue)) {
                return;
            }
            target.add(key, stringValue.trim());
            return;
        }
        target.add(key, value);
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String normalizeTemplateId(String templateId) {
        String normalized = normalize(templateId);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }
        if ("default".equalsIgnoreCase(normalized)) {
            return null;
        }
        return normalized;
    }

    private MultipartFile normalizeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        return file;
    }

    private Map<String, Object> buildError(int code, String desc) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("flag", false);
        error.put("code", code);
        error.put("desc", desc);
        error.put("count", null);
        error.put("data", null);
        return error;
    }

    private static class MultipartFileResource extends ByteArrayResource {
        private final String filename;

        private MultipartFileResource(MultipartFile file) throws IOException {
            super(file.getBytes());
            this.filename = file.getOriginalFilename();
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }

    private static class AuthHeadersResult {
        private final HttpHeaders headers;
        private final Integer errorCode;
        private final String errorMessage;

        private AuthHeadersResult(HttpHeaders headers, Integer errorCode, String errorMessage) {
            this.headers = headers;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }
}
