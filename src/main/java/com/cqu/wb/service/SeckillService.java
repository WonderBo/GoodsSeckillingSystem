package com.cqu.wb.service;

import com.cqu.wb.domain.Order;
import com.cqu.wb.domain.SeckillOrder;
import com.cqu.wb.domain.User;
import com.cqu.wb.redis.RedisService;
import com.cqu.wb.redis.SeckillKey;
import com.cqu.wb.util.MD5Util;
import com.cqu.wb.util.UUIDUtil;
import com.cqu.wb.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/**
 * Created by jingquan on 2018/8/1.
 */
@Service
public class SeckillService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisService redisService;

    /**
     *
     * @param user
     * @param goodsId
     * @return
     * @description 使用AWT生成验证码图片
     */
    public BufferedImage createSeckillVerifyCode(User user, long goodsId) {
        // 输入验证
        if(user == null || goodsId <= 0) {
            return null;
        }

        int width = 80;
        int height = 32;
        // 生成图片
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        // 设置背景颜色
        graphics.setColor(new Color(0xDCDCDC));
        graphics.fillRect(0, 0, width, height);
        // 画边界
        graphics.setColor(Color.BLACK);
        graphics.drawRect(0, 0, width - 1, height - 1);
        // 生成Random实例用于生成随机码
        Random random = new Random();
        // 制造些混淆
        for(int i = 0; i < 50; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            graphics.drawOval(x, y, 0, 0);
        }
        // 生成随机验证码
        String verifyCode = generateVerifyCode(random);
        graphics.setColor(new Color(0, 100, 0));
        graphics.setFont(new Font("Candara", Font.BOLD, 24));
        graphics.drawString(verifyCode, 8, 24);
        graphics.dispose();
        // 将验证码计算结果存入缓存
        int verifyResult = calculateVerifyCode(verifyCode);
        redisService.set(SeckillKey.seckillVerifyCodeSeckillKey, user.getId() + "_" + goodsId, verifyResult);

        // 返回验证图片
        return bufferedImage;
    }


    private char[] operatorArray = new char[] {'+', '-', '*'};
    /**
     * @param random
     * @description 生成随机验证码字符串
     */
    private String generateVerifyCode(Random random) {
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);
        char op1 = operatorArray[random.nextInt(3)];
        char op2 = operatorArray[random.nextInt(3)];

        String verifyCode = "" + num1 + op1 + num2 + op2 + num3;

        return verifyCode;
    }

    /**
     *
     * @param verifyCode
     * @return
     * @description 利用JS脚本引擎计算验证码字符串
     */
    private int calculateVerifyCode(String verifyCode) {
        try {
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");

            return (Integer)scriptEngine.eval(verifyCode);
        } catch (Exception e) {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }

    /**
     *
     * @param user
     * @param goodsId
     * @param verifyAnswer
     * @return
     * @description 验证用户输入的验证码结果
     */
    public boolean checkVerifyAnswer(User user, long goodsId, int verifyAnswer) {
        // 输入验证
        if(user == null || goodsId <= 0) {
            return false;
        }

        Integer redisVerifyResult = redisService.get(SeckillKey.seckillVerifyCodeSeckillKey,
                user.getId() + "_" + goodsId, Integer.class);
        // 验证验证结果是否正确以及过期
        if(redisVerifyResult == null || redisVerifyResult - verifyAnswer != 0) {
            return false;
        }
        // 删除缓存验证结果（可选）
        redisService.delete(SeckillKey.seckillVerifyCodeSeckillKey, user.getId() + "_" + goodsId);

        return true;
    }

    /**
     *
     * @param user
     * @param goodsId
     * @return
     * @description 生成随机秒杀地址，并存入缓存以便秒杀请求时验证该地址
     */
    public String createSeckillPath(User user, long goodsId) {
        // 输入验证
        if(user == null || goodsId <= 0) {
            return null;
        }

        String seckillPath = MD5Util.md5(UUIDUtil.getUuid() + "123456");
        redisService.set(SeckillKey.seckillPathSeckillKey, user.getId() + "_" + goodsId, seckillPath);

        return seckillPath;
    }

    /**
     *
     * @param user
     * @param goodsId
     * @param seckillPath
     * @return
     * @description 验证用户秒杀请求中的地址和之前在缓存中生成的地址是否相同
     */
    public boolean checkSeckillPath(User user, long goodsId, String seckillPath) {
        // 输入验证
        if(user == null || seckillPath == null) {
            return false;
        }

        String redisPath = redisService.get(SeckillKey.seckillPathSeckillKey, user.getId() + "_" + goodsId, String.class);

        return seckillPath.equals(redisPath);
    }

    /**
     *
     * @param user
     * @param goodsVo
     * @return
     * @description 减库存、下订单、写入秒杀订单（原子操作）
     */
    @Transactional
    public Order seckill(User user, GoodsVo goodsVo) {
        boolean result = goodsService.reduceStock(goodsVo);

        // 必须保证减库存成功后才可以下订单，事务只会保证发生异常时才会回滚，而减库存失败（库存为0）不是异常，而是更新操作不影响表记录
        if(result) {
            return orderService.createOrder(user, goodsVo);
        } else {
            setSeckillGoodsOver(goodsVo.getId());   // 在缓存中设置秒杀商品已经被秒杀完的标记
            return null;
        }
    }

    /**
     *
     * @param userId
     * @param goodsVoId
     * @return
     * @description 获取秒杀结果（是否生成秒杀订单）
     */
    public long getSeckillResult(long userId, long goodsVoId) {
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(userId, goodsVoId);
        if(seckillOrder != null) {  // 秒杀成功
            return seckillOrder.getOrderId();
        } else {
            boolean isSeckillOver = getSeckillGoodsOver(goodsVoId);     // 获取缓存中秒杀商品是否已经被秒杀完的标记
            if(isSeckillOver) {
                return -1;      // 秒杀失败
            } else {
                return 0;       // 排队中
            }
        }
    }

    /**
     *
     * @param goodsVoId
     * @description 在缓存中设置秒杀商品已经被秒杀完的标记
     */
    private void setSeckillGoodsOver(long goodsVoId) {
        redisService.set(SeckillKey.isGoodsSeckillOverSeckillKey, "" + goodsVoId, true);
    }

    /**
     *
     * @param goodsVoId
     * @return
     * @description 获取缓存中秒杀商品是否已经被秒杀完的标记
     */
    private boolean getSeckillGoodsOver(long goodsVoId) {
        return redisService.exists(SeckillKey.isGoodsSeckillOverSeckillKey, "" + goodsVoId);
    }

    /**
     *
     * @param goodsVoList
     * @descrption 重置数据库：重置秒杀商品库存，删除订单和秒杀订单
     */
    public void resetDB(List<GoodsVo> goodsVoList) {
        goodsService.resetStock(goodsVoList);
        orderService.deleteOrders();
    }
}
