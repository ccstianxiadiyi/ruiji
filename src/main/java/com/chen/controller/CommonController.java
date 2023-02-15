package com.chen.controller;

import com.chen.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${path.basePath}")
    private String bathUrl;

    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String s = originalFileName.split("\\.")[1];
        String filename = UUID.randomUUID().toString() + "." + s;
        file.transferTo(new File(bathUrl + filename));
        return R.success(filename);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse res) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(bathUrl + name));
        ServletOutputStream outputStream = res.getOutputStream();
        res.setContentType("images/jpeg");
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = fileInputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, len);
            outputStream.flush();
        }


    }
}
