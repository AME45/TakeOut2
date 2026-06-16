package com.sky.service.impl;

import com.sky.entity.AddressBook;
import com.sky.mapper.AddressbookMapper;
import com.sky.service.AddressbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressbookServiceImpl implements AddressbookService {

    @Autowired
    private AddressbookMapper addressbookMapper;

    public void addAddressBook(AddressBook addressBook) {
        if (addressBook.getIsDefault() == null) {
            addressBook.setIsDefault(0);
        }
        addressbookMapper.addAddressBook(addressBook);
    }

    public List<AddressBook> getAddressBookList(AddressBook addressBook) {
        List<AddressBook> list = addressbookMapper.getAddressBookList(addressBook);
        return list;
    }

    public AddressBook getById(Long id) {
        AddressBook addressBook = addressbookMapper.getById(id);
        return addressBook;
    }
}
