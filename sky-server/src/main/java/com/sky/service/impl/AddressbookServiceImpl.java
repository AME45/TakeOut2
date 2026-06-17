package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressbookMapper;
import com.sky.service.AddressbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void deleteById(Long id) {
        addressbookMapper.deleteById(id);
    }

    public void updateById(AddressBook addressBook) {
        addressbookMapper.updateById(addressBook);
    }

    @Transactional
    public void updateDefaultAddressBookById(AddressBook addressBook) {
        /*
        List<AddressBook> list = addressbookMapper.getAddressBookList(addressBook);
        for (AddressBook addressBook1 : list)
            if(addressBook1.getId() != addressBook.getId())
                addressBook1.setIsDefault(0);
            else
                addressBook1.setIsDefault(1);
        addressbookMapper.updateDefaultAddressBookById(list);
*/

        // 2. 将该用户所有地址置为非默认
        addressbookMapper.setAllNotDefault(BaseContext.getCurrentId());
        // 3. 将指定地址置为默认
        addressbookMapper.setDefault(addressBook.getId());
    }

    public AddressBook getDefaultAddressBook(Long userId) {
        return addressbookMapper.getDefaultAddressBook(userId);
    }
}
