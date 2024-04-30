package com.hixtrip.sample.domain.inventory.repository;

import com.hixtrip.sample.domain.inventory.model.Inventory;

/**
 *
 */
public interface InventoryRepository {

    String INVENTORY_KEY = "HIXTRIP:INVENTORY:SKU_ID:";
    // 总库存
    String TOTAL_KEY = "total";
    // 可售库存
    String SELLABLE_KEY = "sellable";
    // 预占库存
    String WITHHOLDING_KEY = "withholding";
    // 占用库存
    String OCCUPIED_KEY = "occupied";
    // 默认超时时间 2个小时
    Long DEFAULT_TIMEOUT = 7200L;
    // 默认库存数量
    Long DEFAULT_NUM = 1000L;

    Long getSellableQuantity(Long skuId);

    int updateInventory(Inventory inventory);
}
