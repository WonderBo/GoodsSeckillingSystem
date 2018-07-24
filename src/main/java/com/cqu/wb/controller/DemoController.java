package com.cqu.wb.controller;

import com.cqu.wb.domain.Demo;
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
        return Result.erroe(CodeMessage.SERVER_ERROR);
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
    @RequestMapping(value = "/getDemoById", method = RequestMethod.GET)
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
    @RequestMapping(value = "/insertDemo", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> insertDemo() {
        demoService.insertDemo();
        return  Result.success(true);
    }
}
