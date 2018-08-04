package com.cqu.wb.controller;

import com.cqu.wb.domain.User;
import com.cqu.wb.redis.GoodsKey;
import com.cqu.wb.redis.RedisService;
import com.cqu.wb.result.Result;
import com.cqu.wb.service.GoodsService;
import com.cqu.wb.service.UserService;
import com.cqu.wb.vo.GoodsDetailVo;
import com.cqu.wb.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by jingquan on 2018/7/30.
 */
@Controller
@RequestMapping(value = "/goods")
public class GoodsController {

    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     *
     * @param response
     * @param model
     * @param cookieToken
     * @param paramToken
     * @return
     * @description 从分布式Session中获取用户信息，并跳转到商品列表页面
     *              由于每个页面都需要User的session信息，此方式获取Session用户信息代码冗余，推荐采用如下请求参数绑定的方式保存User信息
     */
    @RequestMapping(value = "/to_list_v1", method = RequestMethod.GET)
    public String listGoodsV1(HttpServletResponse response, Model model,
                               @CookieValue(value = UserService.TOKEN_NAME, required = false)String cookieToken,
                               @RequestParam(value = UserService.TOKEN_NAME, required = false)String paramToken) {
        // 若用户信息失效则返回登录页面
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return "login";
        }

        // 从请求参数（为兼容手机端）或者Cookie中获取用户Token，优先考虑从请求参数中获取，若无再考虑Cookie中获取
        String token = null;
        if(StringUtils.isEmpty(paramToken)) {
            token = cookieToken;
        } else {
            token = paramToken;
        }
        // 根据Token从Redis缓存中获取用户信息
        User user = userService.getUserByToken(response, token);
        model.addAttribute("user", user);

        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        model.addAttribute("goodsVoList", goodsVoList);

