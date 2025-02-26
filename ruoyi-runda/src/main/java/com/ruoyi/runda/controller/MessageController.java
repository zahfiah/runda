package com.ruoyi.runda.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.runda.domain.Message;
import com.ruoyi.runda.service.IMessageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 消息通知Controller
 *
 * @author ruoyi
 * @date 2025-02-25
 */
@RestController
@RequestMapping("/runda/message")
public class MessageController extends BaseController
{
    @Autowired
    private IMessageService messageService;

    /**
     * 查询消息通知列表
     */
    @PreAuthorize("@ss.hasPermi('runda:message:list')")
    @GetMapping("/list")
    public TableDataInfo list(Message message)
    {
        startPage();
        List<Message> list = messageService.selectMessageList(message);
        return getDataTable(list);
    }

    /**
     * 导出消息通知列表
     */
    @PreAuthorize("@ss.hasPermi('runda:message:export')")
    @Log(title = "消息通知", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Message message)
    {
        List<Message> list = messageService.selectMessageList(message);
        ExcelUtil<Message> util = new ExcelUtil<Message>(Message.class);
        util.exportExcel(response, list, "消息通知数据");
    }

    /**
     * 获取消息通知详细信息
     */
    @PreAuthorize("@ss.hasPermi('runda:message:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(messageService.selectMessageById(id));
    }

    /**
     * 新增消息通知
     */
    @PreAuthorize("@ss.hasPermi('runda:message:add')")
    @Log(title = "消息通知", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Message message)
    {
        return toAjax(messageService.insertMessage(message));
    }

    /**
     * 修改消息通知
     */
    @PreAuthorize("@ss.hasPermi('runda:message:edit')")
    @Log(title = "消息通知", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Message message)
    {
        return toAjax(messageService.updateMessage(message));
    }

    /**
     * 删除消息通知
     */
    @PreAuthorize("@ss.hasPermi('runda:message:remove')")
    @Log(title = "消息通知", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(messageService.deleteMessageByIds(ids));
    }



    @PreAuthorize("@ss.hasPermi('runda:message:updateRead')")
    @Log(title = "消息管理", businessType = BusinessType.UPDATE)
    @PutMapping("/updateRead")
    public AjaxResult updateRead(@RequestBody Message message)
    {
        return toAjax(messageService.updateRead(message));
    }



    //统计未读
    @PreAuthorize("@ss.hasPermi('runda:message:isRead')")
    @GetMapping("/isRead")
    public int isRead(Message message)
    {
        startPage();
        return messageService.selectIsRead(message);
    }
}
