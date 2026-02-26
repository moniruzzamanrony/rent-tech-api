package com.itvillage.renttech.shiftingcontact;


import com.itvillage.renttech.base.service.MagicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ShiftingContactService extends MagicService<ShiftingContact, String> {
    private final ShiftingContactRepository shiftingContactRepository;
    public ShiftingContactService(ShiftingContactRepository repository) {
        super(repository);
        this.shiftingContactRepository =  repository;
    }

    public Page<ShiftingContact> getShiftingContacts(Pageable pageable) {
        return shiftingContactRepository.findAll(pageable);
    }
}
