package com.cqu.wb.controller;

import com.cqu.wb.domain.Order;
import com.cqu.wb.domain.User;
import com.cqu.wb.result.CodeMessage;
import com.cqu.wb.result.Result;
import com.cqu.wb.service.GoodsService;
import com.cqu.wb.service.OrderService;
import com.cqu.wb.vo.GoodsVo;
import com.cqu.wb.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by jingquan on 2018/8/5.
 */
@Controller
@RequestMapping(value = "/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    /**
     *
     * @param user
     * @param orderId
     * @return
     * @deacription 获取订单信息与商品信息后进行封装，订单详情页ajax请求该接口得到相关数据用于前后端分离／页面静态化
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public Result<OrderDetailVo> orderDetail(User user, @RequestParam("orderId") long orderId) {
        if(user == null) {
            return Result.error(CodeMessage.SESSION_ERROR);
        }

        Order order = orderService.getOrderById(orderId);
        if(order == null) {
            return Result.error(CodeMessage.ORDER_NOT_EXIST);
        }

        long goodsId = order.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);

        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setGoodsVo(goodsVo);
        orderDetailVo.setOrder(order);

        return Result.success(orderDetailVo);
    }
}
