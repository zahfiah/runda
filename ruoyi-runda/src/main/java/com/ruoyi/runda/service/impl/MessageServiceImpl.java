package com.ruoyi.runda.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.runda.mapper.MessageMapper;
import com.ruoyi.runda.domain.Message;
import com.ruoyi.runda.service.IMessageService;

/**
 * 消息通知Service业务层处理
 *
 * @author ruoyi
 * @date 2025-02-25
 */
@Service
public class MessageServiceImpl implements IMessageService
{
    //@Autowired
    private MessageMapper messageMapper;

    /**
     * 查询消息通知
     *
     * @param id 消息通知主键
     * @return 消息通知
     */
    @Override
    public Message selectMessageById(Long id)
    {
        return messageMapper.selectMessageById(id);
    }

    /**
     * 查询消息通知列表
     *
     * @param message 消息通知
     * @return 消息通知
     */
    @Override
    public List<Message> selectMessageList(Message message)
    {
        return messageMapper.selectMessageList(message);
    }

    /**
     * 新增消息通知
     *
     * @param message 消息通知
     * @return 结果
     */
    @Override
    public int insertMessage(Message message)
    {
        return messageMapper.insertMessage(message);
    }

    /**
     * 修改消息通知
     *
     * @param message 消息通知
     * @return 结果
     */
    @Override
    public int updateMessage(Message message)
    {
        return messageMapper.updateMessage(message);
    }

    /**
     * 批量删除消息通知
     *
     * @param ids 需要删除的消息通知主键
     * @return 结果
     */
    @Override
    public int deleteMessageByIds(Long[] ids)
    {
        return messageMapper.deleteMessageByIds(ids);
    }

    /**
     * 删除消息通知信息
     *
     * @param id 消息通知主键
     * @return 结果
     */
    @Override
    public int deleteMessageById(Long id)
    {
        return messageMapper.deleteMessageById(id);
    }
    @Override
    public int selectIsRead(Message message)
    {
        return messageMapper.selectIsRead(message);
    }

    @Override
    public int updateRead(Message message)
    {
        return messageMapper.updateRead(message);
    }


}
