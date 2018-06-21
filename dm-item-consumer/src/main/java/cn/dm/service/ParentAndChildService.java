package cn.dm.service;

import cn.dm.common.Dto;
import cn.dm.vo.MonthVo;
import cn.dm.vo.ParentAndChildVo;
import cn.dm.vo.SlideShowVo;

import java.util.List;

/**
 * 亲子页服务接口
 */
public interface ParentAndChildService {

    /**
     * 根据分类查询轮播图
     *
     * @param itemTypeId
     * @return
     */
    public Dto<List<SlideShowVo>> querySlideShowPic(Long itemTypeId) throws Exception;

    /**
     * 猜你喜欢接口/精彩聚焦接口/根据年龄段查询节目
     *
     * @param paramName   查询数据库字段名称
     * @param paramValue  字段值
     * @param isRecommend 是否是推荐 0:不推荐；1：推荐
     * @param type        需要的图片类型
     * @param limit
     * @return
     * @throws Exception
     */
    public Dto<List<ParentAndChildVo>> queryItem(String paramName, Object paramValue, Integer limit, Integer isRecommend, Integer type) throws Exception;


    /**
     * 根据热门城市查询热门排行
     *
     * @param areaId 区域Id
     * @param limit
     * @return
     * @throws Exception
     */
    public Dto<List<ParentAndChildVo>> queryItemHot(Long areaId, Integer limit) throws Exception;


    /**
     * 根据月份查询演出
     *
     * @param month 月份(1,2,3,4.....)
     * @param year  年份(2018)
     * @param itemTypeId
     * @return
     * @throws Exception
     */
    public Dto<List<MonthVo>> queryItemByMonth(Integer month, Integer year,Long itemTypeId) throws Exception;

}
