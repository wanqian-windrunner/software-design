package com.qkl.edu_system.service;

import java.math.BigDecimal;
import java.util.Map;

public interface AiService {
    Map<String, Object> judgeAnswer(Integer id, String question, String correctAnswer, String studentAnswer);

    // 新增：生成考试综合评价
    String generateExamReview(BigDecimal accuracy, int correctCount, int totalQuestions,
                              String allQuestionsAndAnswers);
}