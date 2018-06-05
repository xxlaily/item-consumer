package cn.dm.service;

import cn.dm.common.Dto;
import cn.dm.vo.ItemCommentVo;
import cn.dm.vo.ItemDetailVo;
import cn.dm.vo.ItemPriceVo;
import cn.dm.vo.ItemSchedulerVo;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 商品详情服务接口
 */
public interface ItemDetailService {

    /**
     * 根据商品id返回商品详情页
     *
     * @param id
     * @return
     */
    public Dto<ItemDetailVo> queryItemDetail(Long id) throws Exception;


    /**
     * 根据商品ID查询商品排期
     *
     * @param id
     * @return
     */
    public Dto<List<ItemSchedulerVo>> queryItemScheduler(Long id) throws Exception;

    /**
     * 根据商品排期查询商品价格
     *
     * @param scheduleId
     * @return
     */
    public Dto<List<ItemPriceVo>> queryItemPrice(Long scheduleId) throws Exception;

    /**
     * 根据商品Id查询剧评
     *
     * @param
     * @return
     */
    public Dto<List<ItemCommentVo>> queryItemComment(Long id) throws Exception;

    /**
     * 添加剧评
     *
     * @param itemId
     * @param userId
     * @param score
     * @param comment
     * @return
     * @throws Exception
     */
    public Dto commitItemComment(@RequestParam("itemId") Long itemId,
                                 @RequestParam("userId") Long userId,
                                 @RequestParam("score") Integer score,
                                 @RequestParam("comment") String comment) throws Exception;
}
