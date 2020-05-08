package com.lock.redislock;

import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@SpringBootApplication
public class RedisLockApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisLockApplication.class, args);
	}

	@Bean
	public Redisson getRedisson(){
		Config config = new Config();
		//单机模式  依次设置redis地址和密码
		config.useSingleServer().
				setAddress("192.168.2.9:6379").setDatabase(1);
		return (Redisson)Redisson.create(config);
	}

	@Autowired
	private Environment env;

	/*@Bean(destroyMethod = "shutdown")
	public RedissonClient redissonClient() throws IOException {
		String[] profiles = env.getActiveProfiles();
		String profile = "";
		if(profiles.length > 0) {
			profile = "-" + profiles[0];
		}
		return Redisson.create(Config.fromYAML(new ClassPathResource("redisson" + profile + ".yml").getInputStream()));
	}*/

}
