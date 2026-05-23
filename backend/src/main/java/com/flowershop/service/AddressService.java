package com.flowershop.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flowershop.entity.Address;
import com.flowershop.mapper.AddressMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService extends ServiceImpl<AddressMapper, Address> {

    public List<Address> getUserAddresses(Long userId) {
        return lambdaQuery().eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreatedAt)
                .list();
    }

    public boolean setDefault(Long id, Long userId) {
        // 先取消所有默认
        lambdaUpdate().eq(Address::getUserId, userId)
                .set(Address::getIsDefault, 0)
                .update();
        // 设置当前地址为默认
        return lambdaUpdate()
                .eq(Address::getId, id)
                .eq(Address::getUserId, userId)
                .set(Address::getIsDefault, 1)
                .update();
    }

    public Address getDefaultAddress(Long userId) {
        return lambdaQuery()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1)
                .one();
    }
}