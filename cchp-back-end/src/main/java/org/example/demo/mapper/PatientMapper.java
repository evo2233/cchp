package org.example.demo.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.demo.model.entity.Patient;

@Mapper
public interface PatientMapper {
    // 查找用户信息
    @Select("select * from Patient where identity=#{identity} and realname=#{realname} and password=#{password}")
    public Patient getPatient(@Param("identity") String identity,
                              @Param("realname") String realname,
                              @Param("password") String password);

    @Select("select * from Patient where identity=#{identity}")
    public Patient getPatientById(String identity);

    // 插入用户信息
    @Insert("insert into Patient(identity, realname, password) values(#{identity}, #{realname}, #{password})")
    public void insertPatient(Patient patient);
}

