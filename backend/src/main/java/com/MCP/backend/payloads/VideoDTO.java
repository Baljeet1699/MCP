package com.MCP.backend.payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoDTO {
    private String id;
    private String title;
    private String description;
}
