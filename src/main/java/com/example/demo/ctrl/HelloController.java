package com.example.demo.ctrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.bean.HttpResult;
import com.example.demo.bean.User;
import com.example.demo.srv.FileService;

@RestController
public class HelloController<E> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private FileService fileSrv;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public HttpResult<List<User>> listUser() {
        ArrayList<User> userList = new ArrayList<User>(2);
        userList.add(new User(1, 22, "易拉宝1"));
        userList.add(new User(2, 22, "易拉宝2"));
        userList.add(new User(3, 22, "易拉宝3"));
        return new HttpResult<List<User>>(200, "get list success.", userList);
    }

    @RequestMapping(value = "/user/rp", method = RequestMethod.GET)
    public HttpResult<User> getReqParaUser(@RequestParam(value = "id", required = true) int id) {
        HttpResult<User> httpResult = new HttpResult<User>(200, "get success.", new User(id, 22, "易拉宝" + id));
        if (!(id >= 1 && id <= 3)) {
            httpResult.setCode(40001);
            httpResult.setMsg("用户不存在");
            httpResult.setData(null);
        }
        return httpResult;
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public HttpResult<User> getUser(@PathVariable("id") int id) {
        HttpResult<User> httpResult = new HttpResult<User>(200, "get success.", new User(id, 22, "易拉宝" + id));
        if (!(id >= 1 && id <= 3)) {
            httpResult.setCode(40001);
            httpResult.setMsg("用户不存在");
            httpResult.setData(null);
        }
        return httpResult;
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public HttpResult<User> deleteUser(@PathVariable("id") int id) {
        HttpResult<User> httpResult = new HttpResult<User>(200, "delete success.", new User(1, 22, "易拉宝"));
        if (id >= 1 && id <= 3) {
            httpResult.getData().setId(id);
            httpResult.getData().setName("易拉宝" + id);
        } else {
            httpResult.setCode(40001);
            httpResult.setMsg("用户不存在");
            httpResult.setData(null);
        }
        return httpResult;
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public HttpResult<User> postUser(@RequestBody User user) {
        if (user.getName() == null) {
            return new HttpResult<User>(50001, "参数错误", null);
        }
        return new HttpResult<User>(200, "ok", user);
    }

    @RequestMapping(value = "/user/p", method = RequestMethod.POST)
    public HttpResult<User> postUser() {
        return new HttpResult<User>(200, "ok", new User(1, 22, request.getParameter("name")));
    }

    @RequestMapping(value = "/user", method = RequestMethod.OPTIONS)
    public HttpResult<User> opUser() {
        return new HttpResult<User>(200, "ok", null);
    }

    /**
     * 单文件上传
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public HttpResult<String> uploadUserPic(@RequestParam("file") MultipartFile file) {
        Enumeration<?> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            System.out.println(paraName + ": " + request.getParameter(paraName));
        }
        HttpResult<String> uploadFile = fileSrv.uploadFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
                .path(uploadFile.getData()).toUriString();
        uploadFile.setData(fileDownloadUri);
        return uploadFile;
    }

    /**
     * 多文件上传（key相同）
     */
    @RequestMapping(value = "/uploadBatch", method = RequestMethod.POST)
    public HttpResult<ArrayList<String>> uploadUserPic(@RequestParam("file") MultipartFile[] files) {
        Enumeration<?> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            System.out.println(paraName + ": " + request.getParameter(paraName));
        }
        ArrayList<String> fileList = new ArrayList<String>(files.length);
        for (MultipartFile file : files) {
            HttpResult<String> uploadFile = fileSrv.uploadFile(file);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
                    .path(uploadFile.getData()).toUriString();
            fileList.add(fileDownloadUri);
        }
        return new HttpResult<ArrayList<String>>(200, "ok", fileList);
    }

    /**
     * 多文件上传（key不相同）
     */
    @RequestMapping(value = "/uploadBatchKey", method = RequestMethod.POST)
    public HttpResult<ArrayList<String>> uploadBatchKey(@RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2, @RequestParam("file3") MultipartFile file3) {
        Enumeration<?> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            System.out.println(paraName + ": " + request.getParameter(paraName));
        }
        MultipartFile[] files = { file1, file2, file3 };
        ArrayList<String> fileList = new ArrayList<String>(files.length);
        for (MultipartFile file : files) {
            HttpResult<String> uploadFile = fileSrv.uploadFile(file);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
                    .path(uploadFile.getData()).toUriString();
            fileList.add(fileDownloadUri);
        }
        return new HttpResult<ArrayList<String>>(200, "ok", fileList);
    }

    /**
     * 多文件上传（不指定key，后端不知道前传的name是什么情况下）
     */
    @RequestMapping(value = "/uploadBatchAnyKey", method = RequestMethod.POST)
    public HttpResult<ArrayList<String>> uploadBatchAnyKey(HttpServletRequest request) {
        ArrayList<String> fileList = new ArrayList<String>(16);

        MultipartHttpServletRequest multipartRequest = ((MultipartHttpServletRequest) request);

        MultiValueMap<String, MultipartFile> multiFileMap = multipartRequest.getMultiFileMap();

        for (List<MultipartFile> files : multiFileMap.values()) {
            for (MultipartFile file : files) {
                HttpResult<String> uploadFile = fileSrv.uploadFile(file);
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
                        .path(uploadFile.getData()).toUriString();
                fileList.add(fileDownloadUri);
            }
        }

        return new HttpResult<ArrayList<String>>(200, "ok", fileList);
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileSrv.loadFileAsResource(fileName);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.err.println("Could not determine file type.");
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}