package com.az.gretapyta.questionnaires.service;

import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.util.Constants;
import com.az.gretapyta.questionnaires.model.Drawer;
import com.az.gretapyta.questionnaires.repository.DrawersRepository;
import com.az.gretapyta.questionnaires.service.impl.DrawersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class DrawerServiceTest {

  public static final int TEST_ID_OK = 11;
  public static final int TEST_ID_WRONG = -99;
  public static final int TIMES_ONCE = 1;

  DrawersRepository mockRepository = mock(DrawersRepository.class);

  @InjectMocks
  DrawersServiceImpl service;

  private Drawer drawer1;
  private Drawer drawer2;

  @BeforeEach
  public void setUp() {
    Map<String, String> elements1 = new TreeMap<>();
    elements1.put("en", "Test: Social Media");
    elements1.put("pl", "Test: Media Społecznościowe");
    elements1.put("ru", "Test: Социальные медиа");
    drawer1 = new Drawer();
    drawer1.setId(TEST_ID_OK);
    drawer1.setCode("DRW_CODE_MOCK1");
    drawer1.setNameMultilang(elements1);
    drawer1.setReady2Show(false);
    drawer1.setCreated(LocalDateTime.now());
    drawer1.setUpdated(LocalDateTime.now());

    Map<String, String> elements2 = new TreeMap<>();
    elements2.put("en", "Test: Politics");
    elements2.put("pl", "Test: Polityka");
    elements2.put("ru", "Test: Πолитика");
    drawer2 = new Drawer();
    drawer2.setCode("DRW_CODE_MOCK2");
    drawer2.setNameMultilang(elements2);
    drawer2.setReady2Show(false);
    drawer2.setCreated(LocalDateTime.now());
    drawer2.setUpdated(LocalDateTime.now());

    Mockito.when(mockRepository.findById(TEST_ID_OK)).thenReturn(Optional.ofNullable(drawer1));
    Mockito.when(mockRepository.findByCode("DRW_CODE_MOCK2")).thenReturn(Optional.ofNullable(drawer2));
    Mockito.when(mockRepository.findAll(Sort.by("code"))).thenReturn(Arrays.asList(drawer1, drawer2));

    Mockito.when(mockRepository.save(drawer2)).thenReturn(drawer2);
  }

  //------------------------------------------------------------------------//
  //---/ TEST PART /--------------------------------------------------------//
  @Test
  @Order(value = 1)
  @DisplayName("(1) When given valid Drawer's ID, then Drawer should be fund.")
  public void test1() {
    final Drawer result = service.getItemByIdNoUserFilter(TEST_ID_OK);

    assertEquals(drawer1, result);
  }

  @Test
  @Order(value = 2)
  @DisplayName("(2) When given Drawer's ID not valid, then Drawer should not be fund and NotFoundException to be thrown.")
  public void test2() {
    try {
      Drawer result = service.getItemByIdNoUserFilter(TEST_ID_WRONG);
    } catch (Exception e) {
      assertThat(e).isInstanceOf(NotFoundException.class);
    }
    verifyFindByIdIsCalledOnce();
  }

  @Test
  @Order(value = 3)
  @DisplayName("(3) When given valid Drawer's code, then Drawer should be fund.")
  public void test3() {
    String validCode = "DRW_CODE_MOCK2";
    Optional<Drawer> fromDb = service.getItemByCodeNoUserFilter(validCode);

    assertThat(fromDb).isNotNull();
    assertThat(fromDb.get().getCode()).isEqualTo(validCode);
  }

  @Test
  @Order(value = 4)
  @DisplayName("(4) When given Drawer's code not valid, then Drawer should not be fund and NotFoundException to be thrown.")
  public void test4() {
    String invalidCode =  "INVALID_DRW_CODE";
    try {
      Optional<Drawer> fromDb = service.getItemByCodeNoUserFilter(invalidCode);
    } catch (Exception e) {
      assertThat(e).isInstanceOf(NotFoundException.class);
    }
  }

  @Test
  @Order(value = 5)
  @DisplayName("(5) Given 2 Drawers, when get all, then returns 2 records with valid codes.")
  public void test5() {
    List<Drawer> drawersList = Arrays.asList(drawer1, drawer2);
    List<Drawer> allItems = service.getAllItems();

    verifyFindAllEntitiesIsCalledOnce();
    assertThat(allItems).hasSize(2);
    assertThat(allItems).extracting(Drawer::getCode).contains(drawersList.get(0).getCode(), drawersList.get(1).getCode());
  }

  @Test
  @Order(value = 6)
  @DisplayName("(6) When create Drawer, then new Drawer should be returned.")
  public void test6() throws Exception {
    Drawer testEntity = drawer2;
    Drawer newEntity = service.createEntity(testEntity, Constants.DEFAULT_LOCALE);

    assertThat(newEntity).isNotNull();
    assertThat(newEntity.getCode()).isEqualTo(drawer2.getCode());
  }
  //---/ TEST PART /--------------------------------------------------------//
  //------------------------------------------------------------------------//

  private void verifyFindByIdIsCalledOnce() {
    Mockito.verify(mockRepository, VerificationModeFactory.times(TIMES_ONCE)).findById(Mockito.anyInt());
    Mockito.reset(mockRepository);
  }

  private void verifyFindAllEntitiesIsCalledOnce() {
    Mockito.verify(mockRepository, VerificationModeFactory.times(TIMES_ONCE)).findAll(Sort.by("code"));
    Mockito.reset(mockRepository);
  }
}