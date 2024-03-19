package com.ahd.ecommerce.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping
    public String getIndex(Model model){
        Object[] productObjects = restTemplate.getForObject("http://localhost:9090/ecommerce/products", Object[].class);
        List<Object> products = Arrays.asList(productObjects);

        Object[] orderObjects = restTemplate.getForObject("http://localhost:9090/ecommerce/orders", Object[].class);
        List<Object> orders = Arrays.asList(orderObjects);

        model.addAttribute("products", products);
        model.addAttribute("orders", orders);
        return "index";
    }
}
