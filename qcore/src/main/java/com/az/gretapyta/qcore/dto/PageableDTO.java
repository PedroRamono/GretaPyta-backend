package com.az.gretapyta.qcore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

//TODO ...
//@Getter
//@AllArgsConstructor
//public class PageableDTO<T> {
//  private final int pages;
//  private final long elements;
//  private final List<T> data;
//}

// @Getter
// @AllArgsConstructor
public record PageableDTO<T>(int pages, long elements, List<T> data) {
}