package com.MCP.backend.repositories;

import com.MCP.backend.entities.Course;
import com.MCP.backend.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepo extends JpaRepository<Course,String> {

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.videos WHERE c.courseId = :courseId")
    Optional<Course> findByIdWithVideos(@Param("courseId") String courseId);

}
