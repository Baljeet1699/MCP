package com.MCP.backend.services.imple;

import com.MCP.backend.entities.Video;
import com.MCP.backend.repositories.VideoRepo;
import com.MCP.backend.services.VideoService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class VideoServiceImple implements VideoService {

    @Value("${files.video}")
    String DIR;

    @Autowired
    private VideoRepo videoRepo;

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
    public Video save(Video video, MultipartFile file) {

        try{
           String filename =  file.getOriginalFilename();
           String contentType = file.getContentType();
           InputStream inputStream = file.getInputStream();

           String cleanFileName = StringUtils.cleanPath(filename);
           String cleanFolder = StringUtils.cleanPath(DIR);

            Path path = Paths.get(cleanFolder,cleanFileName);
            System.out.println(path);

            Files.copy(inputStream,path, StandardCopyOption.REPLACE_EXISTING);

            video.setContentType(contentType);
            video.setFilePath(path.toString());

            return this.videoRepo.save(video);

        }catch (IOException exception){
          exception.printStackTrace();
          return null;
        }

    }



    @Override
    public Video getVedio(String vedioId) {
        return videoRepo.findById(vedioId).orElseThrow(() -> new RuntimeException("video not found"));
    }

    @Override
    public List<Video> getAll() {
        return videoRepo.findAll();
    }
}
