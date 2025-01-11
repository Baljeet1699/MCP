package com.MCP.backend.controllers;

import com.MCP.backend.AppConstants;
import com.MCP.backend.entities.Course;
import com.MCP.backend.entities.Video;
import com.MCP.backend.payloads.CourseDTO;
import com.MCP.backend.payloads.CustomMessage;
import com.MCP.backend.services.CourseService;
import com.MCP.backend.services.VideoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private CourseService courseService;

//    @PostMapping
//    public ResponseEntity<?> create(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("title")String title,
//            @RequestParam("description")String description
//    ){
//        Video video = new Video();
//        video.setTitle(title);
//        video.setDescription(description);
//        video.setId(UUID.randomUUID().toString());
//
//        Video savedVideo = this.videoService.save(video,file);
//        if(savedVideo != null){
//            return ResponseEntity.
//                    status(HttpStatus.OK)
//                    .body(video);
//        }else{
//            return ResponseEntity.
//                    status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(CustomMessage.builder()
//                            .message("Video not uploaded")
//                            .success(false)
//                            .build());
//
//        }
//    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description
    ) {
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setId(UUID.randomUUID().toString());

        Video savedVideo = this.videoService.save(video, file);
        if (savedVideo != null) {
            return ResponseEntity.
                    status(HttpStatus.OK)
                    .body(savedVideo);
        } else {
            return ResponseEntity.
                    status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomMessage.builder()
                            .message("Video not uploaded")
                            .success(false)
                            .build());
        }
    }

    @PostMapping("/course")
    public ResponseEntity<?> createCourse(
            @RequestParam("courseData") String courseData,
            @RequestParam("files") List<MultipartFile> files
    ) throws JsonProcessingException {
        CourseDTO courseDTO = new ObjectMapper().readValue(courseData, CourseDTO.class);

        // Map files to their corresponding videos using extracted ID
        Map<String, MultipartFile> fileMap = new HashMap<>();
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.contains("_")) {
                // Extract video ID from the file name
                String fileId = originalFilename.split("_", 2)[0];
                fileMap.put(fileId, file);
            }
        }

        Course savedCourse = courseService.save(courseDTO, fileMap);

        if (savedCourse != null) {
            return ResponseEntity.status(HttpStatus.OK).body(savedCourse);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomMessage.builder()
                            .message("Course not saved")
                            .success(false)
                            .build());
        }
    }


    @PutMapping("/course")
    public ResponseEntity<?> updateCourse(
            @RequestParam("courseData") String courseData,
            @RequestParam(name = "files", required = false) List<MultipartFile> files
    ) throws JsonProcessingException {
        CourseDTO courseDTO = new ObjectMapper().readValue(courseData, CourseDTO.class);

        // Map files to their corresponding videos
        Map<String, MultipartFile> fileMap = new HashMap<>();
        if (files != null) {
            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();
                if (originalFilename != null && originalFilename.contains("_")) {
                    String fileId = originalFilename.split("_", 2)[0];
                    fileMap.put(fileId, file);
                }
            }
        }

        Course updatedCourse = courseService.updateCourse(courseDTO, fileMap, courseDTO.getCourseId());

        if (updatedCourse != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updatedCourse);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomMessage.builder()
                            .message("Course not updated")
                            .success(false)
                            .build());
        }
    }



//    @PostMapping("/course")
//    public ResponseEntity<?> createCourse(
//            @RequestParam("courseData") String courseData,
//            @RequestParam("files") List<MultipartFile> files
//            ) throws JsonProcessingException {
//        CourseDTO courseDTO = new ObjectMapper().readValue(courseData, CourseDTO.class);
//        Course savedCourse = this.courseService.save(courseDTO, files);
//        if (savedCourse != null) {
//            return ResponseEntity.status(HttpStatus.OK).body(savedCourse);
//        } else {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(CustomMessage.builder()
//                            .message("Course not saved")
//                            .success(false)
//                            .build());
//        }
//
//    }


    @GetMapping("/courses")
    public ResponseEntity<?> getAllCourses(){

        List<Course> courses = this.courseService.getAllCourses();
        if(courses != null){
            return ResponseEntity.
                    status(HttpStatus.OK)
                    .body(courses);
        }else{
            return ResponseEntity.
                    status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomMessage.builder()
                            .message("No Courses found")
                            .success(false)
                            .build());

        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getCourse(
            @PathVariable("courseId") String courseId
    ){
        Course course = this.courseService.getCourse(courseId);

        return ResponseEntity.
                status(HttpStatus.OK)
                .body(course);
    }


    @DeleteMapping("/course/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable String courseId) {
        try {
            courseService.deleteCourse(courseId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 No Content
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomMessage.builder()
                            .message("Course not found")
                            .success(false)
                            .build());
        }
    }

//    @PutMapping("/course")
//    public ResponseEntity<?> updateCourse(@RequestParam("courseData") String courseData,@RequestParam(name = "files", required = false) List<MultipartFile> files) throws JsonProcessingException {
//
//        CourseDTO courseDTO = new ObjectMapper().readValue(courseData, CourseDTO.class);
//        Course updatedCourse = this.courseService.save(courseDTO,files);
//        if (updatedCourse != null) {
//            return ResponseEntity.status(HttpStatus.OK).body(updatedCourse);
//        } else {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(CustomMessage.builder()
//                            .message("Course not saved")
//                            .success(false)
//                            .build());
//        }
//    }


    @GetMapping("/stream/{videoId}")
    public ResponseEntity<Resource> stream(
            @PathVariable String videoId
    ){
        Video video = videoService.getVedio(videoId);

        String contentType = video.getContentType();
        String filePath = video.getFilePath();

        if(contentType == null){
            contentType = "application/octet-stream";
        }

       Resource resource = new FileSystemResource(filePath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @GetMapping
    public List<Video> getAll(){
        return videoService.getAll();
    }

    @GetMapping("/stream/range/{videoId}")
    public ResponseEntity<Resource> getVideoInChunks(
            @PathVariable String videoId,
            @RequestHeader(value = "Range",required = false)String range
    ){
        Video video = videoService.getVedio(videoId);

        Path path = Paths.get(video.getFilePath());
        String contentType = video.getContentType();

        Resource resource = new FileSystemResource(path);

        if(contentType == null){
            contentType= "application/octet-stream";
        }

        long fileLength = path.toFile().length();

        if(range == null){
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        }

        long rangeStart;
        long rangeEnd;

        String[] ranges = range.replace("bytes=","").split("-");
        rangeStart = Long.parseLong(ranges[0]);

        rangeEnd = rangeStart + AppConstants.CHUNK_SIZE - 1;

        if (rangeEnd >= fileLength){
            rangeEnd = fileLength - 1;
        }

        InputStream inputStream;
        try{
            inputStream = Files.newInputStream(path);
            inputStream.skip(rangeStart);

            long contentLength = rangeEnd - rangeStart + 1;

            byte[] data = new byte[(int)contentLength];
            int read = inputStream.read(data,0,data.length);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Range","bytes "+rangeStart+"-"+rangeEnd+"/"+fileLength);
            httpHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate");
            httpHeaders.add("Pragma", "no-cache");
            httpHeaders.add("Expires", "0");
            httpHeaders.add("X-Content-Type-Options", "nosniff");
            httpHeaders.setContentLength(contentLength);

            return ResponseEntity
                    .status(HttpStatus.PARTIAL_CONTENT)
                    .headers(httpHeaders)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(new ByteArrayResource(data));
        }catch (IOException exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


}
