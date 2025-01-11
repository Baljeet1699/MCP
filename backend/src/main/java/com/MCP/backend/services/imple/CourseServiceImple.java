package com.MCP.backend.services.imple;

import com.MCP.backend.entities.Course;
import com.MCP.backend.entities.Video;
import com.MCP.backend.exceptions.VideoUploadException;
import com.MCP.backend.payloads.CourseDTO;
import com.MCP.backend.payloads.VideoDTO;
import com.MCP.backend.repositories.CourseRepo;
import com.MCP.backend.repositories.VideoRepo;
import com.MCP.backend.services.CourseService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseServiceImple implements CourseService {

    private final S3Client client;

    @Autowired
    public CourseServiceImple(S3Client client) {
        this.client = client;
    }

    @Value("${app.s3.bucket}")
    private String bucket;

    @Autowired
    CourseRepo courseRepo;

    @Autowired
    VideoRepo videoRepo;

    @Value("${files.video}")
    String DIR;


    @PostConstruct
    public void init(){

        File file = new File(DIR);

        if (!file.exists()){
            file.mkdir();
            System.out.println("Folder created");
        }else {
            System.out.println("Folder already created");
        }
    }


    @Override
    public Course save(CourseDTO courseDTO, Map<String, MultipartFile> files) {
        Course course = new Course();
        course.setCourseId(courseDTO.getCourseId() == null ? UUID.randomUUID().toString() : courseDTO.getCourseId());
        course.setCourseName(courseDTO.getCourseName());
        course.setCourseDesc(courseDTO.getCourseDesc());

        List<Video> videoList = new ArrayList<>();
        for (VideoDTO videoDTO : courseDTO.getVideos()) {
            Video video = new Video();
            video.setId(videoDTO.getId() == null ? UUID.randomUUID().toString() : videoDTO.getId());
            video.setTitle(videoDTO.getTitle());
            video.setDescription(videoDTO.getDescription());
            video.setCourse(course);

            MultipartFile file = files != null ? files.get(video.getId()) : null;
            if (file != null) {
                save(video, file,course.getCourseName());
            }

            videoList.add(video);
        }

        course.setVideos(videoList);
        return courseRepo.save(course);
    }


    @Override
    @Transactional
    public Course updateCourse(CourseDTO courseDTO, Map<String, MultipartFile> files, String courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setCourseName(courseDTO.getCourseName());
        course.setCourseDesc(courseDTO.getCourseDesc());

        // Identify videos to retain and remove
        List<String> videoIdsToKeep = courseDTO.getVideos().stream()
                .map(VideoDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Remove videos not in the updated list
        List<Video> videosToRemove = course.getVideos().stream()
                .filter(video -> !videoIdsToKeep.contains(video.getId()))
                .collect(Collectors.toList());

        for (Video videoToRemove : videosToRemove) {
            deleteOldFile(videoToRemove.getFilePath()); // Delete file from storage
            videoRepo.delete(videoToRemove);
        }

        course.getVideos().removeAll(videosToRemove);

        // Handle new or updated videos
        for (VideoDTO videoDTO : courseDTO.getVideos()) {
            Video video = course.getVideos().stream()
                    .filter(v -> videoDTO.getId() != null && videoDTO.getId().equals(v.getId()))
                    .findFirst()
                    .orElseGet(() -> {
                        Video newVideo = new Video();
                        newVideo.setId(videoDTO.getId()); // Use client-generated ID to match files map
                        newVideo.setCourse(course);
                        course.getVideos().add(newVideo);
                        return newVideo;
                    });

            video.setTitle(videoDTO.getTitle());
            video.setDescription(videoDTO.getDescription());

            // Save file if available
            MultipartFile file = files.get(videoDTO.getId()); // Match using videoDTO.getId()
            if (file != null) {
                save(video, file,course.getCourseName());
            }
        }

        return courseRepo.save(course);
    }



    private void save(Video video, MultipartFile file, String courName) {
        if (file != null && !file.isEmpty()) {
            try {
                // Generate a unique filename
                String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
                Path path = Paths.get(DIR, filename);

                // Save the file to disk
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                // Uploading the video to S3 bucket in AWS
               // String s3Path = uploadVideo(courName,file);

                // Update video entity with file details
                //video.setFilePath(s3Path);
                video.setFilePath(String.valueOf(path));
                video.setContentType(file.getContentType());

                System.out.println("File saved successfully: " + path);
            } catch (IOException e) {
                throw new RuntimeException("Error saving file: " + e.getMessage(), e);
            }
        } else {
            System.err.println("File is null or empty for video: " + video.getTitle());
        }
    }



    @Override
    public Course getCourse(String courseId) {
        return courseRepo.findByIdWithVideos(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
    }



    @Override
    public void deleteCourse(String courseId) {
        Course course = this.courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        for (Video video : course.getVideos()) {
            deleteOldFile(video.getFilePath());

            // delete video file from S3 bucket of AWS
            //deleteFile(bucket,video.getFilePath());

            videoRepo.delete(video);
        }

        courseRepo.delete(course);
    }


    private void deleteOldFile(String filePath) {
        if (filePath != null && !filePath.isEmpty()) {
            Path path = Paths.get(filePath);
            try {
                Files.deleteIfExists(path);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public List<Course> getAllCourses() {
        return this.courseRepo.findAll();
    }



    public String uploadVideo(String courseName, MultipartFile file) {

        String fileName = courseName + "/" + file.getOriginalFilename();

        try {

            // Create a PutObjectRequest
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();

            // Upload the file
            client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return fileName;
        } catch (IOException e) {
            throw new VideoUploadException("error in uploading image"+e.getMessage());
        }

    }

    public void deleteFile(String bucketName, String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting file: " + key, e);
        }
    }


    public String preSignedUrl(String fileName) {

        try (S3Presigner presigner = S3Presigner.create()) {
            // Create a GetObjectRequest
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();

            // Create a PresignRequest
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(2))
                    .getObjectRequest(getObjectRequest)
                    .build();

            // Generate the Pre-Signed URL
            URL presignedUrl = presigner.presignGetObject(presignRequest).url();

            System.out.println("Pre-Signed URL: " + presignedUrl);
            return presignedUrl.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while generating pre-signed URL for file: " + fileName, e);
        }

    }


    public String getImageUrlByName(String fileName) {

        try (S3Presigner presigner = S3Presigner.create()) {
            // Create a GetObjectRequest
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();

            // Create a PresignRequest
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15)) // Set expiration time for the URL
                    .getObjectRequest(getObjectRequest)
                    .build();

            // Generate the pre-signed URL
            URL presignedUrl = presigner.presignGetObject(presignRequest).url();
            return presignedUrl.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while generating pre-signed URL for file: " + fileName, e);
        }
    }

    public List<String> allFiles() {

        try (S3Presigner presigner = S3Presigner.create()) {
            // Create a request to list objects in the bucket
            ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .build();

            // List objects in the bucket
            ListObjectsV2Response listObjectsV2Response = client.listObjectsV2(listObjectsV2Request);

            // Generate pre-signed URLs for each object
            List<String> listFileUrls = listObjectsV2Response.contents().stream()
                    .map(item -> generatePreSignedUrl(presigner, bucket, item.key()))
                    .collect(Collectors.toList());

            return listFileUrls;
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching all files", e);
        }
    }


    private String generatePreSignedUrl(S3Presigner presigner, String bucketName, String key) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15)) // Pre-signed URL valid for 15 minutes
                .getObjectRequest(req -> req.bucket(bucketName).key(key))
                .build();

        URL presignedUrl = presigner.presignGetObject(presignRequest).url();
        return presignedUrl.toString();
    }

}
