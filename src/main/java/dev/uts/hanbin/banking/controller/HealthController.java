package dev.uts.hanbin.banking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
public class HealthController {
  @GetMapping
  public String ping() {
    return "pong";
  }
}
