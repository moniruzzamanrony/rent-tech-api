package com.itvillage.renttech.base.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class BaseDto {

  private String id;

  private int version;

  private ZonedDateTime createdDate;

  private ZonedDateTime modifiedDate;

  private String createdBy;

  private String updatedBy;
}
