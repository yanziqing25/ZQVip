# ZQVIP
### 介绍
这是一个简单易用的VIP插件，目前支持本地、MySQL和SQL Server(Beta)存储使用!

注意：此插件需要前置插件—`ZQExtension.jar`
### 功能
- VIP飞行
- 自动检测VIP到期时间，自动移除过期的VIP并取消过期VIP的飞行模式
- 配合插件-`ZQSign`可设置VIP签到获得更多的金钱，食用更佳!
### 配置文件
#### "check-update"
- 插件自动更新开关，"true"或"false"
#### "allow-flight-worlds"
- 允许SVIP飞行的世界设置
### 指令
- `/addvip <string: player> [int: day]` 添加一个VIP，不填写时间参数即为永久(简写:`/av`)
- `/addsvip <string: player> [int: day]` 添加一个SVIP，不填写时间参数即为永久(简写:`/asv`)
- `/removevip <string: player>` 移除一个VIP(简写:`/rv`)
- `/fly` SVIP飞行命令
### 权限
		vip.command.addvip:
		description: "添加VIP"
		default: op
		vip.command.addsvip:
		description: "添加SVIP"
		default: op
		vip.command.removevip:
		description: "删除VIP/SVIP"
		default: op
		vip.command.svip.fly:
		description: "SVIP飞行"
		default: true