package com.az.gretapyta.questionnaires.service.impl;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.qcore.util.CommonUtilities;
import com.az.gretapyta.questionnaires.model.Drawer;
import com.az.gretapyta.questionnaires.repository.DrawersRepository;
import com.az.gretapyta.questionnaires.service.DrawersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class DrawersServiceImpl extends BaseServiceImpl implements DrawersService {
  private final DrawersRepository repository;

  @Override
  public List<Drawer> getAllItems() {
    return repository.findAll(Sort.by("code"));
  }

  @Override
  public Drawer getItemById(Integer id) {
    return repository.findById(id)
        .orElseThrow(NotFoundException::new);
  }

  @Override
  public Optional<Drawer> getItemByCode(String code) {
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
    return (getItemByCode(code).isPresent());
  }

  @Override
  public Drawer createEntity(Drawer entity, String lang) throws BusinessException {
    validateBeforeCreate(entity, lang);
    return repository.save(entity);
  }
}