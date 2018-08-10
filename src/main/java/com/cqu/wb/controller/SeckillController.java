package com.cqu.wb.controller;

import com.cqu.wb.access.AccessLimit;
import com.cqu.wb.domain.Order;
import com.cqu.wb.domain.SeckillOrder;
import com.cqu.wb.domain.User;
import com.cqu.wb.rabbitmq.SeckillMQMessage;
import com.cqu.wb.rabbitmq.SeckillMQSender;
import com.cqu.wb.redis.*;
import com.cqu.wb.result.CodeMessage;
import com.cqu.wb.result.Result;
import com.cqu.wb.service.GoodsService;
import com.cqu.wb.service.OrderService;
import com.cqu.wb.service.SeckillService;
import com.cqu.wb.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingquan on 2018/8/1.
 */
@Controller
@RequestMapping(value = "/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SeckillMQSender seckillMQSender;

    // 内存标记：保存各个秒杀商品是否已经秒杀完毕
    private Map<Long, Boolean> localSeckillGoodsOverMap = new HashMap<Long, Boolean>();

    /**
     *
     * @throws Exception
     * @description 在系统初始化时将秒杀商品库存数量添加到缓存中，并进行本地内存标记
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if(goodsVoList == null || goodsVoList.size() == 0) {
            return;
        }

        for(GoodsVo goodsVo : goodsVoList) {
            redisService.set(GoodsKey.seckillGoodsStockGoodsKey, "" + goodsVo.getId(), goodsVo.getStockCount());
            localSeckillGoodsOverMap.put(goodsVo.getId(), false);
        }
    }

    /**
     *
     * @return
     * @description 重置脚本：每次测试系统时先进行重置，包括：内存标记，缓存，数据库
     */
    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> resetSystem() {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        for(GoodsVo goodsVo : goodsVoList) {
            goodsVo.setStockCount(10);
            redisService.set(GoodsKey.seckillGoodsStockGoodsKey, "" + goodsVo.getId(), 10);
            localSeckillGoodsOverMap.put(goodsVo.getId(), false);
        }
        redisService.delete(OrderKey.userIdGoodsIdOrderKey);
        redisService.delete(SeckillKey.isGoodsSeckillOverSeckillKey);
        seckillService.resetDB(goodsVoList);

        return Result.success(true);
    }

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
        // 判断库存剩余数量
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
    /**
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     * @descriotion 使用缓存（缓存库存、已秒杀订单），消息队列（异步处理秒杀请求），内存标记来减少对数据库对访问，从而优化秒杀接口性能
     */
    @RequestMapping(value = "/do_seckill_v3", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> seckillV3(Model model, User user, @RequestParam("goodsId")long goodsId) {
        // 判断用户是否登录
        if(user == null) {
            return Result.error(CodeMessage.SESSION_ERROR);
        }
        model.addAttribute("user", user);

        // 内存标记：判断秒杀商品是否已经秒杀完毕，减少缓存到访问（存在网络带宽消耗）
        boolean isSeckillGoodsOver = localSeckillGoodsOverMap.get(goodsId);
        if(isSeckillGoodsOver == true) {
            return Result.error(CodeMessage.SECKILL_OVER);
        }

        // 预减库存
        long stock = redisService.decr(GoodsKey.seckillGoodsStockGoodsKey, "" + goodsId);
        if(stock < 0) {
            localSeckillGoodsOverMap.put(goodsId, true);    // 更新内存标记，后面到请求将不会访问缓存
            return Result.error(CodeMessage.SECKILL_OVER);
        }

        // 判断是否秒杀重复
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(seckillOrder != null) {
            return Result.error(CodeMessage.SECKILL_REPEATE);
        }

        // 构造并发送秒杀消息进入队列
        SeckillMQMessage seckillMQMessage = new SeckillMQMessage();
        seckillMQMessage.setUser(user);
        seckillMQMessage.setGoodsVoId(goodsId);
        seckillMQSender.sendSeckillMessage(seckillMQMessage);

        // 不直接返回秒杀结果，而是返回排队中
        return Result.success(0);
    }
    /**
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     * @description 安全优化后（图形验证码、接口限流防刷、隐藏秒杀地址）的秒杀接口
     */
    @RequestMapping(value = "/{seckillPath}/do_seckill_v4", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> seckillV4(Model model, User user, @RequestParam("goodsId")long goodsId,
                                     @PathVariable("seckillPath") String seckillPath) {
        // 判断用户是否登录
        if(user == null) {
            return Result.error(CodeMessage.SESSION_ERROR);
        }
        model.addAttribute("user", user);

        // 验证秒杀地址
        boolean checkSeckillPath = seckillService.checkSeckillPath(user, goodsId, seckillPath);
        if(checkSeckillPath == false) {
            return Result.error(CodeMessage.ILLEGAL_REQUEST);
        }

        // 内存标记：判断秒杀商品是否已经秒杀完毕，减少缓存到访问（存在网络带宽消耗）
        boolean isSeckillGoodsOver = localSeckillGoodsOverMap.get(goodsId);
        if(isSeckillGoodsOver == true) {
            return Result.error(CodeMessage.SECKILL_OVER);
        }

        // 预减库存
        long stock = redisService.decr(GoodsKey.seckillGoodsStockGoodsKey, "" + goodsId);
        if(stock < 0) {
            localSeckillGoodsOverMap.put(goodsId, true);    // 更新内存标记，后面到请求将不会访问缓存
            return Result.error(CodeMessage.SECKILL_OVER);
        }

        // 判断是否秒杀重复
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(seckillOrder != null) {
            return Result.error(CodeMessage.SECKILL_REPEATE);
        }

        // 构造并发送秒杀消息进入队列
        SeckillMQMessage seckillMQMessage = new SeckillMQMessage();
        seckillMQMessage.setUser(user);
        seckillMQMessage.setGoodsVoId(goodsId);
        seckillMQSender.sendSeckillMessage(seckillMQMessage);

        // 不直接返回秒杀结果，而是返回排队中
        return Result.success(0);
    }

    /**
     *
     * @param response
     * @param user
     * @param goodsId
     * @return
     * @description 获取秒杀验证码图片（请求的是图片地址，实际通过response返回的是图片的'流'）
     */
    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillVerifyCode(HttpServletResponse response, User user, @RequestParam("goodsId")long goodsId) {
        // 判断用户是否登录
        if(user == null) {
            return Result.error(CodeMessage.SESSION_ERROR);
        }

        try {
            BufferedImage bufferedImage = seckillService.createSeckillVerifyCode(user, goodsId);
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(bufferedImage, "JPEG", outputStream);
            outputStream.flush();
            outputStream.close();

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMessage.SECKILL_FAIL);
        }
    }

    /**
     *
     * @param user
     * @param goodsId
     * @param verifyAnswer
     * @return
     * @description 发送秒杀请求前需要先进行接口单位时间访问次数限制判断【强耦合版】、验证码判断，再请求动态秒杀地址
     */
    @RequestMapping(value = "/path_v1", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillPathV1(HttpServletRequest request, User user, @RequestParam("goodsId")long goodsId,
                                         @RequestParam(value = "verifyAnswer", defaultValue = "0") int verifyAnswer) {
        // 判断用户是否登录
        if(user == null) {
            return Result.error(CodeMessage.SESSION_ERROR);
        }

        // 请求访问次数判断（此实现方式存在代码冗余，且限制要求与代码强耦合在一起，建议通用实现方式）
        String requestURI = request.getRequestURI();
        Integer accessCount = redisService.get(AccessKey.accessCountAccessKey, requestURI + "_" + user.getId(), Integer.class);
        if(accessCount == null) {
            redisService.set(AccessKey.accessCountAccessKey, requestURI + "_" + user.getId(), 1);
        } else if(accessCount < 5) {
            redisService.incr(AccessKey.accessCountAccessKey, requestURI + "_" + user.getId());
        } else {
            return Result.error(CodeMessage.ACCESS_LIMIT);
        }

        // 验证码结果判断
        boolean checkResult = seckillService.checkVerifyAnswer(user, goodsId, verifyAnswer);
        if(checkResult == false) {
            return Result.error(CodeMessage.VERIFY_ERROR);
        }

        String seckillPath = seckillService.createSeckillPath(user, goodsId);

        return Result.success(seckillPath);
    }

    /**
     *
     * @param user
     * @param goodsId
     * @param verifyAnswer
     * @return
     * @description 发送秒杀请求前需要先进行接口单位时间访问次数限制判断【注解通用版】、验证码判断，再请求动态秒杀地址
     */
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path_v2", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillPathV2(User user, @RequestParam("goodsId")long goodsId,
                                           @RequestParam(value = "verifyAnswer", defaultValue = "0") int verifyAnswer) {
        // 验证码结果判断
        boolean checkResult = seckillService.checkVerifyAnswer(user, goodsId, verifyAnswer);
        if(checkResult == false) {
            return Result.error(CodeMessage.VERIFY_ERROR);
        }

        String seckillPath = seckillService.createSeckillPath(user, goodsId);

        return Result.success(seckillPath);
    }

    /**
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     * @description 获取异步秒杀结果，结果为查询得到的订单号则秒杀成功，结果为-1则秒杀失败，结果为0则还在排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model, User user, @RequestParam("goodsId")long goodsId) {
        // 判断用户是否登录
        if(user == null) {
            return Result.error(CodeMessage.SESSION_ERROR);
        }
        model.addAttribute("user", user);

        long result = seckillService.getSeckillResult(user.getId(), goodsId);

        return Result.success(result);
    }
}
