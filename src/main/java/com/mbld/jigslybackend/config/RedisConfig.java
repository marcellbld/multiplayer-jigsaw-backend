package com.mbld.jigslybackend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableRedisRepositories("com.mbld.jigslybackend.repositories.redis")
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private Integer port;
    @Value("${spring.data.redis.database}")
    private Integer database;
    @Value("${spring.data.redis.password}")
    private String password;
    @Value("${spring.data.redis.username}")
    private String username;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setDatabase(database);
        config.setUsername(username);
        config.setPassword(password);

        JedisClientConfiguration jedisConf = JedisClientConfiguration.builder().readTimeout(Duration.ofMillis(60000)).connectTimeout(Duration.ofMillis(60000)).useSsl().and().usePooling().build();
        return new JedisConnectionFactory(config, jedisConf);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CommandLineRunner clearRedisDataCmd(RedisTemplate<String, String> redisTemplate) {
        return args -> {
            clearRedisData(redisTemplate);
        };
    }

    private void clearRedisData(RedisTemplate<String, String> redisTemplate) {
        // Logic to clear data in Redis
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

}
