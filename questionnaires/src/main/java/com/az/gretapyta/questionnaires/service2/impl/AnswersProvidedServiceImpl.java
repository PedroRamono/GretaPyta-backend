package com.az.gretapyta.questionnaires.service2.impl;

import com.az.gretapyta.qcore.exception.BusinessException;
import com.az.gretapyta.qcore.exception.NotFoundException;
import com.az.gretapyta.qcore.service.BaseServiceImpl;
import com.az.gretapyta.questionnaires.jpa.GenericSpecification;
import com.az.gretapyta.questionnaires.model2.AnswerProvided;
import com.az.gretapyta.questionnaires.repository2.AnswersProvidedRepository;
import com.az.gretapyta.questionnaires.service2.AnswersProvidedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AnswersProvidedServiceImpl extends BaseServiceImpl implements AnswersProvidedService {
  private final AnswersProvidedRepository repository;

  @Override
  public List<AnswerProvided> getAllItems() {
    return repository.findAll();
  }

  @Override
  public AnswerProvided getItemById(Integer id) {
    return repository.findById(id).orElseThrow(NotFoundException::new);
  }

  public Optional<AnswerProvided> getItemByQuestionAnswerId(Integer questionAnswerId) {
    Specification<AnswerProvided> specQuestionAnswerId = GenericSpecification.getParentIdSpecs(
        questionAnswerId,
        "questionAnswer");

    return repository.findAll(specQuestionAnswerId)
        .stream()
        .findFirst();
  }

  @Override
  public AnswerProvided createEntity(AnswerProvided entity, String lang) throws BusinessException {
    validateBeforeCreate(entity, lang);
    return repository.save(entity);
  }
}