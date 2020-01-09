package com.gzu.queswer.service;

public interface IAnswerService {

    /**
     * 添加回答
     *
     * @param username 用户名
     * @param qid      问题id
     * @param answer   回答内容
     * @return
     */
    public Integer addAnswer(String username, Long qid, String answer);

    /**
     * 修改回答
     *
     * @param aid    回答id
     * @param answer 回答内容
     * @return
     */
    public Integer updateAnswer(Long aid, String answer);

    /**
     * 删除回答
     *
     * @param aid 回答id
     * @return
     */
    public Integer deleteAnswer(Long aid);

    /**
     * 赞同回答
     *
     * @param username 用户名
     * @param aid      回答id
     * @return
     */
    public Integer agree(String username, Long aid);

    /**
     * 反对回答
     *
     * @param username 用户名
     * @param aid      回答id
     * @return
     */
    public Integer disagree(String username, Long aid);
}
