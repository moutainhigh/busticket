package com.vpclub.bait.busticket.query;

import com.alibaba.druid.filter.config.ConfigTools;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.vpclub.bait.busticket")
@MapperScan("com.vpclub.bait.busticket.query.mapper")
public class BusticketQueryApplication {

	public static void main(String[] args) throws Exception{
//		String[] pwd = {"@vpclubdev"};
//		ConfigTools.main(pwd);
		SpringApplication.run(BusticketQueryApplication.class, args);
	}
}