        return "goods_list";
    }
    /**
     *
     * @param model
     * @param user
     * @return
     * @description 采用请求参数解析绑定的方式，从分布式Session中获取用户信息并绑定到方法入参中，然后跳转到商品列表页面
     */
    @RequestMapping(value = "/to_list_v2", method = RequestMethod.GET)
    public String listGoodsV2(Model model, User user) {
        model.addAttribute("user", user);

        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        model.addAttribute("goodsVoList", goodsVoList);

        return "goods_list";
    }
    /**
     *
     * @param request
     * @param response
     * @param model
     * @param user
     * @return
     * @description 商品列表页请求不含参数（即不随其他因素而改变），采用页面级缓存优化：返回页面先从缓存中去取，缓存未命中则手动
     *              渲染页面，再添加进缓存中，最后直接返回html页面
     *
     * 并发量：5000 * 10
     * 优化前：QPS: 1267 load: 15 mysql
     * 页面缓存优化后：QPS: 2884, load: 5
     */
    @RequestMapping(value = "/to_list_v3", method = RequestMethod.GET, produces = "text/html")
    @ResponseBody
    public String listGoodsV3(HttpServletRequest request, HttpServletResponse response, Model model, User user) {
        model.addAttribute("user", user);

        // 取页面缓存（由于页面不随其他因素而改变，因此key可以为""）
        String html = redisService.get(GoodsKey.goodsListHtmlGoodsKey, "", String.class);
        if(!StringUtils.isEmpty(html)) {
            return html;
        }

        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        model.addAttribute("goodsVoList", goodsVoList);

        // 缓存未命中则进行手动渲染html页面，并进行缓存
        SpringWebContext springWebContext = new SpringWebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", springWebContext);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.goodsListHtmlGoodsKey, "", html);
        }

        return html;
    }


    /**
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     * @description 获取秒杀相关信息并跳转到对应的商品详情页
     */
    @RequestMapping(value = "/to_detail_v1/{goodsId}", method = RequestMethod.GET)
    public String getGoodsDetailV1(Model model, User user, @PathVariable("goodsId") long goodsId) {
        model.addAttribute("user", user);

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goodsVo", goodsVo);

        long startTime = goodsVo.getStartDate().getTime();
        long endTime = goodsVo.getEndDate().getTime();
        long currentTime = System.currentTimeMillis();

        int seckillStatus = 0;
        int remainSeconds = 0;
        if(currentTime < startTime) {       // 秒杀还没开始，需要进行倒计时
            seckillStatus = 0;
            remainSeconds = (int)((startTime - currentTime) / 1000);
        } else if(currentTime > endTime) {  // 秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        } else {                            // 秒杀进行时
            seckillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("seckillStatus", seckillStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        return "goods_detail";
    }
    /**
     *
     * @param request
     * @param response
     * @param model
     * @param user
     * @param goodsId
     * @return
     * @description 商品详情页请求含参数（即会随其他因素而改变），采用URL级缓存优化：返回页面先从缓存中去取，缓存未命中则手动
     *              渲染页面，再添加进缓存中，最后直接返回html页面
     */
    @RequestMapping(value = "/to_detail_v2/{goodsId}", method = RequestMethod.GET, produces = "text/html")
    @ResponseBody
    public String getGoodsDetailV1(HttpServletRequest request, HttpServletResponse response,
                                   Model model, User user, @PathVariable("goodsId") long goodsId) {
        model.addAttribute("user", user);

        // 取URL缓存（由于页面会随其他因素【goodsId】而改变，因此key不可以为""，需要标志goodsId）
        String html = redisService.get(GoodsKey.goodsDetailHtmlGoodsKey, "" + goodsId, String.class);
        if(!StringUtils.isEmpty(html)) {
            return html;
        }

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goodsVo", goodsVo);

        long startTime = goodsVo.getStartDate().getTime();
        long endTime = goodsVo.getEndDate().getTime();
        long currentTime = System.currentTimeMillis();

        int seckillStatus = 0;
        int remainSeconds = 0;
        if(currentTime < startTime) {       // 秒杀还没开始，需要进行倒计时
            seckillStatus = 0;
            remainSeconds = (int)((startTime - currentTime) / 1000);
        } else if(currentTime > endTime) {  // 秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        } else {                            // 秒杀进行时
            seckillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("seckillStatus", seckillStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        // 缓存未命中则进行手动渲染html页面，并进行缓存
        SpringWebContext springWebContext = new SpringWebContext(request,response,
                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", springWebContext);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.goodsDetailHtmlGoodsKey, "" + goodsId, html);
        }

        return html;
    }

    /**
     *
     * @param user
     * @param goodsId
     * @return
     * @description 前后端分离／页面静态化：后端页面提供数据获取接口，前端页面完全采用静态html实现，采用ajax异步请求后端接口
     *              并获取数据，采用jQuery将数据填充页面。前后端分离／页面静态化将静态页面直接缓存在客户端浏览器上来进行优化。
     */
    @RequestMapping(value = "/to_detail_v3/{goodsId}", method = RequestMethod.GET)
    @ResponseBody
    public Result<GoodsDetailVo> getGoodsDetailV3(User user, @PathVariable("goodsId") long goodsId) {

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

        long startTime = goodsVo.getStartDate().getTime();
        long endTime = goodsVo.getEndDate().getTime();
        long currentTime = System.currentTimeMillis();

        int seckillStatus = 0;
        int remainSeconds = 0;
        if(currentTime < startTime) {       // 秒杀还没开始，需要进行倒计时
            seckillStatus = 0;
            remainSeconds = (int)((startTime - currentTime) / 1000);
        } else if(currentTime > endTime) {  // 秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        } else {                            // 秒杀进行时
            seckillStatus = 1;
            remainSeconds = 0;
        }

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoodsVo(goodsVo);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setSeckillStatus(seckillStatus);
        goodsDetailVo.setUser(user);

        return Result.success(goodsDetailVo);
    }
}
