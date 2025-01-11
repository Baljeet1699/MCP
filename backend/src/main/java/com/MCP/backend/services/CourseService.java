package com.MCP.backend.services;

import com.MCP.backend.entities.Course;
import com.MCP.backend.payloads.CourseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CourseService {

    Course save(CourseDTO courseDTO, Map<String, MultipartFile> files);

    Course getCourse(String courseId);

    void deleteCourse(String courseId);

    Course updateCourse(CourseDTO courseDTO,Map<String, MultipartFile> files,String courseId);

    List<Course> getAllCourses();
}
