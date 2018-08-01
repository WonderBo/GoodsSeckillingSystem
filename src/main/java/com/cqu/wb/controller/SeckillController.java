package com.cqu.wb.controller;

import com.cqu.wb.domain.Order;
import com.cqu.wb.domain.SeckillOrder;
import com.cqu.wb.domain.User;
import com.cqu.wb.result.CodeMessage;
import com.cqu.wb.service.GoodsService;
import com.cqu.wb.service.OrderService;
import com.cqu.wb.service.SeckillService;
import com.cqu.wb.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
     */
    @RequestMapping(value = "/do_seckill")
    public String seckill(Model model, User user, @RequestParam("goodsId")long goodsId) {
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
}
