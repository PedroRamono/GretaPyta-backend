package com.az.gretapyta.questionnaires.model.interfaces;

import java.util.Iterator;
import java.util.Set;

public interface Presentable {
  Boolean getReady2Show();
  int getCreatorId();
  void filterChildrenOnReady2Show(boolean isAdmin, int creatorId);

  static void filterChildrenOnReady2Show(boolean isAdmin, int creatorId, Set<? extends Presentable> set) {
    for (Iterator<? extends Presentable> iterator = set.iterator(); iterator.hasNext();) {
      Presentable n = iterator.next();
      if (isAdmin || n.getReady2Show() || (n.getCreatorId() == creatorId)) {
        n.filterChildrenOnReady2Show(isAdmin, creatorId);
      } else {
        iterator.remove();
      }
    }
  }
}