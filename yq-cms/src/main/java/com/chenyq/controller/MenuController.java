package com.chenyq.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenyq.domain.*;
import com.chenyq.mapper.RoleMenuMapper;
import com.chenyq.mapper.SubMenuMapper;
import com.chenyq.service.MenuService;
import com.chenyq.service.TokenService;
import com.chenyq.utils.CurrentAcc;
import com.chenyq.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/menus")
public class MenuController {
    @Autowired
    private MenuService menuService;
    @Autowired
    private SubMenuMapper subMenuMapper;

    /**
     * 获取菜单列表: 根据用户Role的id获取用户菜单列表
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<ArrayList<Menu>> getMenusById(@PathVariable Long id) {
        // 根据id 到account_role_menu表中查询有哪些权限
        List<RoleMenu> roleMenus = menuService.getRoleMenus(id);

        // 遍历id获取菜单
        ArrayList<Menu> menus = new ArrayList<>();
        for (RoleMenu roleMenu : roleMenus) {
            // 通过menu_id获取到一级菜单
            Menu menu = menuService.getById(roleMenu.getMenuId());
            menus.add(menu);
            // 通过菜单id获取到二级菜单
            Long parentId = menu.getId();
            LambdaQueryWrapper<SubMenu> slqw = new LambdaQueryWrapper<>();
            slqw.eq(SubMenu::getParentId, parentId);
            List<SubMenu> subMenus = subMenuMapper.selectList(slqw);
            menu.setChildren(subMenus);
        }
        return Result.success(menus);
    }


    @PostMapping("/list")
    public Result<ResultList> getAllMenus(@RequestBody Terms terms) {
        Result<ArrayList<Menu>> menus = getMenusById(1L);
        ResultList resultList = new ResultList();
        resultList.setList(menus.getData());
        return Result.success(resultList);
    }
}
