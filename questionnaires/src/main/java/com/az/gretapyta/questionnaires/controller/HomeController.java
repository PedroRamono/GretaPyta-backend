package com.az.gretapyta.questionnaires.controller;

import com.az.gretapyta.qcore.controller.APIController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
  public static final String HOME_REQUEST_MESSAGE = "GretaPyta Home !";

  @RequestMapping(APIController.API_ROOT_URL)
  public @ResponseBody String greeting() {
    return HOME_REQUEST_MESSAGE;
  }
}