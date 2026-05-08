package com.qkl.edu_system.service.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qkl.edu_system.config.AiConfig;
import com.qkl.edu_system.service.AiService;
import com.volcengine.ark.runtime.model.responses.common.ResponsesThinking;
import com.volcengine.ark.runtime.model.responses.constant.ResponsesConstants;
import com.volcengine.ark.runtime.model.responses.content.InputContentItemText;
import com.volcengine.ark.runtime.model.responses.item.ItemEasyMessage;
import com.volcengine.ark.runtime.model.responses.item.MessageContent;
import com.volcengine.ark.runtime.model.responses.request.CreateResponsesRequest;
import com.volcengine.ark.runtime.model.responses.request.ResponsesInput;
import com.volcengine.ark.runtime.model.responses.response.ResponseObject;
import com.volcengine.ark.runtime.service.ArkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private AiConfig aiConfig;

    @Override
    public Map<String, Object> judgeAnswer(Integer id, String question, String correctAnswer, String studentAnswer) {
        String msg = "你是专业的主观题批改老师，现在执行以下任务：\n" +
                "1. 任务：比对【标准答案】和【学生回答】，按知识点/采分点分步评分，**严禁非对即错**，答案有沾边、部分正确必须按比例给分，不得直接判0分（除非答案完全文不对题）；\n" +
                "2. 评分规则：总准确率为0.00~1.00之间的两位小数，按答对的采分点占比计算；\n" +
                "3. 返回要求：必须返回严格的JSON格式，无任何多余文字，格式示例：{\"ID\":3,\"accuracy\":0.25,\"reason\":\"详细判分原因\"}；\n" +
                "4. reason要求：必须写明：得分点（学生答对/沾边的内容），错误点（学生答案的偏差/错误），掌握的知识点与薄弱/错误的知识点";

        String fullMsg = msg + "\n题号：" + id + "\n题目：" + question + "\n标准答案：" + correctAnswer + "\n回答内容：" + studentAnswer;

        try {
            String text = callAi(fullMsg);
            System.out.println("AI判题返回：" + text);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(text);

            Map<String, Object> result = new HashMap<>();
            result.put("id", jsonNode.get("ID").asInt());
            result.put("accuracy", new BigDecimal(jsonNode.get("accuracy").asText()));
            result.put("reason", jsonNode.get("reason").asText());

            return result;

        } catch (Exception e) {
            System.err.println("AI判题失败：" + e.getMessage());
            e.printStackTrace();

            Map<String, Object> fallbackResult = new HashMap<>();
            fallbackResult.put("id", id);
            fallbackResult.put("accuracy", simpleCompare(correctAnswer, studentAnswer));
            fallbackResult.put("reason", "AI服务暂时不可用，使用简单对比");

            return fallbackResult;
        }
    }

    @Override
    public String generateExamReview(BigDecimal accuracy, int correctCount, int totalQuestions,
                                     String allQuestionsAndAnswers) {
        String msg = "你是一位教育专家，需要根据学生的考试情况生成综合评价。" +
                "以下是考试信息：\n" +
                "总题数：" + totalQuestions + "\n" +
                "正确数：" + correctCount + "\n" +
                "平均准确率：" + accuracy + "%\n\n" +
                "答题详情：\n" + allQuestionsAndAnswers + "\n\n" +
                "请根据以上信息，生成一份综合评价（50-100字），包括：" +
                "1. 整体表现评价" +
                "2. 知识掌握情况分析" +
                "3. 薄弱环节指出" +
                "4. 学习建议" +
                "要求：语气鼓励为主，评价客观专业，直接返回评价内容，不要包含任何标记。";

        try {
            String review = callAi(msg);
            System.out.println("AI综合评价：" + review);
            return review;
        } catch (Exception e) {
            System.err.println("AI生成评价失败：" + e.getMessage());
            e.printStackTrace();
            return fallbackReview(accuracy, correctCount, totalQuestions);
        }
    }

    /**
     * 调用 AI 服务
     */
    private String callAi(String message) throws JsonProcessingException {
        ArkService arkService = ArkService.builder()
                .apiKey(aiConfig.getApiKey())
                .baseUrl(aiConfig.getBaseUrl())
                .build();

        CreateResponsesRequest request = CreateResponsesRequest.builder()
                .model(aiConfig.getModel())
                .input(ResponsesInput.builder()
                        .addListItem(ItemEasyMessage.builder()
                                .role(ResponsesConstants.MESSAGE_ROLE_USER)
                                .content(MessageContent.builder()
                                        .addListItem(InputContentItemText.builder().text(message).build())
                                        .build())
                                .build())
                        .build())
                .thinking(ResponsesThinking.builder()
                        .type(ResponsesConstants.THINKING_TYPE_DISABLED)
                        .build())
                .build();

        ResponseObject resp = arkService.createResponse(request);
        arkService.shutdownExecutor();

        return extractTextFromResponse(resp.toString());
    }

    private String extractTextFromResponse(String response) {
        int start = response.indexOf("text='") + 6;
        int end = response.indexOf("', annotations");
        if (start > 5 && end > start) {
            return response.substring(start, end);
        }
        return response;
    }

    private BigDecimal simpleCompare(String correctAnswer, String studentAnswer) {
        if (correctAnswer == null || studentAnswer == null) {
            return BigDecimal.ZERO;
        }

        String correct = correctAnswer.trim().toLowerCase();
        String student = studentAnswer.trim().toLowerCase();

        if (correct.equals(student)) {
            return new BigDecimal("1.00");
        }

        if (correct.contains(student) || student.contains(correct)) {
            return new BigDecimal("0.80");
        }

        int matchCount = 0;
        int totalLength = Math.max(correct.length(), student.length());

        for (int i = 0; i < Math.min(correct.length(), student.length()); i++) {
            if (correct.charAt(i) == student.charAt(i)) {
                matchCount++;
            }
        }

        double similarity = (double) matchCount / totalLength;
        return new BigDecimal(String.format("%.2f", similarity));
    }

    /**
     * AI 不可用时的备用评价
     */
    private String fallbackReview(BigDecimal accuracy, int correctCount, int totalQuestions) {
        if (accuracy.compareTo(new BigDecimal("90")) >= 0) {
            return "优秀！您在本次考试中表现非常出色，答对了 " + correctCount + "/" + totalQuestions + " 道题，准确率 " + accuracy + "%。说明您对知识点掌握非常牢固，继续保持！";
        } else if (accuracy.compareTo(new BigDecimal("75")) >= 0) {
            return "良好！您答对了 " + correctCount + "/" + totalQuestions + " 道题，准确率 " + accuracy + "%。大部分知识点已掌握，建议针对薄弱环节进行针对性练习。";
        } else if (accuracy.compareTo(new BigDecimal("60")) >= 0) {
            return "及格。您答对了 " + correctCount + "/" + totalQuestions + " 道题，准确率 " + accuracy + "%。还需要加强学习，建议回顾本次考试中的错误题目，巩固相关知识点。";
        } else {
            return "需要努力。您答对了 " + correctCount + "/" + totalQuestions + " 道题，准确率 " + accuracy + "%。建议重新系统学习相关知识点，多做练习提高掌握程度。";
        }
    }
}