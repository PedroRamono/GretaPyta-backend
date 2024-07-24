package com.az.gretapyta.questionnaires.model2;

import com.az.gretapyta.qcore.model.BaseEntity;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.security.UserRoles;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static com.az.gretapyta.qcore.util.Constants.BASE_DATE_TIME_FORMAT;

@Entity
// @Data // Not suitable for JPA entities performance-wise.
@Getter
@Setter
@Table(name = "USERS")
public class User extends BaseEntity implements Serializable {

  public static final String ANONYMOUS_PREFIX_NAME = "anonymous_";
  @Serial
  private static final long serialVersionUID = 1L;

  @Column(name = "FIRST_NAME", length = 64)
  private String firstName;

  @Column(name = "MIDDLE_NAME", length = 64)
  private String middleName;

  @Column(name = "LAST_NAME", length = 64)
  private String lastName;

  @Column(name = "EMAIL_ADDRESS", length = 64)
  private String emailAddress;

  @Column(name = "GENDER", length = 4)
  private String gender;

  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonFormat(pattern = BASE_DATE_TIME_FORMAT)
  LocalDate birthday;

  @Column(name = "LOGIN_NAME", nullable = false, unique = true, length = 64)
  private String loginName;

  @Column(name = "PASSWORD_HASH", nullable = true, length = 255)
  private String passwordHash;

  @Column(name = "PREFERRED_LANG", length = 4)
  private String preferredLang;

  @Column(name = "ANONYMOUS_USER")
  private Boolean anonymousUser;

  @Column(name = "ROLE", length = 4, nullable = false)
  private String role;

  // User -> UserQuestionnaire:
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
  private Set<UserQuestionnaire> userQuestionnaires;


  public String getFullName() {
    return firstName + " " + lastName;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getFullName())
        .append(this.getLoginName()==null ? "" : ", login=" + this.getLoginName())
        .append((this.getAge()>0) ?  ", " + this.getAge() + "y" : "")
        .append(this.getGender()==null ? "" : gender)
        .append(", email: " +this.getEmailAddress());
    return sb.toString();
  }

  //----/ Business Logic section: /-------------------------------//
  public int getAge() {
    return CommonUtilities.getAge(getBirthday());
  }

  public static User createAnonymousUserForLanguage(String langCode) {
    return createUser(null,
        null,
        null,
        null,
        null,
        ANONYMOUS_PREFIX_NAME + langCode,
        " ",
         langCode,
        true,
         UserRoles.USER_ANONYMOUS.getCode());
  }

  public static User createUser( String pFirstName,
                                 String pLastName,
                                 String pSexCode,
                                 LocalDate pBirthday,
                                 String pEmail,
                                 String pLoginName,
                                 String pPassword,
                                 String pPreferredLang,
                                 boolean anonymousUser,
                                 String pRoleCode ) {
    User ret = new User();
    ret.setFirstName(pFirstName);
    ret.setLastName(pLastName);
    ret.setGender(pSexCode);
    ret.setBirthday(pBirthday);
    ret.setEmailAddress(pEmail);
    ret.setLoginName(pLoginName);
    ret.setPasswordHash(pPassword);

    ret.setPreferredLang(pPreferredLang);
    ret.setAnonymousUser(anonymousUser);
    ret.setRole(pRoleCode);

    ret.setCreated(LocalDateTime.now());
    ret.setUpdated(LocalDateTime.now());
    return ret;
  }

  public List<String> getRoles() {
    // For future multiple Roles
    List<String> rolesList = new ArrayList<>();
    rolesList.add(getRole());
    return rolesList;
  }

  public Set<GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> authorities = new HashSet<>(getRoles().size());
    for (String role : getRoles()) {
      authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
    }
    return authorities;
  }

  public static List<User> getUsersFromPredicate( List<User> list,
                                                  Predicate<User> tester) {
    return list.stream()
        .filter(tester)
        //.findFirst()
        .toList();
  }
  //----/ Business Logic section: /-------------------------------//
}