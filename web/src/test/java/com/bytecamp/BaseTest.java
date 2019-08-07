package com.bytecamp;

import com.bytecamp.web.WebApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author wangke
 * @description: 其他测试继承这个基类
 * @date 2019-07-27 15:57
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebApplication.class)
public class BaseTest {
}
