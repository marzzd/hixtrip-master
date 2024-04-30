package com.hixtrip.sample.domain.inventory;

import com.hixtrip.sample.domain.inventory.model.Inventory;
import com.hixtrip.sample.domain.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 库存领域服务
 * 库存设计，忽略仓库、库存品、计量单位等业务
 */
@Component
public class InventoryDomainService {

    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * 获取sku当前库存 todo 根据skuId获取库存并不合适，可能存在多个库存存放相同skuId，以下先按照1个库存对应1个skuId的场景调整代码
     *
     * @param skuId
     */
    public Long getInventory(Long skuId) {
        //todo 需要你在infra实现，只需要实现缓存操作, 返回的领域对象自行定义
        return inventoryRepository.getSellableQuantity(skuId);
    }

    /**
     * 修改库存 todo 根据skuId获取库存并不合适，可能存在多个库存存放相同skuId，以下先按照1个库存对应1个skuId的场景调整代码
     *
     * @param skuId
     * @param sellableQuantity    可售库存
     * @param withholdingQuantity 预占库存
     * @param occupiedQuantity    占用库存
     * @return
     */
    public Boolean changeInventory(Long skuId, Long sellableQuantity, Long withholdingQuantity, Long occupiedQuantity) {
        //todo 需要你在infra实现，只需要实现缓存操作。
        return inventoryRepository.updateInventory(Inventory.builder()
                .skuId(skuId)
                .sellableQuantity(sellableQuantity)
                .withholdingQuantity(withholdingQuantity)
                .occupiedQuantity(occupiedQuantity)
                .build()) > 0;
    }

    /**
     * 返回缓存key
     * @return
     */
    public String cacheKey(){
        return InventoryRepository.INVENTORY_KEY;
    }
}
