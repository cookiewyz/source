
                      超星学术视频  说明文档

1，定制程序（只有本地视频和链接外网）
	1> 在com.chaoxing.util.AppConfig中修改 ORDER_ONLINE_VIDEO = true, 非定制程序为 false。
	2> 在资源文件strings.xml中 url_online_video 字段修改。
	3> 程序名称：在资源文件strings.xml中 app_name字段修改。
	4> logo：在manifest.xml文件中application页中指定程序icon图标.
	5> 定制程序使用通用欢迎图片all_wellcome_cover_common.png。
	6> 暂未禁用自动更新功能(ORDER_ONLINE_VIDEO==true)。
	7> 发布时需要把AndroidManifest.xml中包名修改为定制客户的名称(最后在副本中修改)。
	8> layout中与包名相关的都需要修改(最后在副本中修改)。
	   pagecontrolbar.xml和tabcontrolbar.xml中。
	9> strings.xml中所有“超星学术”替换为“定制用户名称”，如：“超星学术”替换为“法源”。
	10>修改logocfg.ini中logo默认路径

2。桌面小固件
	1> 名称和logo：在sdcard/ssvido文件夹的配置文件(logocfg.ini)中指定名称和logo图片路径。
	文件结构如下：
	[logo]
	imgPath=...
	[title]
	imgPath=...
	2> 小固件的默认6个视频：在sdcard/ssvido文件夹的配置文件(commend_video_list.ini)中指定6个视频名称、缩略图、视频的路径。
	文件结构如下：
	[commendVideo1..6]
	imgPath=...
	videoName=...
	videoPath=...

3.内置本地视频
	在access数据库中编辑好要内置的视频名称、缩略图、视频文件等字段，然后转换成sqlite数据库放入sdcard/ssvido/database/文件夹。
	数据表字段见ssvideo.db

---------------------------------
修改历史：

2012年12月21日-修改人：xin
增加法源用户验证及登录功能
一次登录成功时保存用户名密码，下次自动登录

2012年11月15日-修改人：xin
创建说明文件，增加关于视频的说明
