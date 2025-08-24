package com.gialong.facebook.email;

import com.gialong.facebook.email.request.EmailRequest;
import com.gialong.facebook.email.request.SendEmailRequest;
import com.gialong.facebook.email.request.Sender;
import com.gialong.facebook.exception.AppException;
import com.gialong.facebook.exception.ErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailClient emailClient;

    @Value("${notification.email.brevo-apikey}")
    String apiKey;

    public void sendEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("Gia Long Dang Pham")
                        .email("longdpg.t1.2023@gmail.com")
                        .build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(request.getHtmlContent())
                .build();
        try {
            emailClient.sendEmail(apiKey, emailRequest);
        } catch (FeignException e){
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }
}
