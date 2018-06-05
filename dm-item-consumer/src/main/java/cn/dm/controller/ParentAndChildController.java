package cn.dm.controller;

import cn.dm.common.Dto;
import cn.dm.service.ParentAndChildService;
import cn.dm.vo.MonthVo;
import cn.dm.vo.ParentAndChildVo;
import cn.dm.vo.SlideShowVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 亲子首页Controller
 */
@RestController
@RequestMapping(value = "/api/")
public class ParentAndChildController {

    @Autowired
    private ParentAndChildService parentAndChildService;

    /**
     * 根据分类查询轮播图
     *
     * @param param - itemTypeId 商品分类主键
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/querySlideShowPic", method = RequestMethod.POST)
    public Dto<List<SlideShowVo>> querySlideShowPic(@RequestBody Map<String, Object> param, HttpServletResponse response) throws Exception {
        Integer itemTypeId = (Integer) param.get("itemTypeId");
        return parentAndChildService.querySlideShowPic((long) itemTypeId);
    }


    /**
     * 亲子首页 猜你喜欢接口 和 精彩聚焦接口 （复用）
     *
     * @param param-itemTypeId 商品分类主键
     * @param param-limit      请求数目
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryItemLikeOrNice", method = RequestMethod.POST)
    @ResponseBody
    public Dto<List<ParentAndChildVo>> queryItemLikeOrNice(@RequestBody Map<String, Object> param) throws Exception {
        return parentAndChildService.queryItem("itemType2Id", param.get("itemTypeId"), (Integer) param.get("limit"), 0, 1);
    }

    /**
     * 亲子首页 根据年龄段查询节目
     *
     * @param param-ageGroup 年龄段（0:全年龄段,1:0-3岁,2:3-6岁,3:6-12岁）
     * @param param-limit    请求数目
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryItemByAge", method = RequestMethod.POST)
    @ResponseBody
    public Dto<List<ParentAndChildVo>> queryItemByAge(@RequestBody Map<String, Object> param) throws Exception {
        return parentAndChildService.queryItem("ageGroup", param.get("ageGroup"), (Integer) param.get("limit"), 0, 1);
    }


    /**
     * 亲子首页 根据热门城市查询热门排行
     *
     * @param param-areaId 区域Id
     * @param param-limit  请求数目
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryItemHot", method = RequestMethod.POST)
    @ResponseBody
    public Dto<List<ParentAndChildVo>> queryItemHot(@RequestBody Map<String, Object> param) throws Exception {
        Integer areaId = (Integer) param.get("areaId");
        return parentAndChildService.queryItemHot((long) areaId, (Integer) param.get("limit"));
    }


    /**
     * 亲子首页 根据月份查询演出
     *
     * @param param-month 月份(1,2,3,4.....)
     * @param param-year  年份(2018)
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryItemByMonth", method = RequestMethod.POST)
    @ResponseBody
    public Dto<List<MonthVo>> queryItemByMonth(@RequestBody Map<String, Object> param) throws Exception {
        return parentAndChildService.queryItemByMonth((Integer) param.get("month"), (Integer) param.get("year"));
    }

    /**
     * 亲子首页 广告接口 图片为背景透明
     *
     * @param param-itemTypeId 商品分类主键
     * @param param-limit      请求数目
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryAdvertising", method = RequestMethod.POST)
    @ResponseBody
    public Dto<List<ParentAndChildVo>> queryAdvertising(@RequestBody Map<String, Object> param) throws Exception {
        Integer itemTypeId = (Integer) param.get("itemTypeId");
        return parentAndChildService.queryItem("itemType2Id", (long) itemTypeId, (Integer) param.get("limit"), 0, 0);
    }


}
