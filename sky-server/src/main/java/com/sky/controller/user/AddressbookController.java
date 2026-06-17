package com.sky.controller.user;


import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressbookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Slf4j
@Api(tags = "C端地址簿接口")
public class AddressbookController {

    @Autowired
    private AddressbookService addressbookService;

    @PostMapping
    @ApiOperation("添加地址簿")
    public Result addAddressBook(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressbookService.addAddressBook(addressBook);
        return Result.success();
    }


    @GetMapping("/list")
    @ApiOperation("查询所有地址")
    public Result<List<AddressBook>> getAddressBookList() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = AddressBook.builder().userId(userId).build();
        List<AddressBook> list = addressbookService.getAddressBookList(addressBook);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询")
    public Result<AddressBook> getById(@RequestParam Long id) {
        AddressBook addressBook = addressbookService.getById(id);
        return Result.success(addressBook);
    }

    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result<String> deleteById(Long id) {
        addressbookService.deleteById(id);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result<String> updateById(@RequestBody AddressBook addressBook) {
        addressbookService.updateById(addressBook);
        return Result.success();
    }

    @PutMapping("/default")
    @ApiOperation("根据id修改默认地址")
    public Result<String> updateDefaultAddressBookById(@RequestBody AddressBook addressBook) {
        addressbookService.updateDefaultAddressBookById(addressBook);
        return Result.success();
    }

    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> getDefaultAddressBook() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressbookService.getDefaultAddressBook(userId);
        return Result.success(addressBook);
    }
}
