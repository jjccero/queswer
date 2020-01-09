package com.gzu.queswer.service;

public interface IQuestionService {

    /**
     * 提问题
     *
     * @param username 用户名
     * @param title    标题
     * @param content  详细内容
     * @return
     */
    public Integer addQuestion(String username, String title, String content);

    /**
     * 删除问题
     *
     * @param username 用户名
     * @param qid      问题id
     * @return
     */
    public Integer deleteQuestion(String username, Long qid);

    /**
     * 修改问题内容
     *
     * @param username 用户名
     * @param qid      问题id
     * @return
     */
    public Integer updateQuestion(String username, Long qid, String content);
}
