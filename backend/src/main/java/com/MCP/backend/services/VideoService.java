package com.MCP.backend.services;

import com.MCP.backend.entities.Video;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {

    Video save(Video video, MultipartFile file);

    Video getVedio(String vedioId);

    List<Video> getAll();
}
