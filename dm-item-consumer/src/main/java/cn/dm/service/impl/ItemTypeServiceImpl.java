package cn.dm.service.impl;

import cn.dm.client.RestDmItemTypeClient;
import cn.dm.common.EmptyUtils;
import cn.dm.pojo.DmItemType;
import cn.dm.service.ItemTypeService;
import cn.dm.vo.DmItemTypeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 实现类
 */
@Component
public class ItemTypeServiceImpl implements ItemTypeService{

    @Autowired
    private RestDmItemTypeClient dmItemTypeClient;

    /***
     * 根据parent查询DmItemType
     * @param parent
     * @return
     * @throws Exception
     */
    @Override
    public List<DmItemTypeVo> queryDmItemTypeByParent(Integer parent) throws Exception {
        Map<String,Object> param=new HashMap<String,Object>();
        param.put("parent",parent);
        List<DmItemType> dmItemTypeList=dmItemTypeClient.getDmItemTypeListByMap(param);
        List<DmItemTypeVo> dmItemTypeVos=new ArrayList<DmItemTypeVo>();
        if(EmptyUtils.isNotEmpty(dmItemTypeList)){
            for (DmItemType itemType:dmItemTypeList){
                DmItemTypeVo dmItemTypeVo=new DmItemTypeVo();
                BeanUtils.copyProperties(itemType,dmItemTypeVo);
                dmItemTypeVos.add(dmItemTypeVo);
            }
        }
        return dmItemTypeVos;
    }
}
