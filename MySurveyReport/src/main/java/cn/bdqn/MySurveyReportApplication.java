package cn.bdqn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

//@ServletComponentScan
@SpringBootApplication
@MapperScan("cn.bdqn.dao")
public class MySurveyReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(MySurveyReportApplication.class, args);
    }

}
