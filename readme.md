# 问答社区简单介绍

## 技术选型

* SpringBoot
* SpringMvc
* Mybatis（tk.mybatis）
* Mysql8.0+
* Thymeleaf
* BootStrap
* Jquery，JavaScript和时间库

### 布局介绍

使用BootStrap来进行设计的一个栅格页面，大部分页面是9：3分。

### 提高可扩展性

使用全局异常捕获和自定义异常，使用枚举处理一些带有选择的选项，尽量抽出方法保持代码的优雅性，使用类似于JWT的token认证。

## 实现流程介绍

### 社区权限规则介绍

#### 未登录用户

未登录用户只能浏览问题，不能进行评论和发布，修改，查看其他人回复内容，如果想要进行发布等操作会使用JS配合Controller跳转到登陆验证的控制器。

#### 已登陆用户

已登陆用户可以进行浏览，发布，修改自己发布的问题，查看所有自己发布的问题，查看回复内容

### 自定义异常实现全局异常处理

#### 自定义异常

自定义异常接口

```Java
public interface ICustomizeErrorCode {
    String getMessage();
    Integer getCode();

}
```

自定义异常实现此接口规范异常处理

```Java
public class CustomizeException extends RuntimeException {

    private String message;
    private Integer code;

    public CustomizeException(ICustomizeErrorCode customizeErrorCode) {
        this.code = customizeErrorCode.getCode();
        this.message = customizeErrorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
```

#### 全局异常处理（需要判断处理请求格式）

使用ControllerAdvice进行响应全局异常，在里面定义一个异常处理器处理异常,处理难点就是需要根据不同的请求类型来进行响应错误代码（如果ContentType是Json就使用Json发送异常信息，如果是HTML就返回error页面并渲染信息）

```Java
 @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable ex,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        String contentType = request.getContentType();
        if (contentType!=null&&contentType.equals("application/json")) {
            //如果是自定义异常（自己抛出的异常）就用JSON格式返回
            ResultDTO resultDTO;
            if (ex instanceof CustomizeException) {
                resultDTO = ResultDTO.errorOf((CustomizeException) ex);
            } else {
                //否则就是模板异常
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYSTEM_ERROR);
            }
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
            //如果不是JSON，就返回页面
        } else {
            if (ex instanceof CustomizeException) {
                model.addAttribute("message", ex.getMessage());
            } else {
                model.addAttribute("message", CustomizeErrorCode.SYSTEM_ERROR.getMessage());
            }
            return new ModelAndView("error");
        }
    }
```

### GitHub认证简单介绍

使用GitHub实现用户的注册和登陆，下面是GitHub接口调用流程。

![1560486206855](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1560486206855.png)

1. 用户登陆去调用GitHub
2. GitHub回调Controller中的callback接口，并传一个code和state
3. 通过client_id，client_secret，code，state获取Access_Token
4. 使用OKHTTP携带用户信息和token发送给GiiHub

```Java
OkHttpClient client = new OkHttpClient();
Request request = new Request.Builder()
        .url("https://api.github.com/user?access_token=" + accessToken)
        .build();
```

5. 然后通过GitHubUser接收GitHub用户信息
6. 回调请求结束，返回登陆成功，刷新用户状态。

> 说复杂页不复杂，说不复杂也很复杂.

### 登陆实现

实现步骤：

1. 从GitHub拉取用户信息
2. 如果是第一次使用GitHub登陆就直接使用insert给用户表添加信息（相当于注册），并且给生成一个token存在Cookie里
3. 如果不是第一次的话，根据gitHubUse.id在数据库查询有没有此用户，如果有就把此token写入cookie。
4. 使用拦截器根据Cookie在数据库查数据，并写入Session，从而实现登陆。

> 自动登陆也是根据拦截器来实现

### 退出登陆

清空Session里面的user，并且清空Cookie。这里有一个需要注意的的点，Cookie是不能被直接移除的，换个角度可以设置此Cookie中的token为null，并且设置时长为0就可以解决删除Cookie的问题。

### 问题实现

#### 问题发布

发布一个问题需要根据多表操作来完成

使用QuestionDTO来实现对Question实体的封装，加入User实体到QuestionDTO中，方便于在前端显示评论人，评论人头像等信息。

发布的问题的主要内容不能为空，**使用js+后端双重检测是否非法**，并且记录发布时间并在前端按指定格式显示。

前端使用BootStrap来进行属性的收集和发送，使用表单进行数据提交。

页面展示：

![1560499585398](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1560499585398.png)



#### 问题修改

和发布使用同一个界面，不同的是根据当前问题的内容对输入框进行回显（如果当前修改的问题有内容的话就回显内容）

