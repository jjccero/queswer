package com.gzu.queswer.dao;

import com.gzu.queswer.model.StringIndex;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchDao {
    List<StringIndex> selectQuestionIndexs();

    List<StringIndex> selectUserIndexs();
}
