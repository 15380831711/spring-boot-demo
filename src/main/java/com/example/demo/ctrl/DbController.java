package com.example.demo.ctrl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.bean.App;
import com.example.demo.bean.HttpResult;

@RestController
public class DbController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "/app", method = RequestMethod.GET)
    public HttpResult<List<App>> getApps() {
        String sql = "SELECT * FROM xview_app";
        List<App> apps = jdbcTemplate.query(sql, new StudentRowMapper());
        return new HttpResult<List<App>>(200, "ok", apps);
    }

    class StudentRowMapper implements RowMapper<App> {
        @Override
        public App mapRow(ResultSet resultSet, int i) throws SQLException {
            App stu = new App();
            stu.setId(resultSet.getInt("id"));
            stu.setAppid(resultSet.getInt("appid"));
            stu.setApp_name(resultSet.getString("app_name"));
            return stu;
        }
    }

}
