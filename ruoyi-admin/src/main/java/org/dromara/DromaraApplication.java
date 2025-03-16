package org.dromara;

import cn.hutool.crypto.SecureUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.util.StopWatch;

/**
 * 启动程序
 *
 * @author ruoyi
 */
@SpringBootApplication
public class DromaraApplication {

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        // 开始计时
        stopWatch.start();

        SecureUtil.disableBouncyCastle();
        SpringApplication application = new SpringApplication(DromaraApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);

        // 停止计时
        stopWatch.stop();
        System.out.printf("(♥◠‿◠)ﾉﾞ  ruoyi-tdesign启动成功, 共耗时：%.3f秒   ლ(´ڡ`ლ)ﾞ\n", stopWatch.getTotalTimeSeconds());
    }

}
