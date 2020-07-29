package com.example.demo.srv;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.bean.FileException;
import com.example.demo.bean.HttpResult;

@Service
public class FileService {

    public HttpResult<String> uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new HttpResult<String>(50001, "上传的文件为空", null);
        }

        String filePath = new File("files").getAbsolutePath();
        File fileUpload = new File(filePath);
        if (!fileUpload.exists()) {
            fileUpload.mkdirs();
        }

        fileUpload = new File(filePath, System.currentTimeMillis() + "_" + file.getOriginalFilename());
        if (fileUpload.exists()) {
            return new HttpResult<String>(50002, "上传的文件已存在", null);
        }

        try {
            file.transferTo(fileUpload);
            return new HttpResult<String>(200, "上传成功", fileUpload.getName());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return new HttpResult<String>(50003, "上传失败", null);
        }
    }

    /**
     * 加载文件
     * 
     * @param fileName 文件名
     * @return 文件
     */
    public Resource loadFileAsResource(String fileName) {
        Path fileStorageLocation = Paths.get("./files").toAbsolutePath().normalize();
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileException("File not found " + fileName, ex);
        }
    }

}
