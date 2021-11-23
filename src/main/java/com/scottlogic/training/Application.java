package com.scottlogic.training;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.scottlogic.training.matcher.Matcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.RestController;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.SparkConf;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.File;

/*
  This starts up both the Spring Boot MVC server and the socketIO server
  (provided by netty-socketio: https://github.com/mrniko/netty-socketio)
 */
@SpringBootApplication
@RestController
public class Application {

    @Value("${rt.server.host}")
    private String host;

    @Value("${rt.server.port}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);
        return new SocketIOServer(config);
    }

    @Bean
    public SparkSession sparkSession() {
        String warehouseLocation = new File("spark-warehouse").getAbsolutePath();

        SparkConf conf = new SparkConf();
        conf.set("spark.master", "local").set("spark.sql.warehouse", warehouseLocation);

        SparkSession spark = SparkSession.builder()
                .appName("orderMatcher")
                .config(conf)
                .enableHiveSupport()
                .getOrCreate();

        return spark;
    }

    @Bean
    @DependsOn
    public Matcher matcher(SparkSession sparkSession) {
        return new Matcher(sparkSession);
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).run(args);
    }

}
