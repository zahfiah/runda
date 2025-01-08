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
import com.ruoyi.runda.domain.EnterpriseList;
import com.ruoyi.runda.service.IEnterpriseListService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 单位名录Controller
 * 
 * @author runda
 * @date 2025-01-07
 */
@RestController
@RequestMapping("/runda/list")
public class EnterpriseListController extends BaseController
{
    @Autowired
    private IEnterpriseListService enterpriseListService;

    /**
     * 查询单位名录列表
     */
    @PreAuthorize("@ss.hasPermi('runda:list:list')")
    @GetMapping("/list")
    public TableDataInfo list(EnterpriseList enterpriseList)
    {
        startPage();
        List<EnterpriseList> list = enterpriseListService.selectEnterpriseListList(enterpriseList);
        return getDataTable(list);
    }

    /**
     * 导出单位名录列表
     */
    @PreAuthorize("@ss.hasPermi('runda:list:export')")
    @Log(title = "单位名录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EnterpriseList enterpriseList)
    {
        List<EnterpriseList> list = enterpriseListService.selectEnterpriseListList(enterpriseList);
        ExcelUtil<EnterpriseList> util = new ExcelUtil<EnterpriseList>(EnterpriseList.class);
        util.exportExcel(response, list, "单位名录数据");
    }

    /**
     * 获取单位名录详细信息
     */
    @PreAuthorize("@ss.hasPermi('runda:list:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(enterpriseListService.selectEnterpriseListById(id));
    }

    /**
     * 新增单位名录
     */
    @PreAuthorize("@ss.hasPermi('runda:list:add')")
    @Log(title = "单位名录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EnterpriseList enterpriseList)
    {
        return toAjax(enterpriseListService.insertEnterpriseList(enterpriseList));
    }

    /**
     * 修改单位名录
     */
    @PreAuthorize("@ss.hasPermi('runda:list:edit')")
    @Log(title = "单位名录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EnterpriseList enterpriseList)
    {
        return toAjax(enterpriseListService.updateEnterpriseList(enterpriseList));
    }

    /**
     * 删除单位名录
     */
    @PreAuthorize("@ss.hasPermi('runda:list:remove')")
    @Log(title = "单位名录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(enterpriseListService.deleteEnterpriseListByIds(ids));
    }
}
