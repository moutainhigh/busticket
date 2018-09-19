package com.vpclub.bait.busticket.command;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(value = "com.vpclub.bait.busticket")
public class BusticketCommandApplication {

	public static void main(String[] args) {
//		String[] pwd = {"vpman1208!"};
		SpringApplication.run(BusticketCommandApplication.class, args);

	}
}
