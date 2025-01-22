package com.az.gretapyta.qcore.controller;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.util.CommonUtilities;
import jakarta.persistence.MappedSuperclass;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
@MappedSuperclass
public abstract class BaseController {
  public static final String REQ_ATTRIB_USER_IDENTIFIER = "userIdentifier";

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

  protected int getUserIdFromRequest(HttpServletRequest request, String langCode, Class className) throws BusinessException {
    Object oUserIdentifier = request.getAttribute(REQ_ATTRIB_USER_IDENTIFIER);
    if (oUserIdentifier == null) {
      log.error(String.format("==> Error: No User Id attribute found in HTTP Request - attribute '%s' is missing.", REQ_ATTRIB_USER_IDENTIFIER));
      String localeMess = CommonUtilities.getTranslatableMessage(
          "error.user_id_attribute_missing_in_request",
          langCode);
      throw new BusinessException(localeMess);
    }

    try {
      // return Integer.valueOf(oUserIdentifier.toString());
      return Integer.parseInt(oUserIdentifier.toString());
    } catch (Exception ex) {
      log.error(String.format("==> Error: Cannot convert'%s' into User ID number !", oUserIdentifier.toString()));
      String errMess = "error.user_id_attribute_in_wrong_format";
      String localeMess = CommonUtilities.getTranslatableMessage(errMess, langCode);
      assert localeMess != null;
      throw new BusinessException( localeMess.formatted(oUserIdentifier.toString()),
          ex.fillInStackTrace() + ":" + ex.getMessage(), className);
    }
  }

  protected int getUserIdFromRequestOrZero(HttpServletRequest request, String langCode, Class className) {
    try {
      return this.getUserIdFromRequest(request, langCode, this.getClass());
    } catch (Exception e) {
      return 0;
    }
  }
}