package com.myit;

import org.activiti.engine.*;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Demo1 {
    //流程引擎对象
    private ProcessEngine engine;
    //流程存储服务组件
    private RepositoryService repositoryService;
    //流程运行时服务组件
    private RuntimeService runtimeService;
    //流程任务组件
    private TaskService taskService;

    //1.创建ProcessEngine对象，其他Service对象
    @Before
    public void before() {
        //ProcessEngines.getDefaultProcessEngine()=>会自动加载resources目录下的名为activiti.cfg.xml的配置文件
        engine = ProcessEngines.getDefaultProcessEngine();
        repositoryService = engine.getRepositoryService();
        runtimeService = engine.getRuntimeService();
        taskService = engine.getTaskService();
    }


    @After
    public void after() {
        engine.close();
    }

    //2.部署流程
    @Test
    public void test1() {
        //部署流程
        DeploymentBuilder deployment = repositoryService.createDeployment();
        //添加流程定义文件，可以一次部署多个流程定义文件，流程定义文件为".bpmn"格式文件
        //a.会将此".bpmn"格式文件对应的xml、png文件保存在ACT_GE_BYTEARRAY表中
        deployment.addClasspathResource("demo1.xml");
        //过滤重复部署
        deployment.enableDuplicateFiltering();
        deployment.deploy();
    }

    //3.创建(启动一个)流程实例
    @Test
    public void test2() {
        //创建一个流程实例
        //id为bpmn中定义的Id属性
        //注意是用startProcessInstanceByKey(...)方法，不是startProcessInstanceById(...)方法
        ProcessInstance myProcess = runtimeService.startProcessInstanceByKey("myProcess1");

        //流程实例的当前任务查询
        TaskQuery taskQuery = taskService.createTaskQuery();
        Task task = taskQuery.processInstanceId(myProcess.getId()).singleResult();
        System.out.println(task.getName());

        //4.完成当前任务
        taskService.complete(task.getId());


        taskQuery = taskService.createTaskQuery();
        task = taskQuery.processInstanceId(myProcess.getId()).singleResult();
        System.out.println(task.getName());

    }

//    //4.完成当前任务
//    @Test
//    public void test3() {
//        runtimeService.createProcessInstanceQuery().processInstanceId("myProcess1").
//        taskService.createTaskQuery().processInstanceId(
//                )
//
//    }

    
}
