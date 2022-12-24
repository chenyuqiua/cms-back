package com.chenyq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chenyq.domain.Menu;
import com.chenyq.domain.RoleMenu;

import java.util.List;

public interface MenuService extends IService<Menu> {
    /**
     * Role的id根据id 到account_role_menu表中查询有哪些权限
     * @param id
     * @return
     */
    List<RoleMenu> getRoleMenus(Long id);
}
