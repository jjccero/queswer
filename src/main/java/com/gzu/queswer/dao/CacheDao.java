package com.gzu.queswer.dao;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CacheDao {
    List selectAids();
    List selectQuestionIndexs();
}
