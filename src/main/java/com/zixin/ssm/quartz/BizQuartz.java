package com.zixin.ssm.quartz;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zixin.ssm.cache.RedisCache;
import com.zixin.ssm.dao.UserDao;

/**
 * 业务相关的作业调�?
 * 
           字段               允许�?                           允许的特殊字�?
	�?	 	0-59	 	, - * /
	�?	 	0-59	 	, - * /
	小时	 	0-23	 	, - * /
	日期	 	1-31	 	, - * ? / L W C
	月份	 	1-12 或�?? JAN-DEC	 	, - * /
	星期	 	1-7 或�?? SUN-SAT	 	, - * ? / L C #
	年（可�?�）	 	留空, 1970-2099	 	, - * /
	
	*  字符代表�?有可能的�?
	/  字符用来指定数�?�的增量
	L  字符仅被用于天（月）和天（星期）两个子表达式，表示一个月的最后一天或者一个星期的�?后一�?
	6L 可以表示倒数第６�?
	
 * @author yingjun
 *
 */
@Component
public class BizQuartz {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserDao userDao;
	@Autowired
	private RedisCache cache;
	
	/**
	 * 用户自动加积�?
	 * 每天9点到17点每�?1分钟�?有用户加�?次积�?
	 */
	@Scheduled(cron = "0 0/1 9-17 * * ? ")
	public void addUserScore() {
		LOG.info("@Scheduled--------addUserScore()");
		userDao.addScore(10);
	}
	/**
	 * 每隔5分钟定时清理缓存
	 */
	@Scheduled(cron = "0 0/5 * * * ? ")
	public void cacheClear() {
		LOG.info("@Scheduled-------cacheClear()");
		cache.clearCache();
	}
	
}
