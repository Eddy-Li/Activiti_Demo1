package com.myit;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.identity.*;
import org.activiti.engine.impl.identity.Authentication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.UUID;

public class IdentityServiceDemo {
    //流程引擎对象
    private ProcessEngine engine;
    //
    private IdentityService identityService;

    @Before
    public void before() {
        //ProcessEngines.getDefaultProcessEngine()=>会自动加载resources目录下的名为activiti.cfg.xml的配置文件
        engine = ProcessEngines.getDefaultProcessEngine();
        identityService = engine.getIdentityService();
    }

    @After
    public void after() {
        engine.close();
    }


    //1.创建用户组:由activiti自动生成用户组id
    @Test
    public void test1() {
        String groupId = UUID.randomUUID().toString();
        Group group = identityService.newGroup(groupId);
        //需要将id设置为null
        group.setId(null);
        group.setName("经理组");
        group.setType("manager");
        identityService.saveGroup(group);
    }

    //创建用户组:自己创建用户组id
    @Test
    public void test2() {
        String groupId = UUID.randomUUID().toString();
        Group group = identityService.newGroup(groupId);
        group.setName("职员组");
        group.setType("operater");
        identityService.saveGroup(group);
    }

    //2.查询用户组
    //注意：一个GroupQuery对象，只能查询一次，再次查询时，需要重新创建GroupQuery对象
    @Test
    public void test3() {
        //a.通过用户组主键id查询
        GroupQuery groupQuery1 = identityService.createGroupQuery();
        String groupId = "5001";
        Group group = groupQuery1.groupId(groupId).singleResult();
        System.out.println("id:" + group.getId() + ", name:" + group.getName() + ", type:" + group.getType());//id:5001, name:经理组, type:manager
        System.out.println("==================================================");

        //b.通过模糊、按多个字段排序查询
        GroupQuery groupQuery2 = identityService.createGroupQuery();
        //调用orderByGroupId()方法时一定要调用desc()或asc()方法，即orderByGroupId().desc()同事出现
        List<Group> groupList = groupQuery2.groupNameLike("%组%").orderByGroupId().desc().orderByGroupName().asc().list();
        for (Group g : groupList) {
            System.out.println("id:" + g.getId() + ", name:" + g.getName() + ", type:" + g.getType());//id:5001, name:经理组, type:manager

        }
        System.out.println("==================================================");

        //c.通过原生sql查询
        NativeGroupQuery nativeGroupQuery = identityService.createNativeGroupQuery();
        List<Group> glist = nativeGroupQuery.sql("SELECT ID_, NAME_, TYPE_ FROM ACT_ID_GROUP WHERE NAME_ = #{name1}")
                .parameter("name1", "职员组")
                .list();
        for (Group g : glist) {
            System.out.println("id:" + g.getId() + ", name:" + g.getName() + ", type:" + g.getType());//id:5001, name:经理组, type:manager

        }

        //根据用户id查找用户所在的用户组，一个用户可能有多个用户组
        //GroupQuery groupQuery3 = identityService.createGroupQuery();
        //String userId = null;
        //List<Group> groups = groupQuery3.groupMember(userId).list();

    }

    //3.修改用户组
    //identityService.saveGroup(group) => activiti会根据group对象的版本REV_字段判断，如果REV_字段 >= 1,则视为修改用户组，否则就是新建用户组
    //修改用户组，是根据groupId来修改的，且groupId不能修改
    @Test
    public void test4() {
        //先查询获取到用户组
        GroupQuery groupQuery1 = identityService.createGroupQuery();
        String groupId = "5001";
        Group group = groupQuery1.groupId(groupId).singleResult();
        System.out.println("id:" + group.getId() + ", name:" + group.getName() + ", type:" + group.getType());//id:5001, name:经理组, type:manager

        //修改用户组
        group.setName("总监组");
        identityService.saveGroup(group);
    }

    //4.删除用户组
    @Test
    public void test5() {
        String groupId = "";
        identityService.deleteGroup(groupId);
    }

    // 用户操作同上
    @Test
    public void test6() throws IOException {
        String userId = UUID.randomUUID().toString();
        User user = identityService.newUser(userId);
        user.setId(null);
        user.setFirstName("3");
        user.setLastName("3");
        user.setEmail("3");
        user.setPassword("3");
        boolean pictureSet = user.isPictureSet();
        identityService.saveUser(user);

        //用户信息管理
        //用户信息保存
        //保存在ACT_ID_INFO表中
        String key = "age";
        String value = "20";
        identityService.setUserInfo(userId,key,value);
        //用户信息查询
        String userInfo = identityService.getUserInfo(userId, key);
        //用户信息删除
        identityService.deleteUserInfo(userId,key);

        //设置用户图片
        FileInputStream fileInputStream = new FileInputStream(new File("...."));
        BufferedImage img = ImageIO.read(fileInputStream);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(img,"png",output);
        byte[] bytes = output.toByteArray();
        Picture picture = new Picture(bytes,"angus image");
        identityService.setUserPicture(userId,picture);
        //获取用户图片
        Picture userPicture = identityService.getUserPicture(userId);
        String mimeType = userPicture.getMimeType();
        byte[] bytes1 = userPicture.getBytes();
        InputStream inputStream = userPicture.getInputStream();
    }

    @Test
    public void test7() {
        //setAuthenticatedUserId():设置认证用户
        //setAuthenticatedUserId()只会在本线程有效
        identityService.setAuthenticatedUserId("10001");
        System.out.println(Authentication.getAuthenticatedUserId());//10001
        new Thread() {
            @Override
            public void run() {
                identityService.setAuthenticatedUserId("12501");
                System.out.println("1:" + Authentication.getAuthenticatedUserId());//12501
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                System.out.println("2:" + Authentication.getAuthenticatedUserId());//null
            }
        }.start();
    }

    @Test
    public void test8(){
        String userId = "";
        String groupId = "";
        //将用户加入到用户组中(ACT_ID_MEMBERSHIP表)
        identityService.createMembership(userId,groupId);
        //将用户从用户组中移除(ACT_ID_MEMBERSHIP表)
        identityService.deleteMembership(userId,groupId);

        //查询用户组下所有用户
        UserQuery userQuery = identityService.createUserQuery();
        List<User> users = userQuery.memberOfGroup(groupId).list();

        //查询用户的所有所属用户组
        GroupQuery groupQuery = identityService.createGroupQuery();
        List<Group> groups = groupQuery.groupMember(userId).list();
    }

    @Test
    public void test9(){

    }
}
