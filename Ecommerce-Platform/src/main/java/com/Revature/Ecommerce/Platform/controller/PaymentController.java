//package com.Revature.Ecommerce.Platform.controller;
//
//import com.Revature.Ecommerce.Platform.service.PaymentService;
//import com.razorpay.Order;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/payment")
//public class PaymentController {
//
//    @Autowired
//    private PaymentService paymentService;
//
//    @PostMapping("/create-order")
//    public String createOrder(@RequestParam int amount) throws Exception {
//
//        Order order = paymentService.createOrder(amount);
//
//        return order.toString();
//    }
//
//    @PostMapping("/verify")
//    public String verifyPayment(@RequestParam String orderId,
//                                @RequestParam String paymentId,
//                                @RequestParam String signature) throws Exception {
//
//        boolean isValid = paymentService.verifyPayment(orderId, paymentId, signature);
//
//        if (isValid) {
//            return "Payment successful";
//        } else {
//            return "Payment failed";
//        }
//    }
//}