package com.Revature.Ecommerce.Platform.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final String KEY = "YOUR_KEY_ID";
    private static final String SECRET = "YOUR_SECRET";

    public Order createOrder(int amount) throws Exception {

        RazorpayClient client = new RazorpayClient(KEY, SECRET);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // convert to paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_rcptid_" + System.currentTimeMillis());

        return client.orders.create(orderRequest);
    }

    public boolean verifyPayment(String orderId, String paymentId, String signature) throws Exception {

        String payload = orderId + "|" + paymentId;

        return Utils.verifySignature(payload, signature, SECRET);
    }
}