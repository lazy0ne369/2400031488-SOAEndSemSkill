package soa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import soa.service.GatewayService;

@RestController
public class GatewayController {

    GatewayService GS;

    public GatewayController(GatewayService GS) {
        this.GS = GS;
    }

    @RequestMapping(value = {
        "/rbc",
        "/rbc/**",
        "/gateway/books",
        "/gateway/books/**"
    })
    public ResponseEntity<byte[]> forward(
            HttpServletRequest request,
            @RequestBody(required = false) byte[] body) {
        return GS.forward(request, body);
    }
}