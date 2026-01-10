package com.itvillage.renttech.base.modules.sms;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SMSRequest {
  private String phoneNumber;
  private String text;
}
