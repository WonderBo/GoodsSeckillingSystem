package com.cqu.wb.service;

import com.cqu.wb.dao.DemoDao;
import com.cqu.wb.domain.Demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jingquan on 2018/7/24.
 */
@Service
public class DemoService {
    @Autowired
    private DemoDao demoDao;

    /**
     *
     * @param id
     * @return
     * @description 根据Id获取Demo实例
     */
    public Demo getDemoById(int id) {
        return demoDao.getDemoById(id);
    }

    /**
     *
     * @return
     * @description 测试事务
     */
    @Transactional
    public boolean insertDemo() {
        Demo demo1 = new Demo();
        demo1.setId(2);
        demo1.setName("jack");
        demoDao.insertDemo(demo1);

        Demo demo2 = new Demo();
        demo2.setId(1);
        demo2.setName("mary");
        demoDao.insertDemo(demo2);

        return true;
    }
}
