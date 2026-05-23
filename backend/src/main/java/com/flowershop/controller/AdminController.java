package com.flowershop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowershop.common.Result;
import com.flowershop.entity.*;
import com.flowershop.service.*;
import com.flowershop.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private FlowerLanguageService flowerLanguageService;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> params) {
        Admin admin = adminService.login(params.get("username"), params.get("password"));
        if (admin == null) return Result.error("用户名或密码错误");
        String token = JwtUtils.generateAdminToken(admin.getId(), admin.getUsername());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("admin", admin);
        return Result.success(data);
    }

    @GetMapping("/dashboard")
    public Result dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userService.getCount());
        data.put("todayUserCount", userService.getTodayCount());
        data.put("productCount", productService.getCount());
        data.put("onSaleCount", productService.getOnSaleCount());
        Map<String, Object> orderStats = orderService.getOrderStats();
        data.put("orderCount", orderStats.get("total"));
        data.put("totalSales", orderStats.get("totalRevenue"));
        data.put("flowerLanguageCount", flowerLanguageService.getCount());
        return Result.success(data);
    }

    @GetMapping("/users")
    public Result users(@RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(required = false) String keyword) {
        Page<User> userPage = userService.lambdaQuery()
                .like(keyword != null, User::getUsername, keyword)
                .or().like(keyword != null, User::getNickname, keyword)
                .or().like(keyword != null, User::getPhone, keyword)
                .orderByDesc(User::getCreatedAt)
                .page(new Page<>(page, size));
        return Result.success(userPage);
    }

    @PutMapping("/users/{id}/status")
    public Result updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        User user = userService.getById(id);
        if (user == null) return Result.error("用户不存在");
        user.setStatus(params.get("status"));
        userService.updateById(user);
        return Result.success("操作成功");
    }

    @GetMapping("/products")
    public Result products(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(required = false) Long categoryId,
                           @RequestParam(required = false) String keyword) {
        Page<Product> productPage = productService.lambdaQuery()
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .like(keyword != null, Product::getName, keyword)
                .or().like(keyword != null, Product::getSubtitle, keyword)
                .orderByDesc(Product::getCreatedAt)
                .page(new Page<>(page, size));
        for (Product p : productPage.getRecords()) {
            Category c = categoryService.getById(p.getCategoryId());
            if (c != null) p.setCategoryName(c.getName());
        }
        return Result.success(productPage);
    }

    @PostMapping("/products")
    public Result addProduct(@RequestBody Product product) {
        if (product.getStatus() == null) product.setStatus(1);
        productService.save(product);
        return Result.success("添加成功");
    }

    @PutMapping("/products")
    public Result updateProduct(@RequestBody Product product) {
        productService.updateById(product);
        return Result.success("更新成功");
    }

    @PutMapping("/products/{id}/status")
    public Result toggleProductStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Product p = productService.getById(id);
        if (p == null) return Result.error("商品不存在");
        p.setStatus(params.get("status"));
        productService.updateById(p);
        return Result.success("操作成功");
    }

    @DeleteMapping("/products/{id}")
    public Result deleteProduct(@PathVariable Long id) {
        productService.removeById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/categories")
    public Result categories() {
        return Result.success(categoryService.list());
    }

    @PostMapping("/categories")
    public Result addCategory(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success("添加成功");
    }

    @PutMapping("/categories")
    public Result updateCategory(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success("更新成功");
    }

    @DeleteMapping("/categories/{id}")
    public Result deleteCategory(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/orders")
    public Result orders(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(defaultValue = "10") int size,
                         @RequestParam(required = false) Integer status,
                         @RequestParam(required = false) String orderNo) {
        return Result.success(orderService.getAllOrders(page, size, status, orderNo));
    }

    @PutMapping("/orders/{id}/status")
    public Result updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        orderService.updateStatus(id, params.get("status"));
        return Result.success("操作成功");
    }

    @GetMapping("/flower-languages")
    public Result flowerLanguages(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String keyword) {
        Page<FlowerLanguage> flPage = flowerLanguageService.lambdaQuery()
                .like(keyword != null, FlowerLanguage::getName, keyword)
                .or().like(keyword != null, FlowerLanguage::getFlowerLanguage, keyword)
                .page(new Page<>(page, size));
        return Result.success(flPage);
    }

    @PostMapping("/flower-languages")
    public Result addFlowerLanguage(@RequestBody FlowerLanguage fl) {
        flowerLanguageService.save(fl);
        return Result.success("添加成功");
    }

    @PutMapping("/flower-languages")
    public Result updateFlowerLanguage(@RequestBody FlowerLanguage fl) {
        flowerLanguageService.updateById(fl);
        return Result.success("更新成功");
    }

    @DeleteMapping("/flower-languages/{id}")
    public Result deleteFlowerLanguage(@PathVariable Long id) {
        flowerLanguageService.removeById(id);
        return Result.success("删除成功");
    }
}