需要更新这些数据项外还要修改时间。

#### 问题展示

##### 所有问题展示

![1560499488259](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1560499488259.png)

##### 单个问题展示

在主页对所有问题进行展示，用使用分页技术，并渲染时间，浏览数和回复数信息。并且在右边栅格中显示问题的发起人的信息。
效果如下：

![1560498719520](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1560498719520.png)

##### 我的问题展示

![1560499535659](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1560499535659.png)

#### 问题删除

问题的删除不仅仅是见简单的问题的删除，并且要搜索到所有和此问题相关的评论也进行删除（一级评论和二级评论），也是一个多表的联动操作。如果不这样的话，会造成空间浪费。

#### 相关问题

相关问题还没有进行实现，但是思路也是一样的，根据问题的title进行匹配并且渲染到页面上。

### 评论实现

> 所有的评论顺序按找时间的倒叙排列，保持最新的评论在最上边（无论是一级评论还是二级评论）

#### 一级评论

一级评论是直接在问题下进行评论，并更新问题的回复数，是问题下的评论，需要加上评论时间。

#### 二级评论

二级评论是评论下的评论，插入此条评论的同时也需要更新父评论的评论数和评论时间

#### 删除评论

在本人评论下会显示删除按钮，可以进行评论的删除，每人只能删除自己写的评论

#### 评论展示

##### 一级评论展示

直接从调用接口后，Request域中的此问题的所有评论，并逐条渲染到前端页面

##### 二级评论展示

使用BootStrap中折叠样式，配合JS的onclick时间，触发折叠样式，并使用一个属性来存当前是否是折叠状态，下一次点击需不需要关闭，然后根据Jquery（append）的添加子DIV来实现子评论的内嵌并且对返回的所有子评论进行数据渲染。

并使用Css对页面的margin，border和background进行调试

展示效果

![1560497831233](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1560497831233.png)

![1560497891703](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1560497891703.png)

#### 评论点赞

使用数据库update，点赞数给指定的评论并进行前端渲染。一人只能点一次赞。

#### 取消点赞

和点赞实现步骤相同

### 搜索主题实现

搜索用的是对文章主题的一个模糊查询，很简单那，并且将它渲染到前端页面，和首页布局大致相同

### 分页原理（后端物理分页）

分页也可以算是Web的基础了，没有使用分页插件来实现分页，而是手动的写分页，并且分页校验。

分页需要传入的属性就是页码和页容量，根据页码和页容量可以算出limit的偏移量

页码的显示是根据后台逻辑，根据总记录数/页容量可以算出页的数量，并且渲染到前端

提供一个直接回到首页，和末尾的跳转还是根据上变的信息进行分页查询

加入校验：

1. 输入负数的页码会跳到第一页
2. 输入很大的页码会跳到末页
3. 输入的不是数字会进行异常处理
4. 根据是否存在足够的页来进行页选择栏的展示

## 数据库表的设计

```Mysql
 user  | CREATE TABLE `user` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ACCOUNT_ID` varchar(100) DEFAULT NULL,
  `NAME` varchar(50) DEFAULT NULL,
  `TOKEN` varchar(36) DEFAULT NULL,
  `GMT_CREATE` bigint(20) DEFAULT NULL,
  `GMT_MODIFIED` bigint(20) DEFAULT NULL,
  `bio` varchar(256) DEFAULT NULL,
  `avatar_url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
```

```Mysql
 question | CREATE TABLE `question` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) DEFAULT NULL,
  `description` text,
  `gmt_create` bigint(20) DEFAULT NULL,
  `gmt_modified` bigint(20) DEFAULT NULL,
  `creator` int(11) DEFAULT NULL,
  `comment_count` int(11) DEFAULT '0',
  `view_count` int(11) DEFAULT '0',
  `like_count` int(11) DEFAULT '0',
  `tag` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
```

```Mysql
 comment | CREATE TABLE `comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) NOT NULL,
  `type` int(11) NOT NULL,
  `commentator` int(11) NOT NULL,
  `gmt_create` bigint(20) NOT NULL,
  `gmt_modified` bigint(20) NOT NULL,
  `like_count` bigint(20) DEFAULT '0',
  `content` varchar(1024) DEFAULT NULL,
  `comment_count` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
```

数据表中有几个设计的不合理，但是由于用户量不可能达到上限，因此就忽略此问题了！！！

## 后期设想

进行完善和优化并部署到云服务器上，写代码一定要注重它的可扩展性，虽然介绍的很粗略，但是解决方案和构想都已罗列。

