package com.qkl.edu_system.mapper;

import com.qkl.edu_system.pojo.StudentAnswer;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface StudentAnswerMapper {

    @Insert("INSERT INTO student_answer (exam_id, student_id, question_id, student_answer, " +
            "is_correct, accuracy, reason, create_time) VALUES (#{examId}, #{studentId}, " +
            "#{questionId}, #{studentAnswer}, #{isCorrect}, #{accuracy}, #{reason}, NOW()) " +
            "ON DUPLICATE KEY UPDATE student_answer = #{studentAnswer}, " +
            "is_correct = #{isCorrect}, accuracy = #{accuracy}, reason = #{reason}")
    void insertOrUpdate(StudentAnswer answer);

    @Select("SELECT * FROM student_answer WHERE exam_id = #{examId} AND student_id = #{studentId}")
    List<StudentAnswer> findByExamAndStudent(@Param("examId") Integer examId,
                                             @Param("studentId") Integer studentId);
}