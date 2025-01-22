package com.az.gretapyta.qcore.controller;

public abstract class APIController {
  public static final String API_ROOT_URL = "/api/ver1";

  public static final String DRAWERS_URL = API_ROOT_URL + "/drawers";
  public static final String QUESTIONNAIRES_URL = API_ROOT_URL + "/questionnaires";
  public static final String STEPS_URL = API_ROOT_URL + "/steps";
  public static final String QUESTIONS_URL = API_ROOT_URL + "/questions";
  public static final String OPTIONS_URL = API_ROOT_URL + "/options";

  //(2)
  public static final String USERS_URL = API_ROOT_URL + "/users";
  public static final String USER_IDENTITY_URL = API_ROOT_URL + "/identity";
  public static final String USER_MESSGES_URL = API_ROOT_URL + "/user-messages";

  public static final String USERS_QUESTIONNAIRES_URL = API_ROOT_URL + "/users-questionnaires";
  public static final String QUESTIONS_ANSWERS_URL = API_ROOT_URL + "/questions-answers";
  public static final String ANSWERS_SELECTED_URL = API_ROOT_URL + "/answers-selected";
  public static final String ANSWERS_PROVIDED_URL = API_ROOT_URL + "/answers-provided";

  /// public static final String RESTRICTED_URL = "/restricted";

  public static final String SEARCH_ENTITY_BY_ID_API = "/searchid/";

  public static final String SEARCH_ENTITY_ON_LANG_TEXT_API = "/searchtxt/";

  public static final String LOGIN_API = "/login";
  public static final String LOGOUT_API = "/logout";
  public static final String CHANGE_PIN_API = "/change-pin";

  public static final String RESTRICTED_ENTITY = "/restricted";
}