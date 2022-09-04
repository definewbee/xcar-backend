package io.doeasy.xcar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author kris.wang
 * xracer 合约相关配置
 */
@Configuration
public class XracerConfig {

    @Value("abey.contracts")
    private List<String> contractAddressList;
}
