# Dropitem Plugin

[English Version Below | 中文版本在下]

---

## 🚀 Introduction | 介绍

The **Dropitem** plugin enhances the visibility of dropped items in Minecraft by displaying their names and quantities dynamically. It is optimized for both Paper and Folia servers, supporting large-scale entity management with high performance.

**Dropitem** 插件通过动态显示掉落物品的名称和数量，增强了 Minecraft 中掉落物品的可视性。它针对 Paper 和 Folia 服务器进行了优化，支持高性能的大规模实体管理。

---

## ✨ Features | 功能

- **Dynamic Item Display**: Shows item names and quantities in real-time.  
  **动态物品显示**: 实时显示物品名称和数量。
- **Density Control**: Automatically hides item names in high-density areas.  
  **密度控制**: 在高密度区域自动隐藏物品名称。
- **Multi-Version Support**: Compatible with both Paper and Folia servers.  
  **多版本支持**: 兼容 Paper 和 Folia 服务器。
- **Configurable**: Customize display settings via `config.yml`.  
  **可配置性**: 通过 `config.yml` 自定义显示设置。

---

## 📥 Installation | 安装

1. Download the latest `Dropitem.jar` from the [Releases](https://github.com/YourName/Dropitem/releases) page.  
   从 [Releases](https://github.com/YourName/Dropitem/releases) 页面下载最新的 `Dropitem.jar`。
2. Place the `Dropitem.jar` file into your server's `plugins` folder.  
   将 `Dropitem.jar` 文件放入服务器的 `plugins` 文件夹。
3. Restart your server. The plugin will generate a default `config.yml`.  
   重启服务器。插件将自动生成默认的 `config.yml`。

---

## ⚙️ Configuration | 配置

The plugin's configuration file (`config.yml`) allows you to customize its behavior:  
插件的配置文件 (`config.yml`) 允许您自定义其行为：

```yaml
# Whether to show item amounts (disabling improves performance)
# 是否显示物品数量（关闭可提升性能）
show-amount: true

# Density check radius (blocks)
# 密度检查半径（方块）
check-radius: 2.5

# Maximum number of similar items in the area
# 区域内最大同类物品数量
max-density: 6

# Check interval (seconds)
# 检查间隔（秒）
check-interval: 3