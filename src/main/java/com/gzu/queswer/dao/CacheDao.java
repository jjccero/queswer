package com.gzu.queswer.dao;

import com.gzu.queswer.model.StringIndex;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CacheDao {
    List<Long> selectQIds();

    List<Long> selectAIdsByQId(Long qId);

    List<Long> selectRIdsByAId(Long aId);

    List<StringIndex> selectQuestionIndexs();

    List<StringIndex> selectUserIndexs();
}
