package com.chenyq.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenyq.domain.*;
import com.chenyq.mapper.RoleMenuMapper;
import com.chenyq.service.RoleService;
import com.chenyq.service.TokenService;
import com.chenyq.utils.CurrentAcc;
import com.chenyq.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RoleMenuMapper roleMenuMapper;

    private SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd :HH:mm:ss");

    /**
     * 获取某个角色(权限)信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Role> selectById(@PathVariable BigInteger id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.error("您查询的用户权限信息不存在!");
        }
        return Result.success(role);
    }

    /**
     * 获取角色(权限)信息列表
     * @return
     */
    @PostMapping("/list")
    public Result<ResultList> selectAll(@RequestBody Terms terms) {
        return roleService.selectPageByDomain(terms);
    }

    /**
     * 创建角色
     * @param request
     * @param role
     * @return
     */
    @PostMapping()
    @Transactional(rollbackFor = {NullPointerException.class})
    public Result<String> save(HttpServletRequest request, @RequestBody Role role) {
        log.error(String.valueOf(role));
        if (role.getName() == null || role.getIntro() == null || role.getMenuList() == null) {
            return Result.error("请输入完整的信息!");
        }

        // 设置创建/修改时间, 创建/修改人
        role.setCreateTime(formatdate.format(new Date()));
        role.setUpdateTime(formatdate.format(new Date()));
        Long id = CurrentAcc.getCurrentId(request, tokenService);
        role.setCreateUser(id);
        role.setUpdateUser(id);
        boolean save = roleService.save(role);

        // 获取新增角色的id
        LambdaQueryWrapper<Role> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Role::getName, role.getName());
        Role newRole = roleService.getOne(lqw);
        Long roleId = newRole.getId();
        Long currentId = CurrentAcc.getCurrentId(request, tokenService);

        // 批量添加数据到account_role_menu表中分配权限
        for (Long menuId: role.getMenuList()) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenu.setCreateTime(formatdate.format(new Date()));
            roleMenu.setUpdateTime(formatdate.format(new Date()));
            roleMenu.setCreateUser(currentId);
            roleMenu.setUpdateUser(currentId);
            roleMenuMapper.insert(roleMenu);
        }

        if (save) {
            return Result.success("创建成功!");
        }
        return Result.error("创建失败!");
    }

    /**
     * 删除角色功能
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = {NullPointerException.class})
    public Result<String> delete(@PathVariable Long id) {
        if (id < 10) Result.error("id小于10的角色不允许删除");

        // 通过角色id到account_role_menu表中查询数据
        LambdaQueryWrapper<RoleMenu> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RoleMenu::getRoleId, id);
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(lqw);
        // 将查询到的数据遍历 将存入一个集合中批量删除
        List<Long> list = new ArrayList<>();
        for (RoleMenu roleMenu: roleMenus) {
            list.add(roleMenu.getId());
        }
        roleMenuMapper.deleteBatchIds(list);

        // 删除角色信息
        boolean res = roleService.removeById(id);

        log.error(String.valueOf(res));
        if (res) {
            return Result.success("删除成功!");
        }
        return Result.error("删除失败!");
    }

    /**
     * 修改角色和权限信息
     * @param request
     * @param role
     * @return
     */
    @PutMapping()
    @Transactional(rollbackFor = {NullPointerException.class})
    public Result<String> update(HttpServletRequest request, @RequestBody Role role) {
        log.error(String.valueOf(role));

        // 将原有的权限信息全部删除
        LambdaQueryWrapper<RoleMenu> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RoleMenu::getRoleId, role.getId());
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(lqw);
        // 将查询到的数据遍历 将存入一个集合中批量删除
        List<Long> list = new ArrayList<>();
        for (RoleMenu roleMenu: roleMenus) {
            list.add(roleMenu.getId());
        }
        roleMenuMapper.deleteBatchIds(list);

        // 添加新的权限信息
        Long currentId = CurrentAcc.getCurrentId(request, tokenService);
        for (Long menuId: role.getMenuList()) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(role.getId());
            roleMenu.setMenuId(menuId);
            roleMenu.setCreateTime(formatdate.format(new Date()));
            roleMenu.setUpdateTime(formatdate.format(new Date()));
            roleMenu.setCreateUser(currentId);
            roleMenu.setUpdateUser(currentId);
            roleMenuMapper.insert(roleMenu);
        }

        // 设置修改时间修改人
        role.setUpdateTime(formatdate.format(new Date()));
        Long id = CurrentAcc.getCurrentId(request, tokenService);
        role.setUpdateUser(id);

        boolean res = roleService.updateById(role);
        if (res) {
            return Result.success("修改成功!");
        }
        return Result.error("修改失败!");
    }
}
