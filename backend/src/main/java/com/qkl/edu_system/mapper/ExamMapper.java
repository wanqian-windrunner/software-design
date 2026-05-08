package com.qkl.edu_system.mapper;

import com.qkl.edu_system.pojo.Exam;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface ExamMapper {

    @Select("""
        SELECT exam_id, clazz_id, state, create_time, update_time
        FROM exam
        WHERE exam_id = #{examId}
    """)
    Exam selectById(Integer examId);

    @Select("SELECT exam_id, exam_name, clazz_id, state, create_time, update_time FROM exam WHERE clazz_id = #{clazzId} ORDER BY create_time DESC")
    List<Exam> selectByClazzId(@Param("clazzId") Integer clazzId);

    @Insert("INSERT INTO exam (exam_name, clazz_id, state, create_time, update_time) " +
            "VALUES (#{examName}, #{clazzId}, #{state}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "examId")
    void insert(Exam exam);

    @Update("""
        UPDATE exam
        SET state = #{state}
        WHERE exam_id = #{examId}
    """)
    void updateState(@Param("examId") Integer examId,
                     @Param("state") Integer state);


    @Delete("DELETE FROM exam WHERE exam_id = #{examId}")
    void deleteById(Integer examId);

    @Select("SELECT * FROM exam WHERE exam_id = #{examId}")
    Exam findById(@Param("examId") Integer examId);

    @Select("SELECT * FROM exam WHERE clazz_id = #{clazzId} ORDER BY create_time DESC")
    List<Exam> findByClazzId(@Param("clazzId") Integer clazzId);


    @Update("UPDATE exam SET exam_name = #{examName}, update_time = NOW() WHERE exam_id = #{examId}")
    void updateName(@Param("examId") Integer examId, @Param("examName") String examName);

    @Select("SELECT * FROM exam WHERE clazz_id = #{clazzId} AND state = #{state} ORDER BY create_time DESC")
    List<Exam> findByClazzIdAndState(@Param("clazzId") Integer clazzId,
                                     @Param("state") Integer state);

}