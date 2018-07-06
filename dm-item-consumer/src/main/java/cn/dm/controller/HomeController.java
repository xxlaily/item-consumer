package cn.dm.controller;
import cn.dm.common.BaseException;
import cn.dm.common.DateUtil;
import cn.dm.common.Dto;
import cn.dm.common.DtoUtil;
import cn.dm.pojo.DmItem;
import cn.dm.pojo.DmItemType;
import cn.dm.service.HomeService;
import cn.dm.vo.DmFloorItems;
import cn.dm.vo.DmItemTypeVo;
import cn.dm.vo.HotItemVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.*;

/***
 * 主页Controller
 */
@RestController
@RequestMapping("/api/p/index")
public class HomeController {

    @Resource
    private HomeService homeService;
    /***
     * 查询所有节目类型
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryAllType",method = RequestMethod.POST)
    public Dto queryAllType() throws Exception {
      List<DmItemTypeVo> dmItemTypeVos=homeService.queryAllItemType();
      return DtoUtil.returnDataSuccess(dmItemTypeVos);
    }
    /***
     * 查询横向导航条
     * @return
     */
    @RequestMapping(value = "/queryTransverse",method = RequestMethod.POST)
    public Dto queryTransverse() throws Exception {
        List<DmItemType> dmItemTypeList=homeService.queryTransverse();
        return DtoUtil.returnDataSuccess(dmItemTypeList);
    }
    /***
     * 查询首页轮播图
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryBanner",method = RequestMethod.POST)
    public Dto queryBanner()throws Exception{
        List<HotItemVo> hotItemVoList=homeService.queryBanner();
        return DtoUtil.returnDataSuccess(hotItemVoList);
    }
    /***
     * 查询今日推荐
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryTodayRecommend",method = RequestMethod.POST)
    public Dto queryTodayRecommend()throws Exception{
        List<HotItemVo> hotItemVoList=homeService.queryTodayRecommend();
        return DtoUtil.returnDataSuccess(hotItemVoList);
    }
    /***
     * 查询即将开售接口
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryToSaleItem",method = RequestMethod.POST)
    public Dto queryToSaleItem()throws Exception{
        List<HotItemVo> hotItemVoList=homeService.queryToSaleItem();
        return DtoUtil.returnDataSuccess(hotItemVoList);
    }
    /***
     * 查询不同楼层的数据
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryFloorItems",method = RequestMethod.POST)
    public Dto queryFloorItems()throws Exception{
        List<DmFloorItems> dmFloorItems=homeService.queryFloorItems();
        return DtoUtil.returnDataSuccess(dmFloorItems);
    }
    /***
     * 查询热门节目
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryHotItems",method = RequestMethod.POST)
    public Dto queryHotItems(@RequestBody Map<String,Long> params)throws Exception{
        Long itemTypeId=params.get("itemTypeId");
        List<HotItemVo> hotItemVoList=homeService.queryHotItems(itemTypeId);
       return DtoUtil.returnDataSuccess(hotItemVoList);
    }

//    @RequestMapping(value = "/crawler",method = RequestMethod.GET)
//    public void crawler(){
//        String url="https://www.damai.cn/alltickets.html?spm=a2o6e.home.0.0.46e148d3ilEf57";
//        try {
//            Random rand = new Random();
//            Document document=Jsoup.connect(url).get();
//            Elements elements=document.select("div[class=item mt20]>table>tbody>tr>td[class=name]>a");
//            for (Element element:elements){
//                String dUrl=element.absUrl("href");
//                Document dDocument=Jsoup.connect(dUrl).get();
//                DmItem dmItem=new DmItem();
//                dmItem.setItemName(dDocument.select("h2[class=tt]>span[class=txt]").text());
//                dmItem.setAbstractMessage(dDocument.select("div[class=m-goods]>h3[class=stt]>span[class=txt]").text());
//                dmItem.setBasicDescription(dDocument.select("div[class=table-info]").get(0).outerHtml());
//                dmItem.setReminderDescription(dDocument.select("div[class=table-info]").get(1).outerHtml());
//                dmItem.setProjectDescription(dDocument.select("div[class=pre]").get(0).outerHtml());
//                dmItem.setLongitude("116.38");
//                dmItem.setLatitude("39.90");
//
//                Calendar calendar   =   new GregorianCalendar();
//                calendar.setTime(new Date());
//                calendar.add(calendar.DATE,rand.nextInt(300));
//
//                dmItem.setStartTime(calendar.getTime());
//
//                calendar.add(calendar.DATE,rand.nextInt(300));
//                dmItem.setEndTime(calendar.getTime());
//
//                dmItem.setMinPrice(new Long(rand.nextInt(300) + 100)*1.0);
//                dmItem.setMaxPrice(dmItem.getMinPrice()+new Long(rand.nextInt(1000) + 100));
//
//                dmItem.setState(rand.nextInt(4) + 1);
//                dmItem.setAgeGroup(rand.nextInt(4) + 0);
//                dmItem.setIsBanner(rand.nextInt(1) + 0);
//                dmItem.setIsRecommend(rand.nextInt(1) + 0);
//
//                dmItem.setItemType1Id(new Long(rand.nextInt(9) + 1));
//                if(dmItem.getItemType1Id()==1){
//                    dmItem.setItemType2Id(new Long(rand.nextInt(4) + 10));
//                }else if(dmItem.getItemType1Id()==2){
//                    dmItem.setItemType2Id(new Long(rand.nextInt(3) + 14));
//                }else if(dmItem.getItemType1Id()==3){
//                    dmItem.setItemType2Id(new Long(rand.nextInt(5) + 17));
//                }else if(dmItem.getItemType1Id()==4){
//                    dmItem.setItemType2Id(new Long(rand.nextInt(5) + 22));
//                }else if(dmItem.getItemType1Id()==5){
//                    dmItem.setItemType2Id(new Long(rand.nextInt(3) + 27));
//                }else if(dmItem.getItemType1Id()==6){
//                    dmItem.setItemType2Id(new Long(rand.nextInt(3) + 30));
//                }else if(dmItem.getItemType1Id()==7){
//                    dmItem.setItemType2Id(new Long(rand.nextInt(5) + 33));
//                }
//                homeService.addItem(dmItem);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    @RequestMapping(value = "/test",method = RequestMethod.GET)
//    public String test(){
//        String fileEncode = System.getProperty("file.encoding");
//        System.out.println("fileEncode1:"+fileEncode);
//        System.setProperty("file.encoding","UTF-8");
//        fileEncode = System.getProperty("file.encoding");
//        System.out.println("fileEncode2:"+fileEncode);
//        String message="你猜我是谁哈";
//        System.out.println(message);
//        return message;
//    }
}
