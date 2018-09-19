package com.vpclub.bait.busticket.command;


import com.vpclub.bait.busticket.api.util.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {
    @Autowired
    private RedisService redisService;
    private static String BUS_SITE_KEY = "bus.site.rediskey";
    @Test
    public void testConnectReis(){
        try {
            Set<String> strings = redisService.redisKeys(BUS_SITE_KEY);
            System.out.println(strings);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
