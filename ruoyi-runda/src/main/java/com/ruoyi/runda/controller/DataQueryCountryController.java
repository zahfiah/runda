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
import com.ruoyi.runda.domain.DataQueryCountry;
import com.ruoyi.runda.service.IDataQueryCountryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 国控数据Controller
 *
 * @author ruoyi
 * @date 2025-02-25
 */
@RestController
@RequestMapping("/runda/country")
public class DataQueryCountryController extends BaseController
{
    @Autowired
    private IDataQueryCountryService dataQueryCountryService;

    /**
     *
     * api获取国控数据
     * @throws
     */
    //@PreAuthorize("@ss.hasPermi('runda:country:api')")
    @GetMapping("/getApi")
    //@ResponseBody
    public String newsApi() throws Exception {

        dataQueryCountryService.httpRequest();
        return "获取国控数据成功";
    }

    /**
     * 查询国控数据列表
     */
    @PreAuthorize("@ss.hasPermi('runda:country:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataQueryCountry dataQueryCountry)
    {
        startPage();
        List<DataQueryCountry> list = dataQueryCountryService.selectDataQueryCountryList(dataQueryCountry);
        return getDataTable(list);
    }

    /**
     * 导出国控数据列表
     */
    @PreAuthorize("@ss.hasPermi('runda:country:export')")
    @Log(title = "国控数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataQueryCountry dataQueryCountry)
    {
        List<DataQueryCountry> list = dataQueryCountryService.selectDataQueryCountryList(dataQueryCountry);
        ExcelUtil<DataQueryCountry> util = new ExcelUtil<DataQueryCountry>(DataQueryCountry.class);
        util.exportExcel(response, list, "国控数据数据");
    }

    /**
     * 获取国控数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('runda:country:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataQueryCountryService.selectDataQueryCountryById(id));
    }

    /**
     * 新增国控数据
     */
    @PreAuthorize("@ss.hasPermi('runda:country:add')")
    @Log(title = "国控数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataQueryCountry dataQueryCountry)
    {
        return toAjax(dataQueryCountryService.insertDataQueryCountry(dataQueryCountry));
    }

    /**
     * 修改国控数据
     */
    @PreAuthorize("@ss.hasPermi('runda:country:edit')")
    @Log(title = "国控数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataQueryCountry dataQueryCountry)
    {
        return toAjax(dataQueryCountryService.updateDataQueryCountry(dataQueryCountry));
    }

    /**
     * 删除国控数据
     */
    @PreAuthorize("@ss.hasPermi('runda:country:remove')")
    @Log(title = "国控数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dataQueryCountryService.deleteDataQueryCountryByIds(ids));
    }
}
