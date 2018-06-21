package cn.dm.controller;

import cn.dm.common.Dto;
import cn.dm.common.DtoUtil;
import cn.dm.pojo.DmItemType;
import cn.dm.service.HomeService;
import cn.dm.service.ItemTypeService;
import cn.dm.vo.DmItemTypeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/p/type")
public class ItemTypeController {

    @Autowired
    private ItemTypeService itemTypeService;

    @RequestMapping(value = "/queryItemType",method = RequestMethod.POST)
    public Dto<List<DmItemTypeVo>> queryItemType(@RequestBody Map<String,Object> params)throws Exception{
        Integer parent=Integer.parseInt(params.get("parent").toString());
        List<DmItemTypeVo> list=itemTypeService.queryDmItemTypeByParent(parent);
        return DtoUtil.returnDataSuccess(list);
    }
}
