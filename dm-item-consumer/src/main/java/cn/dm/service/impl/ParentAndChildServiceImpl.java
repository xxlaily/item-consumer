package cn.dm.service.impl;

import cn.dm.client.RestDmCinemaClient;
import cn.dm.client.RestDmImageClient;
import cn.dm.client.RestDmItemClient;
import cn.dm.common.*;
import cn.dm.exception.ItemErrorCode;
import cn.dm.pojo.DmCinema;
import cn.dm.pojo.DmImage;
import cn.dm.pojo.DmItem;
import cn.dm.service.ParentAndChildService;
import cn.dm.vo.MonthVo;
import cn.dm.vo.ParentAndChildVo;
import cn.dm.vo.SlideShowVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

/**
 * 亲子页服务接口实现
 */
@Component
public class ParentAndChildServiceImpl implements ParentAndChildService {
    private static final Logger logger = LoggerFactory.getLogger(ParentAndChildServiceImpl.class);
    @Autowired
    private RestDmItemClient restDmItemClient;

    @Autowired
    private RestDmImageClient restDmImageClient;

    @Autowired
    private RestDmCinemaClient restDmCinemaClient;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Dto<List<SlideShowVo>> querySlideShowPic(Long itemTypeId) throws Exception {
        SlideShowVo slideShowVo = null;
        Dto<List<SlideShowVo>> dto = null;
        List<DmItem> dmItemList = getItemList("itemType1Id", itemTypeId, 1, 5);
        List<SlideShowVo> dataList = new ArrayList<SlideShowVo>();
        for (DmItem item : dmItemList) {
            logger.info("[querySlideShowPic]" + "dmItemList size>>>>>>>>>>>>>>>"+dmItemList.size());
            //获取对应的轮播图信息
            String imgUrl = getImageUrl(restDmImageClient, item.getId(), Constants.Image.ImageType.carousel, Constants.Image.ImageCategory.item);
            //封装返回数据
            slideShowVo = new SlideShowVo();
            BeanUtils.copyProperties(item, slideShowVo);
            slideShowVo.setImgUrl(imgUrl);
            dataList.add(slideShowVo);
        }
        dto = DtoUtil.returnDataSuccess(dataList);
        return dto;
    }

    @Override
    public Dto<List<ParentAndChildVo>> queryItem(String paramName, Object paramValue, Integer limit, Integer isRecommend, Integer type) throws Exception {
        ParentAndChildVo parentAndChildVo = null;
        //根据分类查询对应的节目信息
        List<DmItem> dmItemList = getItemList(paramName, paramValue, isRecommend, limit);
        List<ParentAndChildVo> dataList = new ArrayList<ParentAndChildVo>();
        Map<String, Object> paramMapCinema = new HashMap<String, Object>();
        for (DmItem item : dmItemList) {
            //获取对应的轮播图信息
            String imgUrl = getImageUrl(restDmImageClient, item.getId(), type, Constants.Image.ImageCategory.item);
            //获取剧场信息
            paramMapCinema.put("cinemaId", item.getCinemaId());
            DmCinema dmCinema = restDmCinemaClient.getDmCinemaById(item.getCinemaId());
            //封装返回数据
            parentAndChildVo = copyData(item, dmCinema, imgUrl);
            dataList.add(parentAndChildVo);
        }
        return DtoUtil.returnDataSuccess(dataList);
    }

    @Override
    public Dto<List<ParentAndChildVo>> queryItemHot(Long areaId, Integer limit) throws Exception {
        List<DmCinema> cinemaList = getCinemaList(areaId);
        //根据剧场查询热门节目信息，根据评论数来判定是否热门
        List<Long> cinemaIdList = new ArrayList<Long>();
        if (EmptyUtils.isEmpty(cinemaList)) {
            return null;
        }
        for (DmCinema dmCinema : cinemaList) {
            cinemaIdList.add(dmCinema.getId());
        }
        //在指定剧场中查询，并限定指定数量
        Map<String, Object> paramMapHotItem = new HashMap<String, Object>();
        paramMapHotItem.put("list", cinemaIdList);
        paramMapHotItem.put("pageSize", limit);
        paramMapHotItem.put("beginPos", 0);
        List<DmItem> dmItemList = restDmItemClient.getHotDmItemList(paramMapHotItem);
        List<ParentAndChildVo> dataList = new ArrayList<ParentAndChildVo>();
        if (EmptyUtils.isEmpty(dmItemList)) {
            return null;
        }
        for (DmItem item : dmItemList) {
            //获取对应的轮播图信息
            String imgUrl = getImageUrl(restDmImageClient, item.getId(), Constants.Image.ImageType.normal, Constants.Image.ImageCategory.item);
            //获取剧场信息
            DmCinema dmCinema = getCinemaById(cinemaList, item.getCinemaId());
            //封装返回数据
            dataList.add(copyData(item, dmCinema, imgUrl));
        }
        return DtoUtil.returnDataSuccess(dataList);
    }

    @Override
    public Dto<List<MonthVo>> queryItemByMonth(Integer month, Integer year, Long itemTypeId) throws Exception {
        Map<String, Object> parmas = new HashMap<String, Object>();
        parmas.put("month", month);
        parmas.put("year", year);
        parmas.put("itemType1Id", itemTypeId);
        List<DmItem> dmItemList = restDmItemClient.queryItemByMonth(parmas);
        List<MonthVo> monthVoList = new ArrayList<MonthVo>();
        if (EmptyUtils.isEmpty(dmItemList)) {
            return DtoUtil.returnDataSuccess(monthVoList);
        }
        //组装每月31天的数据
        Calendar c = Calendar.getInstance();
        for (int i = 1; i <= 31; i++) {
            MonthVo monthVo = new MonthVo();
            List<ParentAndChildVo> parentAndChildVoList = new ArrayList<ParentAndChildVo>();
            //组装满足当前日期（指定天）的数据
            for (DmItem dmItem : dmItemList) {
                c.setTime(dmItem.getStartTime());
                if (c.get(Calendar.DAY_OF_MONTH) == i) {
                    //获取对应的轮播图信息
                    String imgUrl = getImageUrl(restDmImageClient, dmItem.getId(), Constants.Image.ImageType.normal, Constants.Image.ImageCategory.item);
                    //获取剧场信息
                    DmCinema dmCinema = restDmCinemaClient.getDmCinemaById(dmItem.getCinemaId());
                    //组装返回数据
                    parentAndChildVoList.add(copyData(dmItem, dmCinema, imgUrl));
                    monthVo.setDay(DateUtil.format(dmItem.getStartTime()));
                }
            }
            monthVo.setItemList(parentAndChildVoList);
            if (EmptyUtils.isEmpty(parentAndChildVoList)) {
                //如果某天没有节目，则跳过不组装数据
                continue;
            }
            monthVoList.add(monthVo);
        }
        return DtoUtil.returnDataSuccess(monthVoList);
    }

    /**
     * 获取节目列表信息
     *
     * @param paramName   过滤参数名称
     * @param paramValue  参数值
     * @param isRecommend 是否是热门推荐
     * @param limit       请求数目
     * @return
     * @throws Exception
     */
    public List<DmItem> getItemList(String paramName, Object paramValue, Integer isRecommend, Integer limit) throws Exception {
        Map<String, Object> paramMapItem = new HashMap<String, Object>();
        paramMapItem.put(paramName, paramValue);
        paramMapItem.put("isRecommend", isRecommend);
        if (limit != 0) {
            paramMapItem.put("pageSize", limit);
            paramMapItem.put("beginPos", 0);
        }
        //根据分类查询对应的节目信息
        List<DmItem> dmItemList = restDmItemClient.getDmItemListByMap(paramMapItem);
        if (EmptyUtils.isEmpty(dmItemList)) {
            throw new BaseException(ItemErrorCode.ITEM_NO_DATA);
        }
        return dmItemList;
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

    /**
     * 根据ID获取Cinema
     *
     * @param dmCinemaList
     * @param id
     * @return
     */
    public DmCinema getCinemaById(List<DmCinema> dmCinemaList, Long id) {
        DmCinema dmCinema = null;
        for (DmCinema item : dmCinemaList) {
            if (item.getId() == id) {
                dmCinema = item;
                break;
            }
        }
        return dmCinema;
    }

    /**
     * 根据热门区域Id查询符合条件的剧场
     *
     * @return
     */
    public List<DmCinema> getCinemaList(Long areaId) throws Exception {
        Map<String, Object> paramMapItem = new HashMap<String, Object>();
        paramMapItem.put("areaId", areaId);
        List<DmCinema> cinemaList = restDmCinemaClient.getDmCinemaListByMap(paramMapItem);
        if (EmptyUtils.isEmpty(cinemaList)) {
            throw new BaseException(ItemErrorCode.ITEM_NO_DATA);
        }
        return cinemaList;
    }

    /**
     * 统一封装VO格式返回数据
     *
     * @param dmItem
     * @param dmCinema
     * @param imgUrl
     * @return
     */
    public ParentAndChildVo copyData(DmItem dmItem, DmCinema dmCinema, String imgUrl) throws ParseException {
        ParentAndChildVo parentAndChildVo = new ParentAndChildVo();
        BeanUtils.copyProperties(dmCinema, parentAndChildVo);
        BeanUtils.copyProperties(dmItem, parentAndChildVo);
        parentAndChildVo.setStartDate(DateUtil.format(dmItem.getStartTime()));
        parentAndChildVo.setEndDate(DateUtil.format(dmItem.getEndTime()));
        parentAndChildVo.setImgUrl(imgUrl);
        return parentAndChildVo;
    }
}
