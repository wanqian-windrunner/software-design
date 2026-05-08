package com.qkl.edu_system.service.serviceImpl;

import com.qkl.edu_system.mapper.*;
import com.qkl.edu_system.pojo.*;
import com.qkl.edu_system.service.AiService;
import com.qkl.edu_system.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private StudentAnswerMapper studentAnswerMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private AiService aiService;

    /* ================= 个人信息 ================= */

    @Override
    public User getProfile(Integer userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public void updateUsername(Integer userId, String username) {
        userMapper.updateUsername(userId, username);
    }

    @Override
    public void updatePassword(Integer userId, String password) {
        userMapper.updatePassword(userId, password);
    }

    @Override
    public void changeClazz(Integer userId, Integer clazzId) {
        userMapper.updateClazzId(userId, clazzId);
    }

    @Override
    public void deleteAccount(Integer userId) {
        userMapper.deleteById(userId);
    }

    /* ================= 考试管理 ================= */

    @Override
    public List<Exam> listExams(Integer userId, Integer clazzId) {

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // ✅ 关键校验
        if (user.getVerify() == null || user.getVerify() != 1) {
            throw new RuntimeException("未通过审核或未选择班级，无法查看考试");
        }

        return examMapper.selectByClazzId(clazzId);
    }

    /* ================= 答题功能 ================= */

    @Override
    public List<Question> getExamQuestions(Integer examId) {
        // 学生端只查看启用状态的题目
        return questionMapper.findByExamId(examId);
    }

    @Override
    public List<Map<String, Object>> getStudentAnswers(Integer examId, Integer studentId) {
        List<StudentAnswer> answers = studentAnswerMapper.findByExamAndStudent(examId, studentId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (StudentAnswer answer : answers) {
            Map<String, Object> map = new HashMap<>();
            map.put("questionId", answer.getQuestionId());
            map.put("studentAnswer", answer.getStudentAnswer());
            map.put("isCorrect", answer.getIsCorrect());
            map.put("accuracy", answer.getAccuracy());
            result.add(map);
        }

        return result;
    }

    @Override
    public Map<String, Object> submitQuestion(SubmitQuestionRequest request) {
        Question question = questionMapper.findByExamIdAndQuestionId(
                request.getExamId(), request.getQuestionId());

        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 调用 AI 判题
        Map<String, Object> aiResult = aiService.judgeAnswer(
                question.getQuestionId(),
                question.getQuestion(),
                question.getAnswer(),
                request.getAnswer()
        );

        BigDecimal accuracy = (BigDecimal) aiResult.get("accuracy");
        String reason = (String) aiResult.get("reason");
        boolean isCorrect = accuracy.compareTo(new BigDecimal("0.6")) >= 0;

        // 保存答题记录
        StudentAnswer studentAnswer = new StudentAnswer();
        studentAnswer.setExamId(request.getExamId());
        studentAnswer.setStudentId(request.getStudentId());
        studentAnswer.setQuestionId(request.getQuestionId());
        studentAnswer.setStudentAnswer(request.getAnswer());
        studentAnswer.setIsCorrect(isCorrect ? 1 : 0);
        studentAnswer.setAccuracy(accuracy.multiply(new BigDecimal("100")));
        studentAnswer.setReason(reason);

        studentAnswerMapper.insertOrUpdate(studentAnswer);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("isCorrect", isCorrect);
        result.put("accuracy", accuracy.multiply(new BigDecimal("100")));
        result.put("reason", reason);
        result.put("correctAnswer", question.getAnswer());
//        result.put("analysis", aiResult.get("analysis"));

        return result;
    }



    @Override
    public void startExam(Integer examId, Integer studentId) {
        Score score = new Score();
        score.setExamId(examId);
        score.setId(studentId);
        score.setAccuracy(BigDecimal.ZERO);

        scoreMapper.insertOrUpdate(score);
    }

    @Override
    public Map<String, Object> finishExam(Integer examId, Integer studentId) {
        List<StudentAnswer> answers = studentAnswerMapper.findByExamAndStudent(examId, studentId);

        int totalQuestions = answers.size();
        if (totalQuestions == 0) {
            throw new RuntimeException("没有答题记录");
        }

        BigDecimal totalAccuracy = BigDecimal.ZERO;
        int correctCount = 0;
        StringBuilder allDetails = new StringBuilder();

        for (int i = 0; i < answers.size(); i++) {
            StudentAnswer answer = answers.get(i);
            totalAccuracy = totalAccuracy.add(answer.getAccuracy());
            if (answer.getIsCorrect() == 1) {
                correctCount++;
            }

            // 构建答题详情给 AI 分析
            Question question = questionMapper.findByExamIdAndQuestionId(examId, answer.getQuestionId());
            if (question != null) {
                allDetails.append("题").append(i + 1).append("：").append(question.getQuestion())
                        .append("\n学生答案：").append(answer.getStudentAnswer())
                        .append("\n正确答案：").append(question.getAnswer())
                        .append("\n准确率：").append(answer.getAccuracy()).append("%")
                        .append("\n判断原因：").append(answer.getReason())
                        .append("\n\n");
            }
        }

        BigDecimal avgAccuracy = totalAccuracy.divide(
                new BigDecimal(totalQuestions), 2, RoundingMode.HALF_UP);

        // 调用 AI 生成综合评价
        String aiReview;
        try {
            aiReview = aiService.generateExamReview(avgAccuracy, correctCount, totalQuestions, allDetails.toString());
        } catch (Exception e) {
            System.err.println("AI生成评价失败，使用默认评价：" + e.getMessage());
            aiReview = "您答对了 " + correctCount + "/" + totalQuestions + " 道题，准确率 " + avgAccuracy + "%。";
        }

        // 更新成绩表
        scoreMapper.updateScore(examId, studentId, avgAccuracy, aiReview);

        Map<String, Object> result = new HashMap<>();
        result.put("totalQuestions", totalQuestions);
        result.put("correctCount", correctCount);
        result.put("avgAccuracy", avgAccuracy);
        result.put("aiReview", aiReview);

        return result;
    }

    private BigDecimal calculateAccuracy(String correctAnswer, String studentAnswer) {
        if (correctAnswer == null || studentAnswer == null) {
            return BigDecimal.ZERO;
        }

        String correct = correctAnswer.trim().toLowerCase();
        String student = studentAnswer.trim().toLowerCase();

        if (correct.equals(student)) {
            return new BigDecimal("100.00");
        }

        if (correct.contains(student) || student.contains(correct)) {
            return new BigDecimal("80.00");
        }

        int matchCount = 0;
        int totalLength = Math.max(correct.length(), student.length());

        for (int i = 0; i < Math.min(correct.length(), student.length()); i++) {
            if (correct.charAt(i) == student.charAt(i)) {
                matchCount++;
            }
        }

        double similarity = (double) matchCount / totalLength * 100;
        return new BigDecimal(String.format("%.2f", similarity));
    }


    private String generateAIReview(BigDecimal accuracy, int correctCount, int totalQuestions) {
        if (accuracy.compareTo(new BigDecimal("90")) >= 0) {
            return "优秀！表现非常好，对知识点掌握牢固。";
        } else if (accuracy.compareTo(new BigDecimal("75")) >= 0) {
            return "良好！大部分知识点已掌握，继续加油！";
        } else if (accuracy.compareTo(new BigDecimal("60")) >= 0) {
            return "及格，还需要加强学习，部分知识点需要巩固。";
        } else {
            return "需要更加努力，建议重新学习相关知识点。";
        }
    }


    // StudentServiceImpl.java
    @Override
    public List<Map<String, Object>> getExamsWithStatus(Integer clazzId, Integer studentId) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            // 获取该班级所有启用的考试
            List<Exam> exams = examMapper.findByClazzIdAndState(clazzId, 1);

            if (exams == null || exams.isEmpty()) {
                return result;
            }

            for (Exam exam : exams) {
                Map<String, Object> examMap = new HashMap<>();
                examMap.put("examId", exam.getExamId());
                examMap.put("examName", exam.getExamName());
                examMap.put("state", exam.getState());
                examMap.put("createTime", exam.getCreateTime());

                // 查询该学生的考试记录
                Score score = scoreMapper.findByExamAndStudent(exam.getExamId(), studentId);

                if (score != null && score.getAiReview() != null && !score.getAiReview().isEmpty()) {
                    // 已完成
                    examMap.put("finished", true);
                    examMap.put("started", true);
                    examMap.put("accuracy", score.getAccuracy());
                } else if (score != null) {
                    // 进行中
                    examMap.put("finished", false);
                    examMap.put("started", true);
                    examMap.put("accuracy", null);
                } else {
                    // 未开始
                    examMap.put("finished", false);
                    examMap.put("started", false);
                    examMap.put("accuracy", null);
                }

                result.add(examMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Map<String, Object> getExamResult(Integer examId, Integer studentId) {
        Score score = scoreMapper.findByExamAndStudent(examId, studentId);
        if (score == null) {
            throw new RuntimeException("未找到考试成绩");
        }

        Exam exam = examMapper.findById(examId);

        Map<String, Object> result = new HashMap<>();
        result.put("examName", exam.getExamName());
        result.put("accuracy", score.getAccuracy());
        result.put("aiReview", score.getAiReview());
        result.put("createTime", score.getCreateTime());
        result.put("updateTime", score.getUpdateTime());

        return result;
    }
}