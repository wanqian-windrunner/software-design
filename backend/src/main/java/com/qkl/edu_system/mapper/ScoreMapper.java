package com.qkl.edu_system.mapper;

import com.qkl.edu_system.pojo.Score;
import org.apache.ibatis.annotations.*;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ScoreMapper {

    @Select("SELECT s.*, u.username, e.exam_name " +
            "FROM score s " +
            "LEFT JOIN user u ON s.id = u.id " +
            "LEFT JOIN exam e ON s.exam_id = e.exam_id " +
            "WHERE s.exam_id = #{examId} " +
            "ORDER BY s.create_time DESC")
    List<Score> findByExamId(@Param("examId") Integer examId);

    @Select("SELECT s.*, u.username, e.exam_name " +
            "FROM score s " +
            "LEFT JOIN user u ON s.id = u.id " +
            "LEFT JOIN exam e ON s.exam_id = e.exam_id " +
            "WHERE s.test_id = #{testId}")
    Score findById(@Param("testId") Integer testId);

    @Insert("INSERT INTO score (exam_id, id, accuracy, create_time, update_time) " +
            "VALUES (#{examId}, #{id}, #{accuracy}, NOW(), NOW()) " +
            "ON DUPLICATE KEY UPDATE update_time = NOW()")
    void insertOrUpdate(Score score);

    @Update("UPDATE score SET accuracy = #{accuracy}, ai_review = #{aiReview}, " +
            "update_time = NOW() WHERE exam_id = #{examId} AND id = #{id}")
    void updateScore(@Param("examId") Integer examId,
                     @Param("id") Integer studentId,
                     @Param("accuracy") BigDecimal accuracy,
                     @Param("aiReview") String aiReview);

    @Delete("DELETE FROM score WHERE exam_id = #{examId}")
    void deleteByExamId(@Param("examId") Integer examId);

    @Select("SELECT * FROM score WHERE id = #{studentId}")
    List<Score> selectByStudentId(Integer studentId);

    @Select("SELECT * FROM score WHERE exam_id = #{examId} AND id = #{studentId}")
    Score findByExamAndStudent(@Param("examId") Integer examId,
                               @Param("studentId") Integer studentId);
}