package cn.dm.service;

import cn.dm.common.Dto;
import cn.dm.pojo.DmItem;
import cn.dm.pojo.DmItemType;
import cn.dm.vo.DmFloorItems;
import cn.dm.vo.DmItemTypeVo;
import cn.dm.vo.HotItemVo;

import java.util.List;

/***
 * 处理首页业务逻辑的service
 */
public interface HomeService {
    /***
     * 获取首页所有的商品分类
     */
    public List<DmItemTypeVo> queryAllItemType()throws Exception;
    /***
     * 获取横向导航条
     * @return
     * @throws Exception
     */
    public List<DmItemType> queryTransverse()throws Exception;
    /***
     * 查询首页轮播图
     * @return
     * @throws Exception
     */
    public List<HotItemVo> queryBanner()throws Exception;
    /***
     * 查询即将开售接口
     * @return
     * @throws Exception
     */
    public List<HotItemVo> queryToSaleItem()throws Exception;
    /***
     * 查询今日推荐接口
     * @return
     * @throws Exception
     */
    public List<HotItemVo> queryTodayRecommend()throws Exception;
    /***
     * 查询不同楼层的数据
     * @return
     * @throws Exception
     */
    public List<DmFloorItems> queryFloorItems()throws Exception;
    /***
     * 查询热门节目
     * @return
     * @throws Exception
     */
    public List<HotItemVo> queryHotItems(Long itemTypeId) throws Exception;
    /***
     * 添加条目
     * @param dmItem
     */
    public void addItem(DmItem dmItem);
}
