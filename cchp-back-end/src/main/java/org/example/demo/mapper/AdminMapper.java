package org.example.demo.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.demo.model.entity.Admin;

@Mapper
public interface AdminMapper {
    // 查找管理员信息
    @Select("select * from Admin where username=#{username} and password=#{password}")
    public Admin getAdmin(@Param("username") String username, @Param("password") String password);

    // 插入管理员
    @Insert("insert into Admin(username, password, address) values (#{username}, #{password}, #{address})")
    public void addAdmin(Admin admin);
}
