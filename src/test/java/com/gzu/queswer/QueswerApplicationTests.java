package com.gzu.queswer;

import com.gzu.queswer.service.UserService;
import com.gzu.queswer.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QueswerApplicationTests {
    @Test
    public void testHello() {

        UserService userService= SpringUtil.getBean(UserService.class);
        log.info("hello");
    }
}
