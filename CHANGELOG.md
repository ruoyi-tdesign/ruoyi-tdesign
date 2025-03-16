## 🌈 1.3.1 `2024-8-30`
> 本次更新需要执行`update_1.3.0-1.3.1.sql`脚本<br>

### 🚀 New Features
- 新增菜单自动折叠
- 新增用户校验分组并更新页码大小校验规则 新增了UserAddGroup、UserEditGroup和UserQueryGroup三个校验分组接口，用于用户相关操作的验证逻辑区分。同时，更新了BasePageQuery中的校验规则，将页码和每页大小的校验增加了UserQueryGroup分组，以便进行更细致的验证控制。
- 增加takeCacheAndSet方法并更新CacheNames缓存策略
- 新增amqp消息去重注解
- 增加图片内容识别预览组件
- 富文本预览组件支持图片懒加载
- 增加滚动条组件&详情弹窗默认使用滚动条
- 增加自动导入vue、vue-router、pinia类型
- 代码生成支持修改表名称
- 个人头像支持选择我的文件上传
- 新增敏感词功能、代码生成新增导入功能、RedisUtils工具新增ZSet by @yixiaco in [#76](https://github.com/ruoyi-tdesign/ruoyi-tdesign/pull/76)
- 新增StreamUtils工具方法
- 新增集合的两次分组后的值操作转为List和Set
### 🔥 Performance
- 新增上传组件文件名称长度限制参数
- 账号密码只在开发环境填充
- 登录消息推送，项目名称使用配置名称，而不是固定值
- 由于存在隐私泄露风险删除富文本autosave插件。（草稿存储在localStorage中，且直接关闭页面不会清理）
- 代码生成模板增加校验字符串长度限制
- 详情使用描述组件替代表单组件
- Query对象默认继承BasePageQuery而不是BaseEntity
- 动态租户设置支持重入时保持当前的租户设置
- 登录用户信息只返回用户数据
- Oss规则页面优化
- 创建租户联系电话改为非必填，校验联系电话格式
- 字典不再支持多租户操作，转为通用表 (若想继续保持字典多租户，请忽略此提交更新)
### 🐞 Bug Fixes
- 选中三级菜单会使菜单展开项错误
- 修复README单词拼写错误
- 修复了无法清空用户岗位的问题
- 修复nginx匹配location优先级过高导致正则不生效问题
- 通知中心消息多解决溢出的情况 添加固定高度 by @WWTBNBWN in [#72](https://github.com/ruoyi-tdesign/ruoyi-tdesign/pull/72)
- td描述组件新增tableLayout属性，并修改为原默认行为"auto"
- 修复个人中心手机号、邮箱为空时修改报错；修复租户联系电话前端没校验问题
- 修改接口权限代码错误问题-获取系统访问记录详细信息 by @WWTBNBWN in [#71](https://github.com/ruoyi-tdesign/ruoyi-tdesign/pull/71)
- 修复SaSecurityContext方法名错误
- 修复RedisLockUtil问题
- 修复TransactionalEventPublisher发送commit事件执行两次、beforeCommit未执行回调
- 修复MultipleLoginBaseHelper更新用户方法可能引起报错问题
- 数据库枚举类型字段导入报错
- 修复代码生成设置动态数据库没生效问题
- 修复普通角色查询菜单报错问题 by @yixiaco in [#67](https://github.com/ruoyi-tdesign/ruoyi-tdesign/pull/67)
- 修复国际化提示问题，前端切换语言时，服务端也需要切换
- 多租户忽略检验兼容分页插件写法
- powerJob表名错误问题
- 修复MapStructPlus 1.4.0版本在对象属性为List发生复制时的报错问题 by @yixiaco in [#65](https://github.com/ruoyi-tdesign/ruoyi-tdesign/pull/65)
- TaskExecutorBuilder过期的问题，使用ThreadPoolTaskExecutorBuilder替换 by @WWTBNBWN in [#69](https://github.com/ruoyi-tdesign/ruoyi-tdesign/pull/69)
### 🏡 Chore
- remove npm lock file ignore
- 补充弹窗内容最大高度样式
- 字典不再支持多租户操作，转为通用表。补充sql脚本,删除其他租户的字典与数据 (若想继续保持字典多租户，请忽略此提交更新)
- 提供JsonUtil将对象转为Map、List<Map>方法
- sys_app重命名为sys_tenant_app，目的是使表的作用更具体
- 字典列表链接到字典数据样式高亮；字典组件新增参数
- 图片预览禁止选中

### 🔨 Dependency Upgrades
#### 🔨 java pom
- Upgrade to redisson-3.32.0
#### 🔨 node package
- Upgrade to @vueuse/core@^11.0.3
- Upgrade to axios@^1.7.5
- Upgrade to dayjs@^1.11.13
- Upgrade to pinia@^2.2.2
- Upgrade to qs@^6.13.0
- Upgrade to tdesign-icons-vue-next@0.2.4
- Upgrade to tdesign-vue-next@1.9.9
- Upgrade to tinymce@^7.3.0
- Upgrade to vue-cropper@^1.1.4
- Upgrade to vue-i18n@^9.14.0
- Upgrade to vue-router@^4.4.
- Upgrade to @commitlint/cli@^19.4.1
- Upgrade to @commitlint/config-conventional@^19.4.1
- Upgrade to @types/lodash@^4.17.7
- Upgrade to @types/prismjs@^1.26.4
- Upgrade to @types/qs@^6.9.15
- Upgrade to @typescript-eslint/eslint-plugin@^7.13.0
- Upgrade to @typescript-eslint/parser@^7.13.0
- Upgrade to @vitejs/plugin-vue@^5.1.2
- Upgrade to @vitejs/plugin-vue-jsx@^4.0.1
- Upgrade to eslint-plugin-simple-import-sort@^12.1.0
- Upgrade to eslint-plugin-vue@^9.26.0
- Upgrade to postcss-html@^1.7.0
- Upgrade to prettier@^3.3.3
- Upgrade to unplugin-auto-import@^0.18.2
- Upgrade to unplugin-vue-components@^0.27.4
- Upgrade to vite@~5.4.2
- Upgrade to vite-plugin-mock@^3.0.2
- Upgrade to vue-tsc@^2.0.29
- remove @vue/compiler-sfc
## 🎉New Contributors
* @WWTBNBWN made their first contribution in [#69](https://github.com/ruoyi-tdesign/ruoyi-tdesign/pull/69)

## 🌈 1.3.0 `2024-4-13`
> 本次更新需要执行`update_1.2.0-1.3.0.sql`脚本<br>
> PowerJob需要额外更新SQL<br>

### 🚀 New Features
- amqp新增AmqpTransactionalTemplate，废弃AmqpEventPublisher (#62)
- 使用tdesign-vue-next-starter个人中心页替换旧的个人中心页 (#61)
- OssRule表增加排序字段
- 新增使用表达式动态设置菜单隐藏、停用逻辑 (#57)
- 新增集合差异比较工具
- 新增列表选中项的数量展示
- 增加多表多数据模板导出excel方法
- websocket支持多账号体系、支持自定义处理器
- 将代码迁移至TS严格模式
- 新增侧边栏颜色切换
### 🔥 Performance
- mybatis数据注入时不再强制报错
- 适配tinymce6 -> tinymce7
- @OssRule支持Number类型数据
- 更新Sms4J，新增七牛云短信发送配置
- 优化菜单显示间距问题
- 增加StreamUtils集合对象合并操作
- 优化SpEL表达式解析工具代码，修改@DynamicTenant注解的使用方式；优化消息发送参数
- 删除用户时，退出用户的登录状态
- 使用@OssRule注解替代@Translation(type = TransConstant.OSS_RULE)
- oss规则支持内容为id时自动转为url
- 个人信息中邮箱、手机号非必填
- 增加忽略脱敏注解逻辑
- 数据脱敏权限支持多个，并适配多账号类型
- AnyEnum默认不忽略大小写
- 使用还原初始值作为表单重置的默认项
- 优化websocket重连逻辑
- 使用type-only import的esLint自动修复
- 类型导入支持自动判断是否使用 type 关键字
- 优化消息配置提示
### 🐞 Bug Fixes
- 修正支持排序安全正则支持别名
- OSS规则应随OSS配置一同忽略租户；处理@OssRule字段url带参情况
- 修复当用户管理菜单设置为第一层菜单时，与个人中心菜单冲突，导致无法显示页面
- 修复单层菜单显示问题
- 修复获取默认分页值问题，默认分页值修改为10
- @RateLimiter#key使用解析器在解析#var形式的变量时异常
- 修复IgnoreTenant#cache=true时，读取keys报错（需要注意代码语义变更）
- 新增角色时的加载进度不会停止
- 修复关闭的消息模板，如果存在相同的消息常量+消息类型，则保存失败
- 路由重定向次数超过三次后跳转到500页面
- SensitiveHandler在数组中报错
- 手机号校验，空字符串导致校验不通过
- 错误的获取可空用户对象方式
- 修复image-preview组件在id模式下请求多次
- 修复ECharts环形图文字重叠的问题
- 修复路由切换时过渡动画异常的问题
- 修复EsLint问题
- 修复登录页报错
### 🏡 Chore
- 重构SortQuery
- 移除vue-dev-tools(影响debug速度)
- 增加项目文档地址
- 将整体代码规范为ESM
### 🔨 Dependency Upgrades
#### 🔨 java pom
- Upgrade to spring-boot-3.2.4
- Upgrade to springdoc-openapi-starter-webmvc-api-2.5.0
- Upgrade to easyexcel-3.3.4
- Upgrade to mybatis-plus-3.5.6
- Upgrade to pagehelper-spring-boot-starter-2.1.0
- Upgrade to hutool-5.8.27
- Upgrade to spring-boot-admin-3.2.3
- Upgrade to redisson-3.28.0
- Upgrade to lock4j-redisson-spring-boot-starter-2.2.7
- Upgrade to powerJob-4.3.9
- Upgrade to mapstruct-plus-1.4.0
- Upgrade to lombok-1.18.32
- Upgrade to aws-java-sdk-s3-1.12.700
- Upgrade to sms4j-3.2.1
#### 🔨 node package
- Upgrade to @vueuse/core@^10.9.0
- Upgrade to axios@^1.6.8
- Upgrade to echarts@5.4.3
- Upgrade to qs@^6.12.0
- Upgrade to tdesign-vue-next@1.9.3
- Upgrade to tinymce@^7.0.1
- Upgrade to vue-i18n@^9.12.0
- Upgrade to vue-router@^4.3.0
- Upgrade to @commitlint/cli@^19.2.1
- Upgrade to @commitlint/config-conventional@^19.1.0
- Upgrade to @types/lodash@^4.17.0
- Upgrade to @types/qs@^6.9.14
- Upgrade to @typescript-eslint/eslint-plugin@^7.6.0
- Upgrade to @typescript-eslint/parser@^7.6.0
- Upgrade to @vitejs/plugin-vue@^5.0.4
- Upgrade to @vue/eslint-config-typescript@~12.0.0
- Upgrade to cz-conventional-changelog@^3.3.0
- Upgrade to eslint@~8.57.0
- Upgrade to eslint-plugin-simple-import-sort@^12.0.0
- Upgrade to eslint-plugin-vue@^9.24.1
- Upgrade to eslint-plugin-vue-scoped-css@^2.8.0
- Upgrade to lint-staged@^15.2.2
- Upgrade to prettier@^3.2.5
- Upgrade to rollup-plugin-copy@^3.5.0
- Upgrade to stylelint@~16.3.1
- Upgrade to typescript@~5.4.4
- Upgrade to vite@~5.2.8
- Upgrade to vue-tsc@^2.0.13
- Remove vite-plugin-vue-devtools

## 🌈 1.2.0 `2024-2-08`
> 本次更新需要执行`update_1.1.0-1.2.0.sql`脚本<br>
> 重要更新（破坏性）：
> - 修复 session 多账号共用覆盖问题 改为 tokenSession 独立存储（需要清理redis缓存，否则会报错）
> - 将 OSS配置 改为全局模式 降低使用难度 保留sql便于用户自行扩展(常规项目用不上配置分多租户)
> - Sms4J更新版本后，配置项字段发生变更，如果使用消息管理请及时更新配置

### 🚀 New Features
- 将 OSS配置 改为全局模式 降低使用难度 保留sql便于用户自行扩展(常规项目用不上配置分多租户)
- 左侧菜单栏增加主题选择模式
- 系统菜单新增组件名称，组件缓存时不再依赖路由地址
- 菜单管理支持复制添加新菜单
- 新增每分钟执行任务事件；新增每年、月、日、时、分任务sql脚本示例
- 新增分割后匹配正则的校验器
- 代码生成支持left like与right like查询
- 新增 RedisUtils.setObjectIfExists 如果存在则设置方法
- 新增 翻译组件 用户昵称翻译实现
- 新增注解 @ApiEncrypt 用于校验接口加解密
- 新增 EncryptResponseBodyWrapper 加密响应参数包装类
- 添加excel多sheet导出
- (hook): 替换窗口大小变化监听器
### 🔥 Performance
- 优化短信提醒
- 适配新版Sms4j的api变化及新增的短信平台
- 优化vue树表展开逻辑
- 校验菜单路径是否存在；常量抽取到枚举中
- 优化详情显示效果
- mybatis注入登录用户时，使用当前用户而不是固定系统用户
- R.isOk不输出到返回值Json中
- 参数配置表的参数值类型默认修改为text
- 新增消息发送帮助类
- 代码生成支持重新加载字典类型
- 请求异常提示优化
- 优化MultipleStpLogic、MultipleStpLogicJwtForSimple方法重写使用Satoken父类
- 优化使线程动态租户与用户动态租户的界限更加清晰，并使@DynamicTenant支持可重入功能
- 优化代码生成模板，更好的支持checkbox组件与IN查询
- 增加通知关闭按钮
- 使AnyEnum可以匹配枚举属性值
- 丰富RedisUtils对List Set类型的操作
- 为 admin 模块 单独增加ratelimiter模块
- 验证码接口 增加限流配置
- SocialUtils 代码
- 删除无用异常类
- 补全三方登录校验国际化
- 开启 redisson 脚本缓存 减少网络传输
- 删除 hikaricp 官方不推荐使用的配置 jdbc4 协议自带校验方法
- 减少 PlusSaTokenDao 不必要的查询优化性能
- 使用登录用户判断是否登录 提高效率
- 重构 LoginHelper 将本地存储代码操作封装
- getTenantId 判断是否开启多租户
- Dockerfile 使用shell模式 支持环境变量传入jvm参数
- 改进代码逻辑 支持注解强制加密接口数据
- WebSocketUtils 连接关闭改为警告
- 调整加解密判断逻辑, 避免 NPE
- jvm参数调整 全面启用zgc
- 使用动态租户重构业务对租户的逻辑
- 更新用户登录信息方法忽略数据权限
- 补全三方绑定时间字段 删除无用excel注解
- 将登录记录抽取到监听器统一处理
### 🐞 Bug Fixes
- 修复stylelint校验提示
- like语句兼容oracle语法
- 修复不同路径下，相同组件互相影响的问题
- 修复打开部门详情报错问题
- 修复日期过滤条件未加入导出条件中
- 代码生成模板生成多余的日期范围变量
- 发送消息值为null时报空指针问题修复
- 修复消息发送记录表缺少字段问题
- 点击菜单无法跳转问题
- 更新依赖，修复点击菜单无法切换页面问题
- 修改oss分类错误
- 修复websocket未开启问题
- 修复WebSocket session链接列表可能为空
- echarts resize error
- 代码生成查询对象选项问题修复
- 修复区域选择body存在滚动条时选择区域偏移；优化选择区域卡顿问题；
- 修复没有权限访问自身信息问题
- 修复菜单分割下的首页点击跳转错误
- 修复 CryptoFilter 代码逻辑问题
- 修复 selectDictTypeByType 查询方法错误问题
- 修复一些不正常类无法加载报错问题
- 修复 excel合并注解会根据第一合并列的结果来决定后续的列合并
- 修复 session 多账号共用覆盖问题 改为 tokenSession 独立存储（需要清理redis缓存，否则会报错）
- 修复 powerjob部署方案 高版本nginx不生效问题
- 修复 OssFactory 并发多创建实例问题
- 修复 延迟队列在投递消息未到达时间的时候 服务死机导致重启收不到消息
- i18n:修复用户中心词条不生效的问题
- i18n:pages.result.fail.modify中文未定义
### 🏡 Chore
- chore: 修正字典名称更准确
- chore: 暂时使用单条多次发送的方式代替群发消息
- rollback 回滚错误提交, 保留加密组件开关
### 🔨 Dependency Upgrades
#### 🔨 java pom
- Upgrade to spring-boot-3.2.1
- Upgrade to mybatis-spring-boot-starter-3.0.3
- Upgrade to springdoc-openapi-starter-webmvc-api-2.3.0
- Upgrade to easyexcel-3.3.3
- Upgrade to mybatis-plus-3.5.5
- Upgrade to hutool-5.8.24
- Upgrade to spring-boot-admin-3.2.1
- Upgrade to redisson-3.25.2
- Upgrade to dynamic-datasource-spring-boot3-starter-4.3.0
- Upgrade to alibaba-ttl-2.14.5
- Upgrade to mapstruct-plus-1.3.6
- Upgrade to bouncycastle-1.77
- Upgrade to JustAuth-1.16.6
- Upgrade to aws-java-sdk-s3-1.12.633
- Upgrade to sms4j-3.1.1
#### 🔨 node package
- Add @vueuse/core@^10.7.2
- Upgrade to axios@^1.6.7
- Upgrade to pinia-plugin-persistedstate@^3.2.1
- Upgrade to tdesign-vue-next@1.8.0
- Upgrade to tinymce@^6.8.2
- Upgrade to vue@~3.3.13
- Upgrade to vue-i18n@^9.9.0
- Upgrade to @commitlint/cli@^18.6.0
- Upgrade to @commitlint/config-conventional@^18.6.0
- Upgrade to @types/crypto-js@^4.2.2
- Upgrade to @types/echarts@^4.9.22
- Upgrade to @types/qs@^6.9.11
- Upgrade to @typescript-eslint/eslint-plugin@^6.19.1
- Upgrade to @typescript-eslint/parser@^6.19.1
- Upgrade to @vitejs/plugin-vue@^5.0.3
- Upgrade to @vue/compiler-sfc@~3.3.13
- Upgrade to eslint@^8.56.0
- Upgrade to eslint-config-prettier@^9.1.0
- Upgrade to eslint-plugin-import@^2.29.1
- Upgrade to eslint-plugin-prettier@^5.1.3
- Upgrade to eslint-plugin-vue@^9.20.1
- Upgrade to eslint-plugin-vue-scoped-css@^2.7.2
- Upgrade to lint-staged@^15.2.0
- Upgrade to postcss-html@^1.6.0
- Upgrade to prettier@^3.2.4
- Upgrade to stylelint@~16.2.0
- Upgrade to stylelint-config-standard@^36.0.0
- Upgrade to stylelint-order@~6.0.4
- Upgrade to typescript@~5.3.3
- Upgrade to unplugin-auto-import@^0.17.5
- Upgrade to unplugin-vue-components@^0.26.0
- Upgrade to vite@~5.0.12
- Upgrade to vite-plugin-mock@^3.0.1
- Upgrade to vite-plugin-prismjs@^0.0.11
- Upgrade to vite-plugin-vue-devtools@1.0.0-rc.8
- Upgrade to vue-tsc@^1.8.27
- Remove vue-clipboard3

## 🌈 1.1.0 `2023-12-01`
> 本次更新需要执行`update_1.0.9-1.1.0.sql`脚本
### 🚀 New Features
- ruoyi-ui support i18n
- 超级管理员支持查看所有在线用户(#43)
- 前端接入websocket接收消息
- 新增通用job定时任务
- 字典数据新增回显风格
- 新增租户套餐与菜单关联表，删除租户套餐菜单字段
- 更新登录日志, 在线用户展示信息(增加 客户端, 设备类型)
### 🔥 Performance
- 数据权限拦截器优先判断方法是否有效 提高性能减少无用sql解析
- 适配 maxkey 新版本
- @Sensitive脱敏增加角色和权限校验
- 脱敏增加角色及权限校验
- 部门数据权限使用默认兜底方案
- 优化提示消息显示位置
- 优化在线用户显示列表
- 删除冗余参数
- online字段显示 (客户端, 设备类型)
- nginx 配置支持 websocket
- notice 新增通知公告发送ws推送
- websocket 模块减少日志输出 增加登录推送
- 重构登录策略增加扩展性降低复杂度
- AddressUtils 兼容linux系统本地ip
- 补全操作日志部门数据
- 排除powerjob无用的依赖 减少打包30M体积
- 菜单支持模糊搜索
- 租户套餐变更后，支持同步到所有租户上(#40)
- 租户、租户套餐列表查询优化
- 增强oss、oss分类安全校验
- 客户端禁用限制
- 数据权限 减少二次校验查询
- 操作日志列表新增IP地址查询
- 富文本回显使用富文本回显组件
- 使用tabs组件lazy属性替换原有懒加载逻辑
- 优化在我的文件选中分类时，上传文件到指定分类中
- 代码生成mapper.xml默认生成id字段映射
- 优化图片显示效果
- 图片选择、文件选择在数量限制小于等于1时切换为单选模式
### 🐞 Bug Fixes
- 修复 oss服务无法连接 导致业务异常问题 查询不应该影响业务
- 修复菜单标签不显示不缓存的菜单 (#42)
- 修复用户注册缺失 clientid 问题
- 修复 外链带端口出现的异常；
- 补全代码生成 columnList 接口参数注解缺失；
- 修复 ws群发重复推送问题；
- 修复 普通角色编辑使用内置管理员code越权问题
- 参数配置的xss排除未生效修复
- 文件删除需要过滤用户类型及用户id
- 删除系统菜单时，需要检查是否分配菜单给租户套餐 (#39)
- 修复AMQP安全提示问题；
- 修复校验问题；
- 修复 powerjob 使用 nginx 部署无法访问的问题
- 修复依赖问题
- 修复 新增或编辑 SysOssConfig 数据后 推送到 redis 数据不完整
- 个人信息修改密码接口隐藏新旧密码参数明文
- 上传时未选择分类时报错
- 修复我的文件分类名称编辑无效果问题
- 用户归属部门不能为空
- 修复我的文件中预览图片层级过低问题
- 修复图片上传、文件上传组件当url携带参数时，解析错误的问题
### 🏡 Chore
- vite5最低版本要求node18
- 升级package.json依赖
- tdesign-vue-next-starter release 0.9.0
- 删除 无用对象
- 更新依赖；适配Sms4j变更
- 还原默认样式
- 样式调整
- 表单label宽度计算调整
- 更新 redis 集群模式注释说明
- 更新DynamicTenant使用示例
### 🔨 Dependency Upgrades
#### 🔨 java pom
- Upgrade to spring-boot-3.1.5
- Upgrade to sa-token-1.37.0
- Upgrade to mybatis-plus-3.5.4.1
- Upgrade to pagehelper-2.0.0
- Upgrade to hutool-5.8.22
- Upgrade to okhttp-4.12.0
- Upgrade to spring-boot-admin-3.1.7
- Upgrade to redisson-3.24.3
- Upgrade to dynamic-datasource-spring-boot3-starter-4.2.0
- Upgrade to powerjob-4.3.6
- Upgrade to lombok-1.18.30
- Upgrade to aws-java-sdk-s3-1.12.581
- Upgrade to sms4j-spring-boot-starter-3.0.3
- Add fastjson-1.2.83
- Upgrade to maven-surefire-plugin-3.2.1
- Upgrade to flatten-maven-plugin-1.5.0
- Remove snakeyaml
#### 🔨 node package
- Upgrade to axios@^1.6.2
- Upgrade to crypto-js@^4.2.0
- Upgrade to dayjs@^1.11.10
- Upgrade to fuse.js@^7.0.0
- Upgrade to pinia@^2.1.7
- Upgrade to tdesign-vue-next@1.7.0
- Upgrade to tinymce@^6.8.1
- Upgrade to vue@^3.3.9
- Upgrade to vue-i18n@^9.8.0
- Upgrade to vue-router@^4.2.
- Upgrade to @commitlint/cli@^18.4.3
- Upgrade to @commitlint/config-conventional@^18.4.3
- Upgrade to @types/crypto-js@^4.2.1
- Upgrade to @types/js-cookie@^3.0.6
- Upgrade to @types/lodash@^4.14.202
- Upgrade to @types/nprogress@^0.2.3
- Upgrade to @types/prismjs@^1.26.3
- Upgrade to @types/qs@^6.9.10
- Upgrade to @typescript-eslint/eslint-plugin@^6.13.1
- Upgrade to @typescript-eslint/parser@^6.13.1
- Upgrade to @vitejs/plugin-vue@^4.5.0
- Upgrade to @vitejs/plugin-vue-jsx@^3.1.0
- Upgrade to @vue/compiler-sfc@^3.3.9
- Upgrade to eslint@^8.54.0
- Upgrade to eslint-plugin-import@^2.29.0
- Upgrade to eslint-plugin-prettier@^5.0.1
- Upgrade to eslint-plugin-vue@^9.19.2
- Upgrade to eslint-plugin-vue-scoped-css@^2.5.1
- Upgrade to lint-staged@^15.1.0
- Upgrade to prettier@^3.1.0
- Upgrade to stylelint@~15.11.0
- Upgrade to typescript@~5.3.2
- Upgrade to unplugin-auto-import@^0.17.1
- Upgrade to vite@^5.0.4
- Upgrade to vite-plugin-vue-devtools@^1.0.0-rc.5
- Upgrade to vite-svg-loader@^5.1.0
- Upgrade to vue-tsc@^1.8.2

## 🌈 1.0.9 `2023-10-07`
> 本次更新需要执行`update_1.0.8-1.0.9.sql`脚本<br>
> 包含可能有破坏性的脚本且更新时需要删除所有缓存
### 🚀 New Features
- 新增角色、菜单、部门、岗位、字典类型、字典数据、通知公告、登录日志、OSS配置管理 详情
- 富文本组件新增setup参数
- 增加基础分页查询对象

### 🔥 Performance
- 文件上传直接上传不需要中转
- dict-tag组件默认转为string进行匹配
- 优化弹窗表单验证效果
- 编辑弹窗关闭时销毁，防止校验规则复用
- 表单不再提供触发类型
- 优化页面显示效果；代码生成导入时防止重复点击
- 代码生成到地址时，不过滤前端文件
- 升级tinymce版本
- 优化代码生成编辑页效果
- 动态租户注解增加读取租户是否是必须的
- 优化类型提示

### 🐞 Bug Fixes
- 查询语法应该兼容通用sql
- 修复类型错误提示
- 面包屑组件缓存问题
- 修复oss分类查询报错问题
- 修复路由重定向超过3层时地址错误
- 解决eslint解析tsx错误问题
- 获取用户对象返回null而不是直接抛出异常
- 修复面包屑点击无跳转、动态路由显示路径问题
- 修复部门树过滤失效问题
- 修复消息模板的标题字段没有变量响应提示
- 修复模板问题
- 修复个人中心打开报错问题
- 发送MQ消息时,TenantMQMessage对象未序列化报错
- 修复区域选择滚动时选中项被取消的问题
- 代码生成模板格式错乱问题

### 🏡 Chore
- 调整代码生成模板
- 隐藏多余的系统参数配置
- 删除多余的缓存查询
- 同步ruoyi-vue-plus代码（包含可能有破坏性的脚本且更新时需要删除所有缓存）
- 同步tdesign starter
- 缓存前缀默认使用应用名称
- 个人中心图片上传更换编辑icon、旋转更改为向左

### 🔨 Dependency Upgrades
#### 🔨 java pom
- Upgrade to spring-boot-dependencies-3.1.3
- Upgrade to spring-boot-configuration-processor-3.1.3
- Upgrade to spring-boot-admin-starter-server-3.1.5
- Upgrade to spring-boot-admin-starter-client-3.1.5
- Upgrade to redisson-spring-boot-starter-3.23.4
- Upgrade to aws-java-sdk-s3-1.12.540
#### 🔨 node package
- Upgrade to @tinymce/tinymce-vue@^5.1.1
- Upgrade to axios@^1.5.0
- Upgrade to tdesign-vue-next@1.5.6
- Upgrade to tinymce@^6.7.0
- Upgrade to vue-cropper@^1.1.1
- Upgrade to @types/crypto-js@^4.1.2
- Upgrade to @types/js-cookie@^3.0.4
- Upgrade to @types/lodash@^4.14.198
- Upgrade to @types/qs@^6.9.8
- Upgrade to @typescript-eslint/eslint-plugin@^6.7.0
- Upgrade to @typescript-eslint/parser@^6.7.0
- Upgrade to @vitejs/plugin-vue@^4.3.4
- Upgrade to @vue/eslint-config-typescript@^12.0.0
- Upgrade to eslint@^8.49.0
- Upgrade to lint-staged@^14.0.1
- Upgrade to typescript@~5.2.2
- Upgrade to unplugin-vue-components@^0.25.2
- Upgrade to vue-tsc@^1.8.11 


## 🌈 1.0.8 `2023-08-28`
> 本次更新需要执行`update_1.0.7-1.0.8.sql`脚本<br>
> sys_oss如果存在数据，则应该设置user_type为对应的用户类型
### 🚀 New Features
- 新增文件分类功能
- 新增我的文件管理功能
- 新增鼠标选择区域组件
- 新增图片、文件上传组件适配我的文件选择功能
- 富文本组件上传支持我的文件选择功能
- 文件选择器支持accpet属性
- 使用import type导入类型，而不是直接引用
- 使用vue3.3新特性defineOptions定义组件名称
- 代码生成支持实时预览;新增编辑页同步表结构;新增生成方法（新增、编辑、修改、删除、导出、详情、查询）;新增生成对象（controller、Vue、Sql）；新增菜单icon选择
- 菜单列表新增显示状态
- RedisLockUtil新增加锁执行方法

### 🔥 Performance
- 升级适配tdesign-vue-next 1.5.2版本
- 优化用户管理部门树显示效果
- tdesign-vue-next恢复按需引入
- 代码生成查询对象优先使用bo而不是domain
- 代码生成优化、树结构类型补全
- 优化MapstructUtils类，使IDEA能够正常提示空指针异常
- 代码生成编辑默认上级菜单为根目录

### 🐞 Bug Fixes
- 修复测试菜单id重复问题及状态值问题
- 修复弹出层遮盖问题
- 修复文件大小为0不显示文件大小的问题
- 修复隐藏菜单没有效果
- tree结构导入文件修复
- 修复代码生成列表翻页未生效问题
- 修复同名组件不同路径的标签缓存问题
- 修复系统配置中数据库如果不存在数据时的报错问题
- 修复字典数据编辑按钮打开报错
- 修复了表格空数据字样不居中问题
- 修复vue文件生成多余的对象属性
- 修复ruoyi-ui全局函数未提示问题
- 用户管理过滤部门数据时，允许节点折叠

### 🏡 Chore
<details><summary>同步RuoYi-Vue-Plus项目</summary>

* fix:角色权限支持仅本人权限查看，解决无法查看自己创建的角色问题
* fix: 修复可能导致异常类无法反序列化问题
* docs: 错误的方法命名
* perf: 优化 CryptoFilter null判断工具
* update 优化 websocket 路径与 cloud 版本保持一致
* update 更新登录策略返回值
* update 修改验证码路径
* fix：修复客户端编辑时授权类型变更未保存的问题
* update 删除一些跟swagger有关的字眼 避免误解
* perf: 更新依赖
* style: 样式优化
* update 优化 过期的 Security 方法
* perf: 优化 excel 导出字典默认转为下拉框
* perf: 优化 excel 导出字典下拉选项下标错误问题
* perf: 优化 excel 导出问题
* fix 修复 三方绑定 实体类copy覆盖问题
</details> 

### 🔨 Dependency Upgrades
#### 🔨 java pom
- Upgrade to spring-boot-dependencies-3.1.2
- Upgrade to mybatis-spring-boot-starter 3.0.2
- Upgrade to springdoc-openapi 2.2.0
- Upgrade to easyexcel 3.3.2
- Upgrade to mybatis-plus 3.5.3.2
- Upgrade to pagehelper 1.4.7
- Upgrade to hutool 5.8.21
- Upgrade to okhttp 4.11.0
- Upgrade to spring-boot-admin 3.1.4
- Upgrade to redisson 3.23.2
- Upgrade to lock4j 2.2.5
- Upgrade to dynamic-datasource 4.1.3
- Upgrade to transmittable-thread-local 2.14.3
- Upgrade to lombok 1.18.28
- Upgrade to bcprov-jdk15to18 1.76
- Upgrade to aws-java-sdk-s3 1.12.528
- Upgrade to maven-surefire-plugin 3.1.2
#### 🔨 node package
- Upgrade to pinia@^2.1.6
- Upgrade to qrcode.vue@^3.4.1
- Upgrade to tdesign-icons-vue-next@0.2.2
- Upgrade to tdesign-vue-next@1.5.2
- Upgrade to tinymce@^6.6.1
- Upgrade to @commitlint/cli@^17.7.1
- Upgrade to @commitlint/config-conventional@^17.7.0
- Upgrade to @types/lodash@^4.14.197
- Upgrade to @typescript-eslint/eslint-plugin@^6.4.1
- Upgrade to @typescript-eslint/parser@^6.4.1
- Upgrade to @vitejs/plugin-vue@^4.3.3
- Upgrade to @vitejs/plugin-vue-jsx@^3.0.2
- Upgrade to eslint@^8.47.0
- Upgrade to eslint-config-prettier@^9.0.0
- Upgrade to eslint-plugin-import@^2.28.1
- Upgrade to eslint-plugin-vue@^9.17.0
- Upgrade to less@^4.2.0
- Upgrade to vite@^4.4.9
- Upgrade to vue-tsc@^1.8.8

## 🌈 1.0.7 `2023-07-30`
> 本次更新需要执行`update_1.0.6-1.0.7.sql`脚本
### 🚀 New Features
- 系统设置新增使用map直接修改参数；使用便捷方式修改系统参数，拆分租户参数与全局参数设置
- 优化消息模块抽取为新模块，新增耗时字段；从缓存中读取消息模板、消息配置
- 新增动态租户注解，与原有动态租户分开存储，优先级更高，不支持嵌套使用
- 提供租户消息的MQ基础消息对象，方便在使用消息队列时与动态租户注解组合
### 🔥 Performance
- 设置滚动条最小高度；富文本预览新增代码块最大高度

### 🐞 Bug Fixes
- 修复根路由为菜单类型时，不显示或显示错误问题
- 修复在不启用忽略租户方法中，不执行回调
- 修复后台权限匹配路径缺少`gen/`问题
- 修复依赖`ruoyi-common-amqp`报错问题
- 修复判断消息内容是否是HTML的正则不够准确
- 修复权限未匹配命中url时，请求资源被放行
- 修复消息发送时，值对象不是String类型时，不会替换变量引起的报错问题
- 修复在非web环境下构建PageQuery请求对象时报错
- 修复富文本弹出层z-index小于弹窗的问题
- 修复菜单搜索时，偶发性无法跳转问题
- 修复下载文件时，加载插件不关闭问题

### 🏡 Chore
- 更新gitee客户端id
- 优化字典类型链接显示效果
- 修改config遗留注释

### 🔨 Dependency Upgrades
- Upgrade to pinia-plugin-persistedstate@^3.2.0
- Upgrade to tdesign-vue-next@^1.4.1
- Upgrade to @commitlint/cli@^17.6.7
- Upgrade to @commitlint/config-conventional@^17.6.7
- Upgrade to @types/lodash@^4.14.196
- Upgrade to @typescript-eslint/eslint-plugin@^6.2.0
- Upgrade to @typescript-eslint/parser@^6.2.0
- Upgrade to eslint@^8.45.0
- Upgrade to stylelint@~15.10.2
- Upgrade to vite@^4.4.7
- Upgrade to vue-tsc@^1.8.6

## 🌈 1.0.6 `2023-07-20`
### 🚀 New Features
- 富文本编辑器使用TinyMCE替换CKEditor
- 升级tdesign-vue-next-starter版本为0.8.0
- 代码高亮使用Prismjs替代highlightjs，并支持显示行号
- 新增代码预览组件
- 新增富文本预览组件
- number-leading-zero deprecated
- 移除Stylelint-less依赖
- 加载中提示默认修改为30秒
- 绑定平台账号时检验是否已绑定其他用户
- 隐藏使用的clientId被误删、停用

### 🐞 Bug Fixes
- 修复根据用户查询菜单时的问题
- 校验客户端Key防止重复导致查询时因ClientId重复报错
- 修复prettier与eslint-plugin-prettier插件版本不兼容问题
- 登录超时时间设置问题
- 修复在多租户与不启用租户下，token到期时无法清理在线用户状态，设置动态租户在web环境未登录时不生效问题

### 🔨 Dependency Upgrades
- Upgrade to tdesign-icons-vue-next ^0.1.12
- Upgrade to tdesign-vue-next ^1.3.12
- Upgrade to @tinymce/tinymce-vue ^5.1.0
- Upgrade to prismjs ^1.29.0
- Upgrade to tinymce ^6.6.0
- Upgrade to @types/prismjs ^1.26.0
- Upgrade to eslint-plugin-prettier ^5.0.0
- Upgrade to vite-plugin-prismjs ^0.0.8
- Upgrade to vue-tsc ^1.8.5
- Upgrade to stylelint-config-standard ^34.0.0

## 🌈 1.0.5 `2023-07-14`
### 🚀 Features
- 使用PowerJob替换XXLJob
- 新增客户端管理
- 新增第三方登录
- 新增请求加密传输

### 🐞 Bug Fixes
- 修复代码生成中的问题
- 修复表格ui宽度不够时不能滚动问题
- 修复在多租户下，token到期时无法清理在线用户状态

### 🔨 Dependency Upgrades
- Upgrade to tdesign-vue-next ^1.3.11
- Upgrade to vue-router ~4.2.4
- Upgrade to @typescript-eslint/eslint-plugin ^6.0.0
- Upgrade to @typescript-eslint/parser ^6.0.0
- Upgrade to prettier ^3.0.0
- Upgrade to stylelint-less 1.0.8
- Upgrade to vite ^4.4.4
- Upgrade to vite-plugin-vue-devtools ^0.5.1
- Upgrade to Upgrade to easyexcel 3.3.1
- Upgrade to satoken 1.35.0.RC
- Upgrade to dynamic-ds 4.1.1

## 🌈 1.0.4 `2023-07-11`
### 🚀 Features
- fix: 修复菜单搜索浮层样式问题
- chore: release 0.7.7
- build(deps-dev): bump typescript from 4.9.5 to 5.1.6
- feat: 更新ruoyi-ui所有依赖
- refactor: 重构Axios工具，新增接口级防抖节流
- feat: 增加菜单搜索功能 (#10)
- feat: 代码生成新增排序字段生成 (#8)
- feat：更新依赖、highlightjs修改为本地组件
- feat：新增消息模板标题属性变量渲染
- feat 新增OSS配置create_bucket字段，在使用配置时使用创建桶
- update 提供存在数据值时的sql更新脚本
- style: 修改面包屑分隔符
### 🔥 Performance
- 优化代码生成编辑生成配置效果
### 🐞 Bug Fixes
- 修复新建配置时报错
- 修复导入数据表列表时的偶发性错误
- 修复字段排序不生效问题
- 修复highlightjs打包后无高亮效果
- redis读写锁释放读锁时，判断是否已经释放，避免锁重入时被提前释放
- OSS文件管理列表分页问题修复
- 刷新配置缓存时，未清理全局配置；变更OSS预览时失败修复
- ckeditor列表样式问题
- 修改消息模板内容太长时报错
- 修改提取变量正则不确认问题
- 修复变量属性无序问题，改为使用数组方式