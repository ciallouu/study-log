package org.example.springboot.controller;

import org.example.springboot.common.ApiException;
import org.example.springboot.dao.DocumentDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;   // ← 新
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;   // ← 新
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController                          // ← 缺：登记成 Bean，并且返回值自动转 JSON
@RequestMapping("/api/documents")        // ← 缺：路径前缀，让请求落进 /api/** 被拦截器管
public class DocumentController {
    @Value("${app.upload-dir}")
    private String uploadDir;
    private final DocumentDao documentDao;

    public DocumentController(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }
    @PostMapping("/upload")
    public Map<String, Object> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("categoryId") int categoryId,
            @RequestAttribute("ctx.userId") int uploaderId) throws IOException {
        //加校验
        if(file.isEmpty()){
            throw new ApiException(400,"文件为空");
        }
        String safeTitle = title ==null ? "":title.trim();
        if(safeTitle.isEmpty()){
            throw new ApiException(400,"标题不能为空");
        }
        if(safeTitle.length() > 128){
            throw new ApiException(400,"标题不能超过128字符");
        }
        //扩展名白名单（注意 originalFilename 可能是 null
        String original= file.getOriginalFilename();
        String ext="";
        if(original != null){
            int dot = original.lastIndexOf('.');
            if(dot >= 0){
                ext = original.substring(dot + 1).toLowerCase();
            }
        }
        // 原始文件名：只用于展示/下载时还原，不能拿它拼路径（客户端说了算，可能含 ../）
        String safeOriginal = original == null ? "" : original.trim();
        if (safeOriginal.length() > 255) {          // 对齐 original_name varchar(255)
            safeOriginal = safeOriginal.substring(0, 255);
        }
        List<String> allowed=List.of("pdf","doc","docx","ppt","pptx","txt","md","zip");
        if(!allowed.contains(ext)){
            throw new ApiException(400,"不接受的文件类型");
        }
        long maxBytes = 10L *1024 * 1024;
        if(file.getSize()>maxBytes){
            throw new ApiException(400,"文件不能超过 10MB");
        }

        //落盘
        String uuid = UUID.randomUUID().toString().replace("-", "");      // 32 位
        String subDir = java.time.LocalDate.now().toString().replace("-", "/");  // 2026/11/28
        Path dir = Paths.get(uploadDir, subDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        Path target = dir.resolve(uuid + "." + ext);
        file.transferTo(target.toFile());
        String relativePath = subDir + "/" + uuid + "." + ext;//存库用相对路径
        System.out.println("已保存: " + relativePath + " / " + file.getSize() + " 字节");
        // ── 第 6 关：写库。写库失败就把刚落盘的文件删掉，避免孤儿文件
        int documentId;
        try {
            documentId = documentDao.insert(
                    safeTitle, safeOriginal, categoryId, uploaderId, relativePath, file.getSize(), ext);
        } catch (RuntimeException e) {
            Files.deleteIfExists(target);   // 补偿
            throw e;                        // 照旧往上抛，交给 GlobalExceptionHandler
        }

        System.out.println("已入库: id=" + documentId + " / " + relativePath);

        // 注意：Map.of 不允许 null 值，而 getOriginalFilename/getContentType 可能返回 null
        return Map.of(
                "id",           documentId,
                "savedAs",    relativePath,        // ← 真正存到磁盘的名字
                "originalName", safeOriginal,      // ← 用户上传时的原名（展示用）
                "size",       file.getSize(),
                "title",      safeTitle,           // ← 用校验后的
                "categoryId", categoryId,
                "uploaderId", uploaderId);

    }
}