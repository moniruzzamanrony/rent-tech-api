package com.itvillage.renttech.shiftingcontact;


import com.itvillage.renttech.base.constants.ApiConstant;
import com.itvillage.renttech.base.controller.MagicController;
import com.itvillage.renttech.base.dto.APIResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstant.PRIVATE_BASE_API+"/shifting-contact")
public class ShiftingContactController extends MagicController<ShiftingContactService, ShiftingContact> {
    private final ShiftingContactService shiftingContactService;
    public ShiftingContactController(ShiftingContactService service) {
        super(service);
        this.shiftingContactService = service;
    }

    @GetMapping("/list")
    public APIResponseDto<Page<ShiftingContact>> getShiftingContacts(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ShiftingContact> responses = shiftingContactService.getShiftingContacts(pageable);
        return new APIResponseDto<>(HttpStatus.OK.value(), responses);
    }
}
