package com.genius.gitget.store.payment.service;

import static java.lang.Long.valueOf;

import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.store.payment.config.TossPaymentConfig;
import com.genius.gitget.store.payment.domain.Payment;
import com.genius.gitget.store.payment.dto.PaymentFailRequest;
import com.genius.gitget.store.payment.dto.PaymentRequest;
import com.genius.gitget.store.payment.dto.PaymentResponse;
import com.genius.gitget.store.payment.dto.PaymentSuccessRequest;
import com.genius.gitget.store.payment.dto.PaymentSuccessResponse;
import com.genius.gitget.store.payment.repository.PaymentRepository;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final TossPaymentConfig tossPaymentConfig;

    @Transactional
    public PaymentResponse requestTossPayment(User user, PaymentRequest paymentRequest) {
        User findUser = userRepository.findByIdentifier(user.getIdentifier())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!findUser.getIdentifier().equals(paymentRequest.getUserEmail())) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        if (paymentRequest.getAmount() < 100L) {
            throw new BusinessException(ErrorCode.FAILED_POINT_PAYMENT);
        }
        if (!(paymentRequest.getAmount() == 1000L || paymentRequest.getAmount() == 3000L
                || paymentRequest.getAmount() == 5000L || paymentRequest.getAmount() == 7000L)) {
            throw new BusinessException(ErrorCode.FAILED_POINT_PAYMENT);
        }
        Payment requestToEntity = paymentRequest.paymentRequestToEntity(user, paymentRequest);
        Payment savedPayment = paymentRepository.save(requestToEntity);
        return PaymentResponse.createByEntity(savedPayment);
    }

    @Transactional
    public PaymentSuccessResponse tossPaymentSuccess(PaymentSuccessRequest paymentSuccessRequest) throws Exception {
        Payment payment = verifyPayment(paymentSuccessRequest.getOrderId(),
                valueOf(paymentSuccessRequest.getAmount()));
        PaymentSuccessResponse result = requestPaymentAccept(paymentSuccessRequest);
        payment.setPaymentSuccessStatus(paymentSuccessRequest.getPaymentKey(), true);

        User user = userRepository.findByIdentifier(payment.getUser().getIdentifier())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        user.updatePoints(payment.getPointAmount());

        return result;
    }

    @Transactional
    public PaymentSuccessResponse requestPaymentAccept(PaymentSuccessRequest paymentSuccessRequest) throws Exception {
        String orderId;
        String amount;
        String paymentKey;

        paymentKey = paymentSuccessRequest.getPaymentKey();
        orderId = paymentSuccessRequest.getOrderId();
        amount = String.valueOf(paymentSuccessRequest.getAmount());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("paymentKey", paymentKey);
        hashMap.put("orderId", orderId);
        hashMap.put("amount", String.valueOf(amount));

        JSONObject obj = new JSONObject(hashMap);

        String widgetSecretKey = tossPaymentConfig.getTestSecretKey();
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        URL url = new URL(TossPaymentConfig.URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_ORDERID));

        if (!((jsonObject.get("orderId") != null && jsonObject.get("orderId") == payment.getOrderId())
                && (jsonObject.get("paymentKey") != null && jsonObject.get("paymentKey") == payment.getPaymentKey()))) {
            throw new BusinessException(ErrorCode.FAILED_FINAL_PAYMENT);
        }

        return PaymentSuccessResponse.createByEntity(payment);
    }

    public Payment verifyPayment(String orderId, Long amount) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new BusinessException(
                ErrorCode.MEMBER_NOT_FOUND));
        if (amount < 100L) {
            throw new BusinessException(ErrorCode.FAILED_POINT_PAYMENT);
        }
        if (payment.getAmount().equals(amount)) {
            Long pointAmount = payment.getPointAmount();
            if (pointAmount == (amount / 10L) && (pointAmount * 10L) == amount) {
                return payment;
            }
        }
        throw new BusinessException(ErrorCode.INVALID_PAYMENT_AMOUNT);
    }

    public void tossPaymentFail(PaymentFailRequest paymentFailRequest) {
        Payment payment = paymentRepository.findByOrderId(paymentFailRequest.getOrderId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.FAILED_FINAL_PAYMENT));
        payment.setPaymentFailStatus(paymentFailRequest.getMessage(), false);
    }
}