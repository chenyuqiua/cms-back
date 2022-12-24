package com.chenyq.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenyq.domain.Menu;
import com.chenyq.domain.RoleMenu;
import com.chenyq.mapper.MenuMapper;
import com.chenyq.mapper.RoleMenuMapper;
import com.chenyq.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private RoleMenuMapper roleMenuMapper;

    /**
     * Role的id根据id 到account_role_menu表中查询有哪些权限
     * @param id
     * @return
     */
    @Override
    public List<RoleMenu> getRoleMenus(Long id) {
        // 根据id 到account_role_menu表中查询有哪些权限
        LambdaQueryWrapper<RoleMenu> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RoleMenu::getRoleId, id);
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(lqw);
        return roleMenus;
    }
}
