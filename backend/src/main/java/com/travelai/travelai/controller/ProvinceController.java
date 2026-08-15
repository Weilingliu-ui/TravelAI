package com.travelai.travelai.controller;

import com.travelai.travelai.common.response.Result;
import com.travelai.travelai.entity.Province;
import com.travelai.travelai.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 省份基础数据 Controller
 *
 * @author TravelAI Team
 */
@RestController
@RequestMapping("/api/provinces")
@RequiredArgsConstructor
public class ProvinceController {

    private final ProvinceService provinceService;

    @GetMapping("/{id}")
    public Result<Province> getById(@PathVariable Long id) {
        return Result.success(provinceService.getById(id));
    }

    @GetMapping
    public Result<List<Province>> list() {
        return Result.success(provinceService.list());
    }

    @PostMapping
    public Result<Province> save(@RequestBody Province province) {
        provinceService.save(province);
        return Result.success(province);
    }

    @PutMapping
    public Result<Province> update(@RequestBody Province province) {
        provinceService.updateById(province);
        return Result.success(province);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        provinceService.removeById(id);
        return Result.success();
    }
}
