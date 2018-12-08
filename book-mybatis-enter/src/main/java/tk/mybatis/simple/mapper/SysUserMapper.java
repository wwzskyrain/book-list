package tk.mybatis.simple.mapper;

import tk.mybatis.simple.model.SysUser;

public interface SysUserMapper {

    SysUser selectUserById(Long id);

    SysUser selectUserAndRoleById(Long id);

    SysUser selectUserAndRoleById2(Long id);

    SysUser selectUserAndRolesById(Long id);


    int insert(SysUser sysUser);
    int update(SysUser sysUser);

}
