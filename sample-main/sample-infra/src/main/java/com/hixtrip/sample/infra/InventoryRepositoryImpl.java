package com.hixtrip.sample.infra;

import com.hixtrip.sample.domain.inventory.model.Inventory;
import com.hixtrip.sample.domain.inventory.repository.InventoryRepository;
import com.hixtrip.sample.infra.utils.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 库存仓储实现
 */
@Slf4j
@Component
public class InventoryRepositoryImpl implements InventoryRepository {


//    @Autowired
//    private InventoryMapper inventoryMapper;

    private final Lock lock = new ReentrantLock();


    /**
     * 获取可售库存 默认库存1000
     *
     * @param skuId
     * @return
     */
    @Override
    public Long getSellableQuantity(Long skuId) {
        String cacheKey = INVENTORY_KEY + skuId;
        // 先查缓存，缓存没有，则查数据库后再更新到缓存
        Long sellableQuantity = CacheUtil.getCacheMapLong(INVENTORY_KEY + skuId, SELLABLE_KEY);
        if (sellableQuantity != null) {
            return sellableQuantity;
        }
        // 缓存管理库存
        CacheUtil.setCacheMap(cacheKey, Map.of(TOTAL_KEY, DEFAULT_NUM,
                        SELLABLE_KEY, DEFAULT_NUM,
                        WITHHOLDING_KEY, 0,
                        OCCUPIED_KEY, 0),
                DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        return DEFAULT_NUM;
    }

    /**
     * 修改库存 单体架构下加lock锁控制并发，避免缓存击穿
     *
     * @param inventory
     * @return
     */
    @Override
    @Transactional
    public int updateInventory(Inventory inventory) {
        String key = INVENTORY_KEY + inventory.getSkuId();
        Long sellable = CacheUtil.getCacheMapLong(key, SELLABLE_KEY);
        if (sellable == null) {
            log.error("暂无库存");
            throw new RuntimeException("暂无库存");
        }
        if (sellable < 0) {
            log.error("可售库存不足");
            throw new RuntimeException("可售库存不足");
        }
        if (CacheUtil.incrementMapValue(key, SELLABLE_KEY, inventory.getSellableQuantity()) < 0) {
            CacheUtil.incrementMapValue(key, SELLABLE_KEY, -inventory.getSellableQuantity());
            log.error("可售库存不足");
            throw new RuntimeException("可售库存不足");
        }
        CacheUtil.incrementMapValue(key, WITHHOLDING_KEY, inventory.getWithholdingQuantity());
        CacheUtil.incrementMapValue(key, OCCUPIED_KEY, inventory.getOccupiedQuantity());
        return 1;
    }

}
