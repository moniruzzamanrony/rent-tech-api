package com.itvillage.renttech.base.modules.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class BdBulkSmsService implements SMSService {

  @Value("${sms.bdbulksms.net.host}")
  private String host;

  @Value("${sms.bdbulksms.net.token}")
  private String token;

  @Override
  public void sendSms(SMSRequest smsRequest) {
    String apiUrl =
        host
            + "?token="
            + token
            + "&to="
            + smsRequest.getPhoneNumber()
            + "&message="
            + smsRequest.getText().replaceAll(" ", "+");
    try {
      HttpClient httpClient = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder().uri(new URI(apiUrl)).GET().build();
      httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    } catch (URISyntaxException | IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
