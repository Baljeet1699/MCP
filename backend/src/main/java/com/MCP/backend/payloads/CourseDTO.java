package com.MCP.backend.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseDTO {
    private String courseId;
    private String courseName;
    private String courseDesc;
    private List<VideoDTO> videos;
}
