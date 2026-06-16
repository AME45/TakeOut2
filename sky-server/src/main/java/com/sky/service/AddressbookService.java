package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressbookService {
    void addAddressBook(AddressBook addressBook);

    List<AddressBook> getAddressBookList(AddressBook addressBook);

    AddressBook getById(Long id);
}
