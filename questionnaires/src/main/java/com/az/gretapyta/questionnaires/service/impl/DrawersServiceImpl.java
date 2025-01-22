package com.az.gretapyta.questionnaires.service.impl;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.jpa.GenericSpecification;
import com.az.gretapyta.questionnaires.model.Drawer;
import com.az.gretapyta.questionnaires.repository.DrawersRepository;
import com.az.gretapyta.questionnaires.service.DrawersService;
import com.az.gretapyta.questionnaires.service2.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class DrawersServiceImpl extends BaseServiceImpl implements DrawersService {

  private final DrawersRepository repository;
  private final UsersService usersService;

  @Override
  public List<Drawer> getAllItems() {
    return repository.findAll(Sort.by("code"));
  }

  @Override
  public List<Drawer> getAllItems(int userId) {
    if (usersService.isAdministrator(userId)) {
      return getAllItems();
    } else {
      Specification<Drawer> specOr = GenericSpecification.getReady2ShowOrOwnerUserSpecs(userId);
      List<Drawer> list = repository.findAll(specOr, Sort.by("code"));

      list.forEach(n -> n.filterChildrenOnReady2Show(false, userId));
      return list;
    }
  }

  @Override
  public Drawer getItemByIdNoUserFilter(int id) {
    return repository.findById(id)
        .orElseThrow(NotFoundException::new);
  }

  @Override
  public Drawer getItemById(int id, int userId) {
    if (usersService.isAdministrator(userId)) {
      return getItemByIdNoUserFilter(id);
    } else {
      Specification<Drawer> specAndOr = GenericSpecification.getIdAndReady2ShowOrOwnerUserSpecs(id, userId);
      Drawer item = repository.findOne(specAndOr).orElseThrow(NotFoundException::new);
      item.filterChildrenOnReady2Show(false, userId);
      return item;
    }
  }

  @Override
  public Optional<Drawer> getItemByCodeNoUserFilter(String code) {
    return repository.findByCode(code);
  }

  @Override
  public Optional<Drawer> getItemByCode(String code, int userId) {
    return repository.findByCode(code);
  }

  @Override
  public Map<Integer, Integer> getQuestionnairesPopularityCounts(Boolean byPopularity) {
    try {
      Object[] rawArray = repository.getQuestionnairesPopularityCounts(byPopularity);
      return CommonUtilities.convertRawArrayOfIdsToMap(rawArray);
    } catch (Exception e) { // InvalidDataAccessResourceUsageException
      log.error(e);
      return null;
    }
  }

  @Override
  public boolean codeExists(final String code) {
    return (getItemByCodeNoUserFilter(code).isPresent());
  }

  @Override
  public Drawer createEntity(Drawer entity, String lang) throws BusinessException {
    validateBeforeCreate(entity, lang);
    return repository.save(entity);
  }

  @Override
  public Drawer updateEntity(Drawer entity, String lang) throws BusinessException {
    validateBeforeUpdate(entity, lang);
    return repository.save(entity);
  }

  protected boolean validateBeforeUpdate(Drawer entity, String lang) throws BusinessException {
    if (super.validateBeforeUpdate(entity, lang)) {
      //(1) get Entity of ID
      //(2) fill in some 'Immutable' fields
      Drawer existingEntity = getItemByIdNoUserFilter(entity.getId());
      validate4PortOutOnUpdate(existingEntity, entity);
      return true;
    }
    return false;
  }

  protected void validate4PortOutOnUpdate(Drawer originalEntity, Drawer destinationEntity) {
    destinationEntity.setCreated(originalEntity.getCreated());
    destinationEntity.setCode(originalEntity.getCode()); // Code is not allowed to be modified.
    // Dependants
    destinationEntity.setQuestionnaires(originalEntity.getQuestionnaires());
  }
}