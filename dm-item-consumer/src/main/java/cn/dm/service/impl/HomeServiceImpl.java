package cn.dm.service.impl;

import cn.dm.client.RestDmImageClient;
import cn.dm.client.RestDmItemClient;
import cn.dm.client.RestDmItemTypeClient;
import cn.dm.common.*;
import cn.dm.exception.ItemErrorCode;
import cn.dm.pojo.DmImage;
import cn.dm.pojo.DmItem;
import cn.dm.pojo.DmItemType;
import cn.dm.service.HomeService;
import cn.dm.vo.DmFloorItems;
import cn.dm.vo.DmItemTypeVo;
import cn.dm.vo.DmItemVo;
import cn.dm.vo.HotItemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HomeServiceImpl implements HomeService {

    @Autowired
    private RestDmItemTypeClient restDmItemTypeClient;

    @Autowired
    private RestDmItemClient restDmItemClient;

    @Autowired
    private RestDmImageClient restDmImageClient;

    @Autowired
    private RedisUtils redisUtils;

    /***
     * 查询所有节目类型和热门节目
     * @return
     */
    @Override
    public List<DmItemTypeVo> queryAllItemType() throws Exception {
        List<DmItemTypeVo> dmItemTypeVos = restDmItemTypeClient.selectTestChildren();
        if (EmptyUtils.isNotEmpty(dmItemTypeVos)) {
            for (DmItemTypeVo dmItemTypeVo : dmItemTypeVos) {
                List<HotItemVo> hotItemVoList = dmItemTypeVo.getHotItems();
                if (EmptyUtils.isNotEmpty(hotItemVoList)) {
                    for (HotItemVo dmItem : hotItemVoList) {
                        //查询图片信息
                        String imgUrl = getImageUrl(restDmImageClient, dmItem.getId(), Constants.Image.ImageType.normal, Constants.Image.ImageCategory.item);
                        dmItem.setImgUrl(imgUrl);
                    }
                }
            }
        }
        return dmItemTypeVos;
    }

    /**
     * 查询图片信息
     *
     * @param restDmImageClient
     * @param type
     * @param category
     * @return
     */
    public String getImageUrl(RestDmImageClient restDmImageClient, Long id, Integer type, Integer category) {
        //查询图片信息
        List<DmImage> dmImageList = null;
        //从reidis中获取图片信息
        String key = Constants.IMAGE_TOKEN_PREFIX + id + "_" + type + "_" + category;
        String imgUrl = (String) redisUtils.get(key);
        //如果redis中没有,则到数据库中获取
        if (EmptyUtils.isEmpty(imgUrl)) {
            dmImageList = restDmImageClient.queryDmImageList(id, type, category);
            imgUrl = dmImageList.get(0).getImgUrl();
            //同步图片到redis
            redisUtils.set(key, imgUrl);
        }
        return imgUrl;
    }

    /***
     * 查询横向导航条
     * @return
     * @throws Exception
     */
    @Override
    public List<DmItemType> queryTransverse() throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("level", "1");
        return restDmItemTypeClient.getDmItemTypeListByMap(param);
    }

    /***
     * 查询导航条
     * @return
     * @throws Exception
     */
    @Override
    public List<HotItemVo> queryBanner() throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("isBanner", 1);
        param.put("beginPos", 0);
        param.put("pageSize", 5);
        List<DmItem> dmItemList = restDmItemClient.getDmItemListByMap(param);
        List<HotItemVo> hotItemVoList = new ArrayList<HotItemVo>();
        if (EmptyUtils.isEmpty(dmItemList)) {
            return null;
        }

        for (DmItem dmItem : dmItemList) {
            HotItemVo hotItemVo = new HotItemVo();
            BeanUtils.copyProperties(dmItem, hotItemVo);
            String imgUrl = getImageUrl(restDmImageClient, dmItem.getId(), Constants.Image.ImageType.carousel, Constants.Image.ImageCategory.item);
            hotItemVo.setImgUrl(imgUrl);
            hotItemVoList.add(hotItemVo);
        }

        return hotItemVoList;
    }

    /***
     * 查询今日推荐接口queryToSaleItem
     * @return
     * @throws Exception
     */
    public List<HotItemVo> queryToSaleItem() throws Exception {
        //1.获取当前日期 输出 YYYY-MM-DD
        String now = DateUtil.format(new Date(), "yyyy-MM-dd  HH:mm:ss");
        String tomorrow = DateUtil.format(DateUtil.addDay(new Date(), 1), "yyyy-MM-dd  HH:mm:ss");
        //2.查询大于当前时间但小于24小时的节目
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("minTime", now);
        param.put("maxTime", tomorrow);
        param.put("beginPos", 0);
        param.put("pageSize", 6);
        List<DmItem> dmItemList = restDmItemClient.getDmItemListByMap(param);
        List<HotItemVo> hotItemVoList = new ArrayList<HotItemVo>();
        if (EmptyUtils.isEmpty(dmItemList)) {
            return null;
        }
        for (DmItem dmItem : dmItemList) {
            HotItemVo hotItemVo = new HotItemVo();
            BeanUtils.copyProperties(dmItem, hotItemVo);
            String imgUrl = getImageUrl(restDmImageClient, dmItem.getId(), Constants.Image.ImageType.normal, Constants.Image.ImageCategory.item);
            hotItemVo.setImgUrl(imgUrl);
            hotItemVoList.add(hotItemVo);
        }
        return hotItemVoList;
    }

    /***
     * 查询当日推荐节目
     * @return
     * @throws Exception
     */
    @Override
    public List<HotItemVo> queryTodayRecommend() throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("isRecommend", 1);
        param.put("beginPos", 0);
        param.put("pageSize", 6);
        List<DmItem> dmItemList = restDmItemClient.getDmItemListByMap(param);
        List<HotItemVo> hotItemVoList = new ArrayList<HotItemVo>();
        if (EmptyUtils.isEmpty(dmItemList)) {
            return null;
        }
        for (DmItem dmItem : dmItemList) {
            HotItemVo hotItemVo = new HotItemVo();
            BeanUtils.copyProperties(dmItem, hotItemVo);
            String imgUrl = getImageUrl(restDmImageClient, dmItem.getId(), Constants.Image.ImageType.normal, Constants.Image.ImageCategory.item);
            hotItemVo.setImgUrl(imgUrl);
            hotItemVoList.add(hotItemVo);
        }
        return hotItemVoList;
    }

    /***
     * 查询不同楼层的数据
     * @return
     * @throws Exception
     */
    @Override
    public List<DmFloorItems> queryFloorItems() throws Exception {
        List<DmFloorItems> dmFloorItemsList = restDmItemClient.queryItemByFloor();
        if (EmptyUtils.isEmpty(dmFloorItemsList)) {
            return null;
        }
        for (DmFloorItems dmFloorItems : dmFloorItemsList) {
            for (DmItemVo item : dmFloorItems.getItems()) {
                String imgUrl = getImageUrl(restDmImageClient, item.getId(), Constants.Image.ImageType.normal, Constants.Image.ImageCategory.item);
                item.setImgUrl(imgUrl);
            }
        }
        return dmFloorItemsList;
    }

    /****
     * 查询热门节目
     * @return
     * @throws Exception
     */
    @Override
    public List<HotItemVo> queryHotItems(Long itemTypeId) throws Exception {
        List<HotItemVo> hotItemVoList = new ArrayList<HotItemVo>();
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("sortDesc", "commentCount");
        param.put("itemType1Id", itemTypeId);
        param.put("beginPos", 0);
        param.put("pageSize", 10);
        List<DmItem> itemList = restDmItemClient.getDmItemListByMap(param);
        if (EmptyUtils.isEmpty(itemList)) {
            return null;
        }
        for (DmItem dmItem : itemList) {
            HotItemVo hotItemVo = new HotItemVo();
            BeanUtils.copyProperties(dmItem, hotItemVo);
            String imgUrl = getImageUrl(restDmImageClient, dmItem.getId(), Constants.Image.ImageType.normal, Constants.Image.ImageCategory.item);
            hotItemVo.setImgUrl(imgUrl);
            hotItemVoList.add(hotItemVo);
        }
        return hotItemVoList;
    }

    public void addItem(DmItem dmItem) {
        try {
            restDmItemClient.qdtxAddDmItem(dmItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
