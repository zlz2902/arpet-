# AR 放置功能测试步骤

本文说明如何在真机上验证当前工程中的 **AR 平面检测 + 加载 GLB + 点击放置** 流程。

---

## 1. 准备环境

- 安装 **Android Studio**（建议较新版本，自带 Gradle，无需单独配置命令行 Gradle 亦可开发）。
- 准备一台 **支持 ARCore 的 Android 真机**（或支持的平板）。  
  - 请在官方列表中确认机型：[ARCore 支持设备](https://developers.google.com/ar/devices)。  
  - **优先使用真机**；模拟器对摄像头的支持有限，不适合作为 AR 功能的主测试手段。
- 确保设备可通过 **Google Play** 使用 **面向 AR 的 Google Play 服务**（常见名称：**Google Play Services for AR**）。若商店可更新，建议更新到较新版本。

---

## 2. 荣耀 X50 GT / 国行荣耀为何往往跑不了本应用

本工程的 Android 端使用 **Google ARCore**（`AndroidManifest` 里 `com.google.ar.core`，SceneView 也依赖它）。要稳定测试，一般需要同时满足：

1. 机型在 Google **[ARCore 支持设备](https://developers.google.com/ar/devices)** 列表里能搜到你的**完整型号**（未列入 = 未通过谷歌认证，官方不提供保证）。
2. 设备上能安装并使用 **Google Play Services for AR**（在 Google Play 搜索该应用；若显示 **与设备不兼容**，则 ARCore 无法作为官方支持路径使用）。

**荣耀 X50 GT** 多数情况下 **不在** ARCore 官方列表中；国行荣耀也常 **没有完整谷歌服务**，即便手动安装相关组件，ARCore 仍可能 **无法初始化**，表现为：**一进 AR 就闪退、黑屏、会话创建失败**——这是 **当前技术选型（ARCore）与机型不匹配**，按上文「准备环境」换机即可验证，不是文档步骤漏了某一步。

### 你可以怎么做

| 做法 | 说明 |
|------|------|
| 借一台认证机 | 向朋友借列表内机型（如部分小米国际版、三星、Pixel 等），专门用来跑本仓库的 Android AR。 |
| 买二手调试机 | 买前先在 ARCore 列表里核对型号，避免再次买到不支持 ARCore 的机器。 |
| 用 Web 页先试效果 | 仓库根目录 **`index.html`** 走浏览器 **WebAR（A-Frame + AR.js）**，**不依赖 ARCore**，可在手机浏览器里开（需 **HTTPS** 或 **localhost**）。体验与原生应用不同，但能部分感受「摄像头画面里叠 3D」。 |
| 坚持只用荣耀国行 | 需改用 **华为 AREngine 等国产 AR SDK**，与现有 ARCore 代码 **不是同一套**，要单独做适配与重构，工作量较大。 |

---

## 3. 打开工程并运行到手机

1. 启动 **Android Studio**，选择 **Open**，打开本仓库根目录（包含 `settings.gradle`、`app` 模块的文件夹）。
2. 等待 **Gradle Sync（同步）** 完成。若失败，查看 **Build** 窗口报错；多为网络无法访问 Google Maven，需调整网络或代理。
3. 在手机上开启 **开发者选项**，并打开 **USB 调试**。
4. 用数据线连接电脑与手机；手机上同意 **允许 USB 调试**（可勾选「始终允许」）。
5. 在 Android Studio 顶部 **运行设备下拉列表** 中选择你的手机。
6. 点击工具栏绿色 **Run（运行）**（或菜单 **Run → Run ‘app’**），等待编译、安装完成。

首次运行会安装 Debug 版应用，应用名为资源中的 **「AR宠物小精灵」**（以 `strings.xml` 为准）。

---

## 4. 在手机上操作验证

1. 打开刚安装的应用，在系统弹窗中授予 **相机** 权限。
2. **缓慢平移手机**，让摄像头扫过地面或桌面，便于 ARCore 建立平面（环境光线适中、表面有一定纹理更容易识别）。
3. 观察顶部状态文字：
   - 若出现 **「模型加载完成，点击地面放置！」**（或你在 `PetExperienceConfig` 中配置的就绪文案），说明 **`assets` 中的 GLB**（默认 `sample_pet.glb`）已加载成功。
   - 若出现 **「模型加载失败: …」**，请检查 `app/src/main/assets/` 下是否存在与配置一致的 `.glb` 文件名。
4. **用手指点击**已识别平面上的位置（具体视觉反馈取决于 SceneView / 设备表现）。
5. 预期结果：3D 模型锚定在该平面位置；若代码中保留 Toast，可出现 **「小精灵出现了！」** 一类提示。

---

## 5. 常见问题排查

| 现象 | 建议处理 |
|------|----------|
| Android Studio 设备列表中没有手机 | 更换数据线/USB 口；安装手机厂商 USB 驱动；手机上重新确认 USB 调试授权；传输模式改为「文件传输」等。 |
| 安装后立刻闪退 | 机型可能不在 ARCore 支持列表，或未安装/无法更新 **Google Play Services for AR**；换官方支持机型再试。 |
| 长时间无法放置、找不到平面 | 改善光照；对准纹理更明显的桌面/地板；缓慢多角度移动设备；避免纯反光或纯黑表面。 |
| Gradle 同步或编译失败 | 确认网络可访问 Google Maven；查看 Build 日志中的具体依赖错误；必要时配置 HTTP 代理。 |

---

## 6. 与代码的对应关系（便于自查）

| 环节 | 主要位置 |
|------|----------|
| 默认模型文件名 | `PetExperienceConfig.defaultConfig()` → `sample_pet.glb` |
| 模型文件目录 | `app/src/main/assets/` |
| 平面点击放置 | `PetAnchorPresenter.placeFromHit`；监听在 `MainActivity` 中通过 `ArSessionFacade` 注册 |
| ARCore 要求 | `AndroidManifest.xml` 中 `camera` 权限、`camera.ar` 特性、`com.google.ar.core` meta-data |

---

## 7. 可选：命令行构建

若已为工程生成 **Gradle Wrapper**（存在 `gradlew.bat`），可在项目根目录执行：

```bat
gradlew.bat assembleDebug
```

生成的 APK 路径一般为：`app\build\outputs\apk\debug\app-debug.apk`。  
**当前仓库若未包含 Wrapper，仅用 Android Studio 打开同步即可，不必强求命令行。**

---

## 8. WebAR（`index.html`）：小黄鸭模型与荣耀手机试用

### 是不是同一只「小黄鸭」？

**是同一套官方示例模型。** Android 里的 `sample_pet.glb` 与网页里的模型都来自 Khronos **Duck** 示例（仓库根目录 [`index.html`](../index.html) 里通过 URL 加载 `Duck.glb`）。  
你在网页里进入 AR 后看到的 3D，就是这只小鸭（与原生应用占位模型一致；网页当前四个精灵按钮切换的也是同一只鸭，仅占位）。

### 为何不能手机里直接双击打开 HTML？

浏览器只允许在 **安全上下文** 里访问摄像头：**HTTPS**，或 **`localhost` / `127.0.0.1`**。  
直接 `file://` 打开或普通 `http://局域网IP` 访问，**摄像头常被拦截**，AR 起不来。

### 推荐做法 A：USB 连着电脑（你已会开 USB 调试时最省事）

在电脑上先把静态页跑起来，再用 **adb 反向端口**，让手机用 **`127.0.0.1`** 访问（算本地安全上下文，一般可拍照）。

1. 电脑安装 Node.js 后，在项目根目录（`index.html` 所在文件夹）执行：

   ```bat
   npx --yes http-server . -p 8080
   ```

2. 手机用数据线连电脑，开启 USB 调试；在电脑终端执行：

   ```bat
   adb reverse tcp:8080 tcp:8080
   ```

3. 在手机 **Chrome** 地址栏输入：`http://127.0.0.1:8080/index.html`  
4. 允许摄像头；点「开始 AR 体验」或选卡片里的小精灵，即可在画面里看到 **3D 小鸭**（可旋转的那只）。

### 推荐做法 B：同一 Wi-Fi + HTTPS（不适配 USB 时）

局域网 IP 用纯 HTTP 打开时，多数手机浏览器仍不给摄像头。可任选其一：

- 使用 **ngrok**、**Cloudflare Tunnel** 等把本机端口映射成 **HTTPS 域名**，在手机浏览器打开生成的链接；
- 或使用带 **自签名 HTTPS** 的静态服务器（首次需在手机上点「高级 → 继续访问」信任证书）。

具体命令随工具而变，选定工具后按其文档暴露 `8080`（或你实际端口）即可。

### 电脑本机先试（不上手机）

同样在项目根执行 `npx http-server . -p 8080`，在电脑浏览器打开 `http://localhost:8080/index.html`，用 **摄像头** 试 AR（便于确认网络与模型能加载）。

---

*文档随工程迭代维护：若默认模型名或包结构变更，请同步更新「与代码的对应关系」一节表格。*
