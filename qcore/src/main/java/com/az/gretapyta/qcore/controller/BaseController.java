package com.az.gretapyta.qcore.controller;

import jakarta.persistence.MappedSuperclass;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
@MappedSuperclass
public abstract class BaseController {
  private static final String RETURN_INFO_TYPE_KEY = "Return-Type";
  private static final String RETURN_INFO_IS_COLLECTION_KEY = "Return-is-Collection";
  private static final String IS_COLLECTION_NO = "0";
  private static final String IS_COLLECTION_YES = "1";

  /* Pass info about return content (type, is a Collection of...) */
  protected void SetInHeaderReturnEntityInfo( HttpServletResponse response,
                                              String className,
                                              boolean isCollectionOf ) {
    response.setHeader(RETURN_INFO_TYPE_KEY, className);
    response.setHeader(RETURN_INFO_IS_COLLECTION_KEY, (isCollectionOf ? IS_COLLECTION_YES : IS_COLLECTION_NO));
  }
}