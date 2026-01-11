package com.itvillage.renttech.shiftingcontact;


import com.itvillage.renttech.base.service.MagicService;
import org.springframework.stereotype.Service;

@Service
public class ShiftingContactService extends MagicService<ShiftingContact, String> {
    public ShiftingContactService(ShiftingContactRepository repository) {
        super(repository);
    }
}
