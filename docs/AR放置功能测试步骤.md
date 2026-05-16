# AR 宠物小精灵 — 真机测试步骤

本文说明如何在 **Android**、**iPhone** 上测试本仓库应用，以及如何用 **浏览器** 做跨平台演示。

> **当前实现说明**（与早期 ARCore 原生版不同）  
> - **Android / iOS App**：内置网页（主页 → 点「开始 AR 体验」→ 摄像头 + 宠物叠加），**不依赖 Google ARCore**。  
> - **荣耀 / 小米等国行安卓机**一般可直接安装 Android 版测试（需相机权限 + 网络加载主页样式）。  
> - **iPhone 没有 APK**，须在 **Mac + Xcode** 编译安装；包名与 Android 分开，互不影响。

---

## 一、工程结构速查

| 平台 | 目录 / 入口 | 安装包 | 开发工具 |
|------|-------------|--------|----------|
| **Android** | `app/` | `.apk` | Android Studio |
| **iPhone / iPad** | `ios/` | 无 APK；Xcode 安装到真机 | **Mac + Xcode** |
| **浏览器（通用）** | 根目录 `index.html` | 无需安装 | 任意手机浏览器 |

更详细的 iOS 说明见：[ios/README.md](../ios/README.md)

---

## 二、Android 真机测试（小米 / 荣耀 / OPPO 等）

### 2.1 准备环境

1. 安装 **Android Studio**（建议较新版本）。
2. 准备一台 **Android 真机**（模拟器摄像头支持差，**不推荐**测 AR）。
3. 手机开启 **开发者选项** → **USB 调试**。
4. 用数据线连接电脑，在手机上点 **允许 USB 调试**。

> **说明**：当前 Android 版**不需要**安装「Google Play Services for AR」。国行荣耀、小米等均可尝试安装本 App。

### 2.2 打开工程并安装到手机

1. 启动 **Android Studio** → **Open** → 选择本仓库根目录（含 `settings.gradle`、`app` 文件夹）。
2. 等待 **Gradle Sync** 完成。若失败，多为网络无法访问 Maven，可配置代理或见项目根 `README.md`。
3. 顶部设备列表选择你的手机。
4. 点击绿色 **Run（▶）**，等待编译、安装完成。

应用名称一般为 **「AR宠物小精灵」**。

### 2.3 在手机上操作验证

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 首次打开 App | 先出现 **红蓝渐变启动页**（很快），再进入 **营销主页**（特色功能、热门小精灵等） |
| 2 | 浏览主页 | 可上下滑动；**不要**一打开就是摄像头 |
| 3 | 点击 **「开始 AR 体验」** | 系统可能弹出 **相机权限** → 点 **允许** |
| 4 | 进入 AR 页 | 出现 **摄像头实时画面**，画面中有 **🐤 宠物叠加**（演示模式） |
| 5 | 点右上角 **×** | **返回主页**（不退出 App） |
| 6 | 再按一次手机 **返回键** | 在主页时退出 App |

### 2.4 Android 常见问题

| 现象 | 建议处理 |
|------|----------|
| 安装失败 `INSTALL_FAILED_ABORTED` | 手机上安装弹窗点 **允许**；开启 USB 安装；应用信息里允许相机权限 |
| 打开后长时间白屏 | 重新 Run 安装最新版；确认网络可用（主页 Tailwind 等样式需联网）；看顶部是否有错误提示 |
| 一点「开始 AR 体验」就黑屏/白屏 | 到 **应用信息 → 权限 → 相机** 设为「仅使用期间允许」；完全退出 App 再开 |
| 只有 ×、没有摄像头 | 未授予相机权限；或 WebView 异常，可更新 **Android System WebView** / Chrome |
| Gradle 编译报错 `webkit` / `compileSdk` | 使用项目已配置的 `androidx.webkit:1.6.1`；Sync 后 **Rebuild** |

### 2.5 Android 相关文件位置

| 内容 | 路径 |
|------|------|
| 原生壳（WebView） | `app/src/main/java/com/example/arpet/MainActivity.java` |
| 主页网页 | `app/src/main/assets/www/index.html` |
| AR 摄像头页 | `app/src/main/assets/www/ar_mobile.html` |
| 权限声明 | `app/src/main/AndroidManifest.xml` |

---

## 三、iPhone / iPad 真机测试（苹果）

### 3.1 重要说明

- **苹果没有 APK**，不能把 Android 的 `app-debug.apk` 装到 iPhone。
- iOS 工程在 **`ios/`** 目录，与 **`app/`（Android）完全分开**，改一边不会影响另一边。
- 编译 iOS **必须使用 Mac + Xcode**（Windows 本机无法直接打 iOS 包）。

### 3.2 准备环境

1. 一台 **Mac**（macOS）。
2. 从 App Store 安装 **Xcode**。
3. 一个 **Apple ID**（个人免费账号即可真机调试）。
4. **iPhone / iPad** + 数据线。

### 3.3 打开工程并安装到手机

1. 在 Mac 上进入仓库，双击打开：  
   **`ios/ARPet.xcodeproj`**
