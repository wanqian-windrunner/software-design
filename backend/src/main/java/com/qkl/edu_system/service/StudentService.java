package com.qkl.edu_system.service;

import com.qkl.edu_system.pojo.Exam;
import com.qkl.edu_system.pojo.Question;
import com.qkl.edu_system.pojo.SubmitQuestionRequest;
import com.qkl.edu_system.pojo.User;

import java.util.List;
import java.util.Map;

public interface StudentService {

    User getProfile(Integer userId);

    void updateUsername(Integer userId, String username);

    void updatePassword(Integer userId, String password);

    void changeClazz(Integer userId, Integer clazzId);

    void deleteAccount(Integer userId);


    List<Exam> listExams(Integer userId, Integer clazzId);

    List<Question> getExamQuestions(Integer examId);
    Map<String, Object> submitQuestion(SubmitQuestionRequest request);
    void startExam(Integer examId, Integer studentId);
    Map<String, Object> finishExam(Integer examId, Integer studentId);

    List<Map<String, Object>> getExamsWithStatus(Integer clazzId, Integer studentId);
    Map<String, Object> getExamResult(Integer examId, Integer studentId);

    List<Map<String, Object>> getStudentAnswers(Integer examId, Integer studentId);

}