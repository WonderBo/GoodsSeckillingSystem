package com.cqu.wb.controller;

import com.cqu.wb.domain.Demo;
import com.cqu.wb.rabbitmq.RabbitMQSender;
import com.cqu.wb.redis.DemoKey;
import com.cqu.wb.redis.RedisService;
import com.cqu.wb.result.CodeMessage;
import com.cqu.wb.result.Result;
import com.cqu.wb.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by jingquan on 2018/7/24.
 */
@Controller
@RequestMapping(value = "/hello")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @Autowired
    private RedisService redisServer;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    /**
     *
     * @return
     * @description 封装正确REST JSON输出结果
     */
    @RequestMapping(value = "/success", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> helloSuccess() {
        return Result.success("hello, success!");
    }

    /**
     *
     * @return
     * @description 封装异常REST JSON输出结果
     */
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> helloError() {
        return Result.error(CodeMessage.SERVER_ERROR);
    }

    /**
     *
     * @param model
     * @return
     * @description 返回Thymeleaf页面（页面由返回字符串加上配置文件的前后缀）
     */
    @RequestMapping(value = "/thymeleaf", method = RequestMethod.GET)
    public String helloThymeleaf(Model model) {
        model.addAttribute("name", "Wonder");
        return "hello";
    }

    /**
     *
     * @return
     * @description 根据Id获取Demo实例
     */
    @RequestMapping(value = "/db/get", method = RequestMethod.GET)
    @ResponseBody
    public Result<Demo> getDemoById() {
        Demo demo = demoService.getDemoById(1);
        return Result.success(demo);
    }

    /**
     *
     * @return
     * @description 测试事务
     */
    @RequestMapping(value = "/db/tx", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> insertDemo() {
        demoService.insertDemo();
        return  Result.success(true);
    }

    /**
     *
     * @return
     * @description 从Redis中获取值
     */
    @RequestMapping(value = "/redis/get", method = RequestMethod.GET)
    @ResponseBody
    public Result<Demo> redisGet() {
        Demo demo = redisServer.get(DemoKey.idDemoKey, "1", Demo.class);
        return Result.success(demo);
    }

    /**
     *
     * @return
     * @description 向Redis中设置值
     */
    @RequestMapping(value = "/redis/set", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> redisSet() {
        Demo demo = new Demo();
        demo.setId(1);
        demo.setName("mike");
        boolean result = redisServer.set(DemoKey.idDemoKey, "1", demo);
        return Result.success(result);
    }

//    /**
//     *
//     * @return
//     * @description 向消息队列发送消息（Direct模式）
//     */
//    @RequestMapping(value = "/mq/direct", method = RequestMethod.GET)
//    @ResponseBody
//    public Result<String> sendDirectMessage() {
//        rabbitMQSender.sendDirect("Hello Direct RabbitMQ");
//        return Result.success("success");
//    }
//
//    /**
//     *
//     * @return
//     * @description 向消息队列发送消息（Topic模式）
//     */
//    @RequestMapping(value = "/mq/topic", method = RequestMethod.GET)
//    @ResponseBody
//    public Result<String> sendTopicMessage() {
//        rabbitMQSender.sendTopic("Hello Topic RabbitMQ");
//        return Result.success("success");
//    }
//
//    /**
//     *
//     * @return
//     * @description 向消息队列发送消息（Fanout模式）
//     */
//    @RequestMapping(value = "/mq/fanout", method = RequestMethod.GET)
//    @ResponseBody
//    public Result<String> sendFanoutMessage() {
//        rabbitMQSender.sendFanout("Hello Fanout RabbitMQ");
//        return Result.success("success");
//    }
//
//    /**
//     *
//     * @return
//     * @description 向消息队列发送消息（Header模式）
//     */
//    @RequestMapping(value = "/mq/header", method = RequestMethod.GET)
//    @ResponseBody
//    public Result<String> sendHeaderMessage() {
//        rabbitMQSender.sendHeaders("Hello Header RabbitMQ");
//        return Result.success("success");
//    }
}
