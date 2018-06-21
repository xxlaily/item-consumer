package cn.dm.service;

import cn.dm.pojo.DmItemType;
import cn.dm.vo.DmItemTypeVo;

import java.util.List;

/****
 * 节目类型Service
 */
public interface ItemTypeService {
    /***
     * 根据父级节点查询类型列表
     * @param parent
     * @return
     */
    public List<DmItemTypeVo> queryDmItemTypeByParent(Integer parent) throws Exception;
}
