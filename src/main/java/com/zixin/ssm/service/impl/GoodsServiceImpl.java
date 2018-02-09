package com.zixin.ssm.service.impl;

import com.zixin.ssm.cache.RedisCache;
import com.zixin.ssm.dao.GoodsDao;
import com.zixin.ssm.dao.OrderDao;
import com.zixin.ssm.dao.UserDao;
import com.zixin.ssm.entity.Goods;
import com.zixin.ssm.entity.User;
import com.zixin.ssm.enums.ResultEnum;
import com.zixin.ssm.exception.BizException;
import com.zixin.ssm.service.GoodsService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoodsServiceImpl implements GoodsService {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private GoodsDao goodsDao;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RedisCache cache;

	public List<Goods> getGoodsList(int offset, int limit) {
		String cache_key = RedisCache.CAHCENAME + "|getGoodsList|" + offset + "|" + limit;
		List<Goods> result_cache = cache.getListCache(cache_key, Goods.class);
		if (result_cache != null) {
			LOG.info("get cache with key:" + cache_key);
		} else {
			// 缓存中没有再去数据库取，并插入缓存（缓存时间�?60秒）
			result_cache = goodsDao.queryAll(offset, limit);
			cache.putListCacheWithExpireTime(cache_key, result_cache, RedisCache.CAHCETIME);
			LOG.info("put cache with key:" + cache_key);
			return result_cache;
		}
		return result_cache;
	}

	@Transactional
	public void buyGoods(long userPhone, long goodsId, boolean useProcedure) {
		// 用户校验
		User user = userDao.queryByPhone(userPhone);
		if (user == null) {
			throw new BizException(ResultEnum.INVALID_USER.getMsg());
		}
		if (useProcedure) {
			// 通过存储方式的方法进行操�?
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", user.getUserId());
			map.put("goodsId", goodsId);
			map.put("title", "抢购");
			map.put("result", null);
			goodsDao.bugWithProcedure(map);
			int result = MapUtils.getInteger(map, "result", -1);
			if (result <= 0) {
				// 买卖失败
				throw new BizException(ResultEnum.INNER_ERROR.getMsg());
			} else {
				// 买卖成功
				// 此时缓存中的数据不是�?新的，需要对缓存进行清理（具体的缓存策略还是要根据具体需求制定）
				cache.deleteCacheWithPattern("getGoodsList*");
				LOG.info("delete cache with key: getGoodsList*");
				return;
			}
		} else {

			int inserCount = orderDao.insertOrder(user.getUserId(), goodsId, "普�?�买�?");
			if (inserCount <= 0) {
				// 买卖失败
				throw new BizException(ResultEnum.DB_UPDATE_RESULT_ERROR.getMsg());
			} else {
				// 减库�?
				int updateCount = goodsDao.reduceNumber(goodsId);
				if (updateCount <= 0) {
					// 减库存失�?
					throw new BizException(ResultEnum.DB_UPDATE_RESULT_ERROR.getMsg());
				} else {
					// 买卖成功
					// 此时缓存中的数据不再是最新的，需要对缓存进行清理（具体的缓存策略还是要根据具体需求制定）
					cache.deleteCacheWithPattern("getGoodsList*");
					LOG.info("delete cache with key: getGoodsList*");
					return;
				}
			}
		}
	}

}
