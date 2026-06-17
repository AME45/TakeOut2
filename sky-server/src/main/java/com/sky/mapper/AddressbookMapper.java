package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressbookMapper {

    @Insert("insert into address_book" +
            "        (user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code," +
            "         district_name, detail, label, is_default)" +
            "        values (#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}," +
            "                #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void addAddressBook(AddressBook addressBook);

    @Select("select * from address_book where user_id = #{userId}")
    List<AddressBook> getAddressBookList(AddressBook addressBook);

    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);

    @Delete("delete from address_book where id = #{id}")
    void deleteById(Long id);

    void updateById(AddressBook addressBook);

    @Select("select * from address_book where user_id = #{userId} and is_default = 1")
    AddressBook getDefaultAddressBook(Long userId);

    @Update("update address_book set is_default = 0 where user_id = #{userId}")
    void setAllNotDefault(Long userId);

    @Update("update address_book set is_default = 1 where id = #{id}")
    void setDefault(Long id);
}
