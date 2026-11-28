package org.example.springboot.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class DocumentDao {

    private final JdbcTemplate jdbcTemplate;

    public DocumentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** 插入一条文档记录，返回数据库生成的自增 id */
    public int insert(String title, String originalName, int categoryId, int uploaderId,
                      String filePath, long fileSize, String fileType) {
        String sql = "INSERT INTO document"
                + "(title, original_name, category_id, uploader_id, file_path, file_size, file_type, audit_status, download_count) "
                + "VALUES(?,?,?,?,?,?,?,0,0)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setString(2, originalName);
            ps.setInt(3, categoryId);
            ps.setInt(4, uploaderId);
            ps.setString(5, filePath);
            ps.setLong(6, fileSize);
            ps.setString(7, fileType);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();          // getKey() = getKeyAs(Number.class)
        if (key == null) {
            throw new IllegalStateException("插入后没拿到自增 id");
        }
        return key.intValue();
    }
}