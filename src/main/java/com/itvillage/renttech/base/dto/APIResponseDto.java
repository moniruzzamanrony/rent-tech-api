package com.itvillage.renttech.base.dto;

import lombok.Data;

@Data
public class APIResponseDto<T> {
  private int code;
  private String message;
  private T body;

  public APIResponseDto(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public APIResponseDto(int code, String message, T body) {
    this.code = code;
    this.message = message;
    this.body = body;
  }

  public APIResponseDto(int code, T body) {
    this.code = code;
    this.message = getMessageByStatusCode(code);
    this.body = body;
  }

  private String getMessageByStatusCode(int code) {
    switch (code) {
      case 200:
        return "Success";
    }
    return "";
  }
}
