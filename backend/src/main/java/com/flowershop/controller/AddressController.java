package com.flowershop.controller;

import com.flowershop.common.Result;
import com.flowershop.entity.Address;
import com.flowershop.service.AddressService;
import com.flowershop.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/list")
    public Result list(@RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        return Result.success(addressService.getUserAddresses(userId));
    }

    @PostMapping("/add")
    public Result add(@RequestHeader(value = "Authorization", required = false) String token,
                    @RequestBody Address address) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        address.setUserId(userId);
        addressService.save(address);
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressService.setDefault(address.getId(), userId);
        }
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result update(@RequestHeader(value = "Authorization", required = false) String token,
                       @RequestBody Address address) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        address.setUserId(userId);
        addressService.updateById(address);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@RequestHeader(value = "Authorization", required = false) String token,
                       @PathVariable Long id) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        addressService.removeById(id);
        return Result.success("删除成功");
    }

    @PutMapping("/default/{id}")
    public Result setDefault(@RequestHeader(value = "Authorization", required = false) String token,
                           @PathVariable Long id) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        addressService.setDefault(id, userId);
        return Result.success("设置成功");
    }

    @GetMapping("/default")
    public Result getDefault(@RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        if (userId == null) {
            return Result.unauthorized();
        }
        return Result.success(addressService.getDefaultAddress(userId));
    }

    private Long extractUserId(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        try {
            String actualToken = token;
            if (token.startsWith("Bearer ")) {
                actualToken = token.substring(7);
            }
            actualToken = actualToken.trim();
            if (actualToken.isEmpty()) {
                return null;
            }
            return JwtUtils.getUserId(actualToken);
        } catch (Exception e) {
            return null;
        }
    }
}
