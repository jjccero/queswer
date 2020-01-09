package com.gzu.queswer.service;

public interface ICommentService {

    /**
     * 添加评论
     *
     * @param username 用户名
     * @param aid      回答id
     * @param comment  评论内容
     * @return
     */
    public Integer addComment(String username, Long aid, String comment);
}
