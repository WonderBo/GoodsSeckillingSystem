package com.cqu.wb.controller;

import com.cqu.wb.domain.Order;
import com.cqu.wb.domain.SeckillOrder;
import com.cqu.wb.domain.User;
import com.cqu.wb.result.CodeMessage;
import com.cqu.wb.result.Result;
import com.cqu.wb.service.GoodsService;
import com.cqu.wb.service.OrderService;
import com.cqu.wb.service.SeckillService;
import com.cqu.wb.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by jingquan on 2018/8/1.
 */
@Controller
@RequestMapping(value = "/seckill")
public class SeckillController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    /**
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     * @description 验证是否满足秒杀条件并处理秒杀请求（减库存、下订单、写入秒杀订单）
     *
     * 并发量：5000 * 10
     * 优化前：QPS: 1306
     * 优化后：QPS: 2114
     */
    @RequestMapping(value = "/do_seckill_v1")
    public String seckillV1(Model model, User user, @RequestParam("goodsId")long goodsId) {
        // 条件判断部分
        // 判断用户是否登录
        if(user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        //判断库存剩余数量
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int count = goodsVo.getStockCount();
        if(count <= 0) {
            model.addAttribute("error_message", CodeMessage.SECKILL_OVER);
            return "seckill_fail";
        }
        // 判断是否秒杀重复
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(seckillOrder != null) {
            model.addAttribute("error_message", CodeMessage.SECKILL_REPEATE);
            return "seckill_fail";
        }

        // 请求处理部分
        // 减库存、下订单、写入秒杀订单（原子操作）
        Order order = seckillService.seckill(user, goodsVo);
        model.addAttribute("goodsVo", goodsVo);
        model.addAttribute("order", order);

        return "order_detail";
    }
    /**
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     * @description 前后端分离／页面静态化：将页面的静态资源与动态资源分离，静态资源直接缓存在浏览器中，在有效期内不用再向后端
     *              请求静态资源（Expire、Cache-Control字段控制浏览器缓存），而动态资源则通过ajax向后端接口请求数据加载。
     *              GET请求与POST请求最大区别在于幂等性，GET具有幂等性，因此用于访问数据，POST则用于提交修改数据
     */
    @RequestMapping(value = "/do_seckill_v2", method = RequestMethod.POST)
    @ResponseBody
    public Result<Order> seckillV2(Model model, User user, @RequestParam("goodsId")long goodsId) {
        // 条件判断部分
        // 判断用户是否登录
        if(user == null) {
            return Result.error(CodeMessage.SESSION_ERROR);
        }
        model.addAttribute("user", user);
        //判断库存剩余数量
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int count = goodsVo.getStockCount();
        if(count <= 0) {
            return Result.error(CodeMessage.SECKILL_OVER);
        }
        // 判断是否秒杀重复
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(seckillOrder != null) {
            model.addAttribute("error_message", CodeMessage.SECKILL_REPEATE);
            return Result.error(CodeMessage.SECKILL_REPEATE);
        }

        // 请求处理部分
        // 减库存、下订单、写入秒杀订单（原子操作）
        Order order = seckillService.seckill(user, goodsVo);

        return Result.success(order);
    }
}
