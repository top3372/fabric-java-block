package com.ideal.blockchain.dao.config;

import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com.ideal.blockchain.dao.mapper")
@Configuration
public class MapperConfig {

}
