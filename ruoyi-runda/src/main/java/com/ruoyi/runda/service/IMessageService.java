package com.ruoyi.runda.service;

import java.util.List;
import com.ruoyi.runda.domain.Message;

/**
 * 消息通知Service接口
 *
 * @author ruoyi
 * @date 2025-02-25
 */
public interface IMessageService
{
    /**
     * 查询消息通知
     *
     * @param id 消息通知主键
     * @return 消息通知
     */
    public Message selectMessageById(Long id);

    /**
     * 查询消息通知列表
     *
     * @param message 消息通知
     * @return 消息通知集合
     */
    public List<Message> selectMessageList(Message message);

    /**
     * 新增消息通知
     *
     * @param message 消息通知
     * @return 结果
     */
    public int insertMessage(Message message);

    /**
     * 修改消息通知
     *
     * @param message 消息通知
     * @return 结果
     */
    public int updateMessage(Message message);

    /**
     * 批量删除消息通知
     *
     * @param ids 需要删除的消息通知主键集合
     * @return 结果
     */
    public int deleteMessageByIds(Long[] ids);

    /**
     * 删除消息通知信息
     *
     * @param id 消息通知主键
     * @return 结果
     */
    public int deleteMessageById(Long id);

    public  int updateRead(Message message);

    public int selectIsRead(Message message);
}
