# ARPet iOS 版（与 Android 完全分开）

> **说明**：iPhone **没有 APK**。iOS 安装包是 `.ipa`，必须在 **Mac + Xcode** 里编译；和仓库里的 Android 工程（`app/`）互不影响。

## 目录结构

```
ios/
├── ARPet.xcodeproj/     # 用 Xcode 打开这个
├── ARPet/
│   ├── *.swift          # iOS 原生壳（WKWebView）
│   ├── Info.plist
│   └── Resources/
│       ├── www/         # 与 Android 相同的网页（主页 + AR 页）
│       └── sample_pet.glb
└── README.md            # 本文件
```

## 功能（与 Android App 一致）

1. 打开 App → **主页** `index.html`
2. 点击 **「开始 AR 体验」** → `ar_mobile.html`（摄像头 + 宠物叠加）
3. 点 **×** 返回主页

## 如何编译到 iPhone

1. 使用 **Mac**，安装 **Xcode**（App Store）
2. 双击打开 `ios/ARPet.xcodeproj`
3. 在 **Signing & Capabilities** 里选择你的 Apple ID 团队（个人免费账号即可真机调试）
4. 用数据线连接 iPhone，顶部选你的设备，点 **Run (▶)**

## 同步网页资源

修改了 `app/src/main/assets/www/` 或根目录 `index.html` 后，请复制到 iOS：

```bash
# 在项目根目录执行（PowerShell 示例）
Copy-Item app\src\main\assets\www\* ios\ARPet\Resources\www\ -Recurse -Force
```

## 与 Android 的区别

| 项目 | Android (`app/`) | iOS (`ios/`) |
|------|------------------|--------------|
| 安装包 | `.apk` | `.ipa`（Xcode 安装） |
| 工程 | Gradle / Android Studio | Xcode |
| 包名 | `com.example.arpet` | `com.example.arpet.ios` |
| 网页逻辑 | 共用 `www/` 内页面 | 同上 |

## 常见问题

**Q：能在 Windows 上打 iOS 包吗？**  
A：不能本地打包，需要 Mac + Xcode，或使用云端 Mac 构建服务。

**Q：小米 / 荣耀安卓机用这个目录吗？**  
A：不用。安卓请用仓库根目录的 **`app/`** 模块在 Android Studio 里编译。
