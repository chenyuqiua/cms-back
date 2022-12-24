package com.chenyq.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenyq.domain.*;
import com.chenyq.mapper.RoleMapper;
import com.chenyq.mapper.SubMenuMapper;
import com.chenyq.service.MenuService;
import com.chenyq.service.RoleService;
import com.chenyq.utils.FormatDate;
import com.chenyq.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private MenuService menuService;
    @Autowired
    private SubMenuMapper subMenuMapper;

    /**
     * 按条件查询全部角色信息
     * @param terms
     * @return
     */
    @Override
    public Result<ResultList> selectPageByDomain(Terms terms) {
        // 定义查询条件
        LambdaQueryWrapper<Role> lqw = new LambdaQueryWrapper<>();
        if (terms.getName() != null) {
            lqw.like(Role::getName, terms.getName());
        }
        if (terms.getIntro() != null) {
            lqw.like(Role::getIntro, terms.getIntro());
        }
        if (terms.getCreateTime() != null && terms.getCreateTime().size() != 0) {
            String statTime = FormatDate.formatDate(terms.getCreateTime().get(0));
            String endTime = FormatDate.formatDate(terms.getCreateTime().get(1));
            log.error(statTime);
            log.error(endTime);
            lqw.between(Role::getCreateTime, statTime, endTime);
        }
        lqw.orderByDesc(Role::getCreateTime);

        if (terms.getPage() != null && terms.getSize() != null) {
            IPage page = new Page(terms.getPage(), terms.getSize());
            this.page(page, lqw);
            List<Role> accountList = page.getRecords();

            // 为每个角色注入权限列表
            for (Role role: accountList) {
                List<RoleMenu> roleMenus = menuService.getRoleMenus(role.getId());

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
                role.setMenus(menus);
            }

            // 封装返回结果
            ResultList resultList = new ResultList<Role>();
            resultList.setList(accountList);
            resultList.setCurrentPage(page.getCurrent());
            resultList.setSize(page.getSize());
            resultList.setTotalPages(page.getPages());
            resultList.setTotalCount(page.getTotal());

            return Result.success(resultList);
        } else {
            // 按条件查询全部
            List<Role> roles = this.list(lqw);

            // 为每个角色注入权限列表
            for (Role role: roles) {
                List<RoleMenu> roleMenus = menuService.getRoleMenus(role.getId());
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
                role.setMenus(menus);
            }

            ResultList resultList = new ResultList();
            resultList.setList(roles);

            return Result.success(resultList);
        }
    }
}
