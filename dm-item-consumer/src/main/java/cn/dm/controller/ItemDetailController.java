package cn.dm.controller;

import cn.dm.common.Constants;
import cn.dm.common.Dto;
import cn.dm.service.ItemDetailService;
import cn.dm.service.ParentAndChildService;
import cn.dm.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品详情页Controller
 */
@RestController
@RequestMapping(value = "/api")
public class ItemDetailController {

    @Autowired
    private ItemDetailService itemDetailService;

    @Autowired
    private ParentAndChildService parentAndChildService;

    /**
     * 根据商品id返回商品详情页
     *
     * @param param-id 商品Id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/p/queryItemDetail",method = RequestMethod.POST)
    @ResponseBody
    public Dto<ItemDetailVo> queryItemDetail(@RequestBody Map<String, Object> param) throws Exception {
        Integer id=Integer.parseInt(param.get("id").toString());
        return itemDetailService.queryItemDetail((long) id);
    }


    /**
     * 根据商品ID查询商品排期
     *
     * @param param-id 商品Id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/p/queryItemScheduler",method = RequestMethod.POST)
    @ResponseBody
    public Dto<List<ItemSchedulerVo>> queryItemScheduler(@RequestBody Map<String, Object> param) throws Exception {
        Integer id = Integer.parseInt(param.get("itemId").toString());
        return itemDetailService.queryItemScheduler((long) id);
    }


    /**
     * 根据商品排期查询商品价格
     *
     * @param param-scheduleId 商品排期ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/p/queryItemPrice",method = RequestMethod.POST)
    @ResponseBody
    public Dto<List<ItemPriceVo>> queryItemPrice(@RequestBody Map<String, Object> param) throws Exception {
        Integer id = Integer.parseInt(param.get("scheduleId").toString());
        return itemDetailService.queryItemPrice((long) id);
    }


    /**
     * 根据商品Id查询剧评
     *
     * @param param-id 商品ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/p/queryItemComment",method = RequestMethod.POST)
    @ResponseBody
    public Dto<List<ItemCommentVo>> queryItemComment(@RequestBody Map<String, Object> param) throws Exception {
        Integer id = Integer.parseInt(param.get("id").toString());
        return itemDetailService.queryItemComment((long) id);
    }

    /**
     * 推荐接口
     *
     * @param param-itemTypeId 商品分类主键
     * @param param-limit      请求数目
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/p/queryItemRecommend",method = RequestMethod.POST)
    @ResponseBody
    public Dto<List<ParentAndChildVo>> queryItemRecommend(@RequestBody Map<String, Object> param) throws Exception {
        return parentAndChildService.queryItem("itemType2Id", param.get("itemTypeId"), (Integer) param.get("limit"), 1, Constants.Image.ImageType.carousel);
    }

    /**
     * 添加剧评
     *
     * @param param-itemId
     * @param param-userId
     * @param param-score
     * @param param-comment
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/v/commitItemComment",method = RequestMethod.POST)
    @ResponseBody
    public Dto commitItemComment(@RequestBody Map<String, Object> param) throws Exception {
        Integer itemId = Integer.parseInt(param.get("itemId").toString());
        Integer userId = Integer.parseInt(param.get("userId").toString());
        return itemDetailService.commitItemComment((long) itemId, (long) userId, (Integer) param.get("score"), (String) param.get("comment"));
    }
}
