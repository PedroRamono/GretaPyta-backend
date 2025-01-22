package com.az.gretapyta.questionnaires.dto2;

import com.az.gretapyta.qcore.dto.BaseDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class UserDTO extends BaseDTO {

  @Size(max = 64)
  private String firstName;

  @Size(max = 64)
  private String middleName;

  @Size(max = 64)
  private String lastName;

  @Size(max = 64)
  private String emailAddress;

  private String gender;

  private LocalDate birthday;

  private int age;

  @NotNull
  @Size(max = 64)
  private String loginName;

  @Size(max = 255)
  private String passwordHash;

  @Size(max = 4)
  private String preferredLang;

  private boolean anonymousUser;

  private String role;

  private List<UserQuestionnaireDTO> userQuestionnairesDTO;
}