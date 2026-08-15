package com.travelai.travelai.controller;

import com.travelai.travelai.common.response.Result;
import com.travelai.travelai.entity.City;
import com.travelai.travelai.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 城市基础数据 Controller
 *
 * @author TravelAI Team
 */
@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping("/{id}")
    public Result<City> getById(@PathVariable Long id) {
        return Result.success(cityService.getById(id));
    }

    @GetMapping
    public Result<List<City>> list() {
        return Result.success(cityService.list());
    }

    @PostMapping
    public Result<City> save(@RequestBody City city) {
        cityService.save(city);
        return Result.success(city);
    }

    @PutMapping
    public Result<City> update(@RequestBody City city) {
        cityService.updateById(city);
        return Result.success(city);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        cityService.removeById(id);
        return Result.success();
    }
}
