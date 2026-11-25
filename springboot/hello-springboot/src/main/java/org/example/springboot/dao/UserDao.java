package org.example.springboot.dao;

import org.example.springboot.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * 数据访问层：只写 SQL，不写业务。
 * 用 JdbcTemplate 而不是 MyBatis —— 因为面试问的是 SQL 和索引，手写 SQL 才看得见执行计划。
 */
@Repository
public class UserDao {

    private final JdbcTemplate jdbc;

    public UserDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** 结果集一行 → 一个 User 对象。created_at 用 getObject(LocalDateTime.class) 拿，免得手工转。 */
    private static final RowMapper<User> ROW_MAPPER = new RowMapper<>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setUsername(rs.getString("username"));
            u.setRole(rs.getInt("role"));
            u.setPhone(rs.getString("phone"));
            u.setCreatedAt(rs.getObject("created_at", java.time.LocalDateTime.class));
            return u;
        }
    };

    /** 列表用：故意不查 password_hash，少一个泄露点。 */
    public List<User> findAllForList() {
        String sql = "SELECT id, username, role, phone, created_at FROM user ORDER BY id";
        return jdbc.query(sql, ROW_MAPPER);
    }

    /** 注册用：username 上有唯一索引，并发下靠数据库兜底，不靠先查再插。 */
    public void insert(String username, String passwordHash, String phone) {
        String sql = "INSERT INTO user (username, password_hash, role, phone) VALUES (?, ?, 1, ?)";
        jdbc.update(sql, username, passwordHash, phone);
    }

    /** 登录用：这里必须带 password_hash，因为要校验密码。 */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, role, phone, created_at FROM user WHERE username = ?";
        List<User> list = jdbc.query(sql, (rs, rowNum) -> {
            User u = ROW_MAPPER.mapRow(rs, rowNum);
            u.setPasswordHash(rs.getString("password_hash"));
            return u;
        }, username);
        return list.stream().findFirst();
    }
}
