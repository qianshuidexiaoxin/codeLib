package com.zixin.ssm.dao;

import com.zixin.ssm.entity.Goods;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GoodsDao {
	 /**
     * 根据偏移量查询可用商品列�?
     *
     * @param offset
     * @param limit
     * @return
     */
    List<Goods> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 商品减库�?
     *
     * @param goodsId
     * @return�?如果更新行数大于1,表示更新的行�?
     */
    int reduceNumber(long goodsId);

    /**
     * 使用存储过程执行抢购
     * 
     * 能提升并发�?�的原因�?
     * 1、减少多个sql语句执行来回的网络延时�??
     * 2、�?�过mysql自身的事物提升效率�??
     * 
     * @param paramMap
     */
    void bugWithProcedure(Map<String, Object> paramMap);
    
    
}
