package com.genius.gitget.payment;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.AccessToken;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.PaymentCancelDetail;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
public class IamportRestTest {
    IamportClient client;

    @Value("${imp.api.key}")
    String apiKey;
    @Value("${imp.api.secretkey}")
    String secretKey;

    @BeforeEach
    public void setup() {
        client = new IamportClient(apiKey, secretKey);
    }

    @Test
    public void testGetToken() {
        try {
            IamportResponse<AccessToken> auth_response = client.getAuth();
            System.out.println("auth_response.getToken() = " + auth_response.getResponse().getToken());
            assertNotNull(auth_response.getResponse());
            assertNotNull(auth_response.getResponse().getToken());
        } catch (IamportResponseException e) {
            System.out.println(e.getMessage());

            switch (e.getHttpStatusCode()) {
                case 401 :
                    break;
                case 500 :
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPaymentByImpUid() {
        String test_imp_uid = "imp00265320";
        try {
            IamportResponse<Payment> payment_response = client.paymentByImpUid(test_imp_uid);

            assertNotNull(payment_response.getResponse());
            assertEquals(test_imp_uid, payment_response.getResponse().getImpUid());
        } catch (IamportResponseException e) {
            System.out.println("테스트 1: " + e.getMessage());

            switch (e.getHttpStatusCode()) {
                case 401:
                    //TODO
                    break;
                case 500:
                    //TODO
                    break;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String test_imp_uid_cancelled = "imp00265320";
        try {
            IamportResponse<Payment> cancelled_response = client.paymentByImpUid(test_imp_uid_cancelled);

            Payment cancelled = cancelled_response.getResponse();
            PaymentCancelDetail[] cancelDetail = cancelled.getCancelHistory();

            assertEquals(cancelDetail.length, 1);
            assertNotNull(cancelDetail[0].getPgTid());
        } catch (IamportResponseException e) {
            System.out.println("테스트 2: " + e.getMessage());

            switch (e.getHttpStatusCode()) {
                case 401:
                    //TODO
                    break;
                case 500:
                    //TODO
                    break;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
