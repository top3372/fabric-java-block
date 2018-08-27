package blockchain.dao.config;

import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "blockchain.dao.mapper")
@Configuration
public class MapperConfig {

}
