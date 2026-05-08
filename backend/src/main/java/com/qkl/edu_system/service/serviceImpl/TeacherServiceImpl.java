package com.qkl.edu_system.service.serviceImpl;

import com.qkl.edu_system.mapper.ExamMapper;
import com.qkl.edu_system.mapper.QuestionMapper;
import com.qkl.edu_system.mapper.ScoreMapper;
import com.qkl.edu_system.mapper.UserMapper;
import com.qkl.edu_system.pojo.*;
import com.qkl.edu_system.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    /* ================= 个人信息 ================= */

    @Override
    public User getProfile(Integer teacherId) {
        return userMapper.selectById(teacherId);
    }

    @Override
    public void updateUsername(Integer teacherId, String username) {
        userMapper.updateUsername(teacherId, username);
    }

    @Override
    public void updatePassword(Integer teacherId, String password) {
        userMapper.updatePassword(teacherId, password);
    }

    @Override
    public void deleteAccount(Integer teacherId) {
        userMapper.deleteById(teacherId);
    }

    /* ================= 学生管理 ================= */

    @Override
    public List<User> listStudents(Integer clazzId) {
        return userMapper.selectStudentsByClazz(clazzId);
    }

    public void approveStudent(Integer studentId) {
        userMapper.updateVerify(studentId, 1);
    }

    @Override
    public void deleteExam(Integer examId) {
        examMapper.deleteById(examId);
    }


    @Override
    public void updateVerify(Integer studentId, Integer verify) {
        userMapper.updateVerify(studentId, verify);
    }

    @Override
    public void removeStudent(Integer clazzId, Integer studentId) {
        // 查询该学生是否属于该班级
        User student = userMapper.selectById(studentId);
        if (student == null || !student.getClazzId().equals(clazzId)) {
            throw new RuntimeException("该学生不在当前班级中");
        }

        // 将学生的 clazz_id 改为 0，verify 状态恢复为待审核
        student.setClazzId(0);
        student.setVerify(0);
        userMapper.updateById(student);
    }

    /**
     * ✅ 使用 score 表代替不存在的 exam_record
     */
    @Override
    public List<ExamRecord> getStudentExamRecords(Integer studentId) {
        List<Score> scores = scoreMapper.selectByStudentId(studentId);
        return scores.stream().map(score -> {
            ExamRecord record = new ExamRecord();
            record.setId(score.getTestId());
            record.setStudentId(score.getId());
            record.setExamId(score.getExamId());
            record.setAccuracy(score.getAccuracy() != null ? score.getAccuracy().doubleValue() : null);
            record.setAiReview(score.getAiReview());
            record.setCreateTime(score.getCreateTime());
            return record;
        }).collect(Collectors.toList());
    }

    /* ================= 考试管理 ================= */

    @Override
    public List<Exam> listExams(Integer clazzId) {
        return examMapper.selectByClazzId(clazzId);
    }

    @Override
    public void createExam(Exam exam) {
        examMapper.insert(exam);
    }

    @Override
    public void updateExamState(Integer examId, Integer state) {
        examMapper.updateState(examId, state);
    }

    /* ================= 题库管理 ================= */

    @Override
    public List<Question> listQuestions(Integer examId) {
        // 老师端查看所有题目（包括禁用的）
        return questionMapper.findAllByExamId(examId);
    }

    @Override
    public void addQuestion(Question question) {
        log.info("添加题目: {}", question);
        question.setState(question.getState() != null ? question.getState() : 1);
        questionMapper.insert(question);
    }

    @Override
    public void updateQuestion(Question question) {
        questionMapper.update(question);
    }

    @Override
    public void deleteQuestion(Integer examId, Integer questionId) {
        questionMapper.deleteByExamIdAndQuestionId(examId, questionId);
    }

    @Override
    public void updateQuestionState(Integer examId, Integer questionId, Integer state) {
        questionMapper.updateState(examId, questionId, state);
    }


    @Override
    public List<Score> getExamScores(Integer examId) {
        return scoreMapper.findByExamId(examId);
    }

    @Override
    public Score getScoreDetail(Integer testId) {
        return scoreMapper.findById(testId);
    }






}