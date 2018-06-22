package cn.dm.service.impl;

import cn.dm.client.*;
import cn.dm.common.*;
import cn.dm.exception.ItemErrorCode;
import cn.dm.pojo.*;
import cn.dm.service.ItemDetailService;
import cn.dm.vo.ItemCommentVo;
import cn.dm.vo.ItemDetailVo;
import cn.dm.vo.ItemPriceVo;
import cn.dm.vo.ItemSchedulerVo;
import org.apache.activemq.kaha.impl.data.Item;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

@Component
public class ItemDetailServiceImpl implements ItemDetailService {

    @Autowired
    private RestDmItemClient restDmItemClient;

    @Autowired
    private RestDmImageClient restDmImageClient;

    @Autowired
    private RestDmCinemaClient restDmCinemaClient;

    @Autowired
    private RestDmSchedulerClient restDmSchedulerClient;

    @Autowired
    private RestDmSchedulerSeatPriceClient restDmSchedulerSeatPriceClient;

    @Autowired
    private RestDmItemCommentClient restDmItemCommentClient;

    @Autowired
    private RestDmUserClient dmUserClient;

    @Override
    public Dto<ItemDetailVo> queryItemDetail(Long id) throws Exception {
        //查询对应ID的商品
        DmItem dmItem = restDmItemClient.getDmItemById(id);
        if(EmptyUtils.isEmpty(dmItem)){
            return null;
        }
        //查询图片信息
        List<DmImage> dmImageList = getImageList(dmItem.getId(), 1, 1, false);
        //获取剧场信息
        DmCinema dmCinema = restDmCinemaClient.getDmCinemaById(dmItem.getCinemaId());
        //组装返回数据
        ItemDetailVo itemDetailVo = copyData(dmItem, dmCinema, dmImageList);
        return DtoUtil.returnDataSuccess(itemDetailVo);
    }

    @Override
    public Dto<List<ItemSchedulerVo>> queryItemScheduler(Long id) throws Exception {
        List<ItemSchedulerVo> resultList = new ArrayList<ItemSchedulerVo>();
        //查询对应ID的商品
        DmItem dmItem = restDmItemClient.getDmItemById(id);
        if(EmptyUtils.isEmpty(dmItem)){
            return null;
        }
        //查询对应的排期列表
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("itemId", dmItem.getId());
        List<DmScheduler> dmSchedulerList = restDmSchedulerClient.getDmSchedulerListByMap(param);
        if (EmptyUtils.isEmpty(dmSchedulerList)) {
            throw new BaseException(ItemErrorCode.ITEM_NO_DATA);
        }
        //组装返回数据
        for (int i = 0; i < dmSchedulerList.size(); i++) {
            ItemSchedulerVo itemSchedulerVo = new ItemSchedulerVo();
            BeanUtils.copyProperties(dmItem, itemSchedulerVo);
            BeanUtils.copyProperties(dmSchedulerList.get(i), itemSchedulerVo);
            itemSchedulerVo.setStartTime(DateUtil.format(dmItem.getStartTime()));
            itemSchedulerVo.setEndTime(DateUtil.format(dmItem.getEndTime()));
            resultList.add(itemSchedulerVo);
        }
        return DtoUtil.returnDataSuccess(resultList);
    }

    @Override
    public Dto<List<ItemPriceVo>> queryItemPrice(Long scheduleId) throws Exception {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("scheduleId", scheduleId);
        //根据排期ID获取排期价格信息
        List<DmSchedulerSeatPrice> dmSchedulerSeatPrices = restDmSchedulerSeatPriceClient.getDmSchedulerSeatPriceListByMap(paramMap);
        List<ItemPriceVo> itemPriceVoList = new ArrayList<ItemPriceVo>();
        if (EmptyUtils.isEmpty(dmSchedulerSeatPrices)) {
            return null;
        }
        for (DmSchedulerSeatPrice seatPrice : dmSchedulerSeatPrices) {
            ItemPriceVo itemPriceVo = new ItemPriceVo();
            BeanUtils.copyProperties(seatPrice, itemPriceVo);
            //先查询出所有是有效座位且没有被锁定的座位数量
            Map<String, Object> lockMap = new HashMap<String, Object>();
            lockMap.put("status", 1);
            int lockNum = restDmSchedulerClient.getDmSchedulerCountByMap(lockMap);
            //对应的区域如果预定状态的座位数量少于总数代表还有座位
            int isHaveSeat = lockNum > 0 ? 1 : 0;
            itemPriceVo.setIsHaveSeat(isHaveSeat);
            itemPriceVoList.add(itemPriceVo);
        }
        return DtoUtil.returnDataSuccess(itemPriceVoList);
    }

    @Override
    public Dto<List<ItemCommentVo>> queryItemComment(Long itemId) throws Exception {
        //根据商品ID获取所有的剧评
        Map<String, Object> paramMapComment = new HashMap<String, Object>();
        paramMapComment.put("itemId", itemId);
        List<ItemCommentVo> itemCommentVoList = new ArrayList<ItemCommentVo>();
        List<DmItemComment> dmCommentList = restDmItemCommentClient.getDmItemCommentListByMap(paramMapComment);
        if (EmptyUtils.isEmpty(dmCommentList)) {
            return DtoUtil.returnDataSuccess(itemCommentVoList);
        }
        //根据剧评中的用户ID获取用户头像并封装返回数据
        for (DmItemComment dmItemComment : dmCommentList) {
            ItemCommentVo itemCommentVo = new ItemCommentVo();
            BeanUtils.copyProperties(dmItemComment, itemCommentVo);
            //查询用户信息
            DmUser dmUser = dmUserClient.getDmUserById(dmItemComment.getUserId());
            if (EmptyUtils.isNotEmpty(dmUser)) {
                itemCommentVo.setUserName(dmUser.getNickName());
            }
            //获取用户头像
            List<DmImage> dmImageList = getImageList(dmItemComment.getUserId(), 0, 0, true);
            if (EmptyUtils.isNotEmpty(dmImageList)) {
                itemCommentVo.setImgUrl(EmptyUtils.isNotEmpty(dmImageList) ? dmImageList.get(0).getImgUrl() : ""
                );
            }
            itemCommentVo.setCreatedTime(DateUtil.format(dmItemComment.getCreatedTime()));
            itemCommentVoList.add(itemCommentVo);
        }
        return DtoUtil.returnDataSuccess(itemCommentVoList);
    }

    @Override
    public Dto commitItemComment(Long itemId, Long userId, Integer score, String comment) throws Exception {
        //封装评论对象
        DmItemComment dmItemComment = new DmItemComment();
        dmItemComment.setItemId(itemId);
        dmItemComment.setUserId(userId);
        dmItemComment.setScore(score);
        dmItemComment.setContent(comment);
        dmItemComment.setCreatedTime(new Date());
        //添加剧评
        restDmItemCommentClient.qdtxAddDmItemComment(dmItemComment);
        return DtoUtil.returnDataSuccess(null);
    }

    /**
     * 获取图片信息
     *
     * @param targetId
     * @param type
     * @param category     0:用户头像 1:商品图片
     * @param checkUserImg 是否是查询用户头像
     * @return
     * @throws Exception
     */
    public List<DmImage> getImageList(Long targetId, Integer type, Integer category, boolean checkUserImg) throws Exception {
        Map<String, Object> paramMapImage = new HashMap<String, Object>();
        paramMapImage.put("targetId", targetId);
        if (!checkUserImg) {
            paramMapImage.put("type", type);
        }
        //图片类型为1，代表查询商品图片
        paramMapImage.put("category", category);
        return restDmImageClient.getDmImageListByMap(paramMapImage);
    }

    /**
     * 统一封装VO格式返回数据
     *
     * @param dmItem
     * @param dmCinema
     * @param dmImageList
     * @return
     */
    public ItemDetailVo copyData(DmItem dmItem, DmCinema dmCinema, List<DmImage> dmImageList) throws ParseException {
        ItemDetailVo itemDetailVo = new ItemDetailVo();
        BeanUtils.copyProperties(dmItem, itemDetailVo);
        itemDetailVo.setStartTime(DateUtil.format(dmItem.getStartTime()));
        itemDetailVo.setEndTime(DateUtil.format(dmItem.getEndTime()));
        itemDetailVo.setState(dmItem.getState() + "");
        itemDetailVo.setImgUrl(EmptyUtils.isNotEmpty(dmImageList) ? dmImageList.get(0).getImgUrl() : "");
        if (EmptyUtils.isNotEmpty(dmCinema)) {
            BeanUtils.copyProperties(dmCinema, itemDetailVo);
        }
        itemDetailVo.setId(dmItem.getId());
        itemDetailVo.setCommentCount(dmItem.getCommentCount());
        return itemDetailVo;
    }
}
