package com.genius.gitget.challenge.certification.config;

import java.io.IOException;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableCaching
public class CacheConfig {

	@Bean(name = "ehCacheManage")
	public CacheManager cacheManager() throws IOException {
		CachingProvider cachingProvider = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
		javax.cache.CacheManager manager = cachingProvider.getCacheManager(
			new ClassPathResource("/ehcache.xml").getURI(),
			getClass().getClassLoader());

		return new JCacheCacheManager(manager);
	}
}