2. 在 Xcode 左侧选中工程 **ARPet** → **Signing & Capabilities**：
   - 勾选 **Automatically manage signing**
   - **Team** 选择你的 Apple ID 团队
3. 顶部运行目标选你的 **iPhone / iPad**（不要选模拟器测摄像头）。
4. 点击 **Run（▶）**，首次可能在手机上：**设置 → 通用 → VPN 与设备管理** 中信任开发者。
5. 安装完成后，桌面出现 **「AR宠物小精灵」**。

详细说明与资源同步命令见：[ios/README.md](../ios/README.md)

### 3.4 在 iPhone 上操作验证

流程与 Android **一致**：

| 步骤 | 操作 | 预期结果 |
|------|------|----------|
| 1 | 打开 App | 进入 **主页**（与 Android 同款网页） |
| 2 | 点击 **「开始 AR 体验」** | 弹出 **相机权限** → 点 **允许** |
| 3 | AR 页 | 摄像头画面 + **🐤 宠物叠加** |
| 4 | 点 **×** | 返回主页 |

### 3.5 iOS 常见问题

| 现象 | 建议处理 |
|------|----------|
| Signing 报错 | Xcode 登录 Apple ID；选对 Team；Bundle ID 保持 `com.example.arpet.ios` |
| 无法真机运行 | 必须用 **真机**；模拟器不适合测摄像头 AR |
| 相机权限被拒 | **设置 → AR宠物小精灵 → 相机** 打开 |
| 主页样式错乱 / 加载慢 | 主页部分 CDN 需联网；可连 Wi-Fi 后重试 |
| 修改网页后 iOS 没变化 | 把 `app/src/main/assets/www/` 复制到 `ios/ARPet/Resources/www/` 后重新 Run |

### 3.6 iOS 相关文件位置

| 内容 | 路径 |
|------|------|
| Xcode 工程 | `ios/ARPet.xcodeproj` |
| Swift 代码 | `ios/ARPet/*.swift` |
| 内置网页 | `ios/ARPet/Resources/www/` |
| 相机权限文案 | `ios/ARPet/Info.plist` → `NSCameraUsageDescription` |

---

## 四、浏览器测试（安卓 + 苹果通用）

适合：**没有 Mac 却要测 iPhone**、或快速给多人演示。

### 4.1 限制

- 须在 **安全上下文** 下才能用摄像头：**HTTPS**，或 **`localhost` / `127.0.0.1`**。
- 不能直接双击手机里的 `index.html`（`file://` 往往无法打开相机）。

### 4.2 推荐：电脑起服务 + 手机 USB 访问（Android 已开 USB 调试时）

1. 电脑在项目根目录执行：

   ```bat
   npx --yes http-server . -p 8080
   ```

2. 手机连电脑，执行：

   ```bat
   adb reverse tcp:8080 tcp:8080
   ```

3. 手机浏览器（Chrome / Safari）打开：  
   `http://127.0.0.1:8080/index.html`
4. 允许相机 → 点 **「开始 AR 体验」**。

### 4.3 同一 Wi-Fi + HTTPS

局域网 `http://电脑IP:8080` 在多数手机上**仍无法**使用摄像头。需用 **ngrok**、**Cloudflare Tunnel** 等生成 **HTTPS** 链接后再用手机打开。

### 4.4 网页与 App 的关系

- 根目录 **`index.html`** 与 App 内主页为同一套页面逻辑。
- 在 **App 内**打开时会自动跳过 2 秒加载动画，并跳转到独立 AR 页 `ar_mobile.html`。
- 模型说明：占位资源为 Khronos **Duck** 示例（小鸭），与 `sample_pet.glb` 同类占位。

---

## 五、三端对比小结

| 项目 | Android App | iOS App | 浏览器 |
|------|-------------|---------|--------|
| 是否需要 ARCore | 否 | 否 | 否 |
| 国行荣耀 / 小米 | 一般可装 APK | iPhone 用 Xcode 装 | 可以（需 HTTPS/localhost） |
| 先主页再 AR | 是 | 是 | 是 |
| 安装包 | `.apk` | Xcode 安装 | 无 |

---

## 六、可选：命令行构建 Android APK

若已有 Gradle Wrapper（`gradlew.bat`），在项目根目录：

```bat
gradlew.bat assembleDebug
```

输出：`app\build\outputs\apk\debug\app-debug.apk`  
可发给其他 **安卓** 用户安装；**不能**发给 iPhone 用户。

---

## 七、同步网页资源（改页面后必读）

修改了主页或 AR 页后，请同时更新 **Android** 与 **iOS** 内置资源：

**Android（开发时以这里为准）**

- `app/src/main/assets/www/index.html`
- `app/src/main/assets/www/ar_mobile.html`

**复制到 iOS（PowerShell 示例）**

```powershell
Copy-Item app\src\main\assets\www\* ios\ARPet\Resources\www\ -Recurse -Force
```

根目录 `index.html` 若也改了，建议与 `app/src/main/assets/www/index.html` 保持一致。

---

*文档随工程维护。若恢复原生 ARCore / 华为 AREngine 等方案，请重写「实现说明」与各平台步骤。*
