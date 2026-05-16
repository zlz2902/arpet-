# ARPet（AR 宠物小精灵）

本文档面向**人类用户**与 **AI 助手**：说明**产品意图与演进方向**、当前仓库里有什么、如何运行与扩展，便于协作开发与上下文对齐。

---

## 项目愿景（你要做成什么）

这是一个以 **Java（Android）+ AR** 为核心、**面向手机**的体验型项目，长期目标是沉淀成一套**可扩展的 AR 应用骨架**：后续可能接入更多模块（社交、账号、更多玩法、其他渲染或 SDK 等），但主线始终是：

- **在真实环境的画面上**，出现一只（或多只）**宠物小精灵**；
- 用户能与它在 **AR 空间里互动**（点击、放置、动画、音效等——随实现逐步丰富）；
- **设备形态**：优先 **手机**（支持 ARCore 的 Android 机），而非桌面浏览器。

当前仓库里的代码是这一愿景下的 **早期原型**：先把「摄像头看到的现实世界 + 叠加 3D 模型 + 基础交互」跑通，再往上叠功能。

### 当前范围锁定：单人、本地可玩

第一版目标定为 **一个人拿一台手机就能完整体验**，不要求：

- 多人联机、服务器、账号登录；
- 完整的「捕捉 / 图鉴 / 经济系统」（以后想做再加）。

交付标准可以是：**能稳定打开相机 AR → 放置宠物 → 有至少一种你愿意反复点的互动**（动画 / 音效 / 简单反馈均可）。先把这一条跑顺，再考虑扩展。

### 可选扩展（不挡第一版）：摄像头「捕捉」等

若以后仍有精力，可以再考虑：**利用摄像头与场景理解**做「发现 / 捕捉 / 背包」等玩法（命中判定、状态机等）。  
这对单人项目属于 **加分项**，实现前一版 README 与交互要先想清楚；**当前代码不承诺实现**。

---

## 当前仓库里有什么（事实陈述）

仓库内同时有两块内容，**主线是 Android**，Web 页为辅助演示：

| 形态 | 技术 | 角色 |
|------|------|------|
| **Android 应用 `ARPet`** | Java + **ARCore** + **SceneView**（`arsceneview`） | **主力**：手机上平面检测、放置 **GLB** 模型（默认 `sample_pet.glb` 占位），点击平面锚定 |
| **单页 `index.html`** | A-Frame + AR.js + Tailwind | **辅助**：营销式落地页 + 浏览器 WebAR 预览；与 Android **不是同一运行时**，勿混为「框架本体」 |

二者主题一致，但 **Java + AR 框架的长期演进应以 `app` 模块为准**；`index.html` 可选保留作演示或剥离。

---

## 目录结构（速查）

| 路径 | 说明 |
|------|------|
| `settings.gradle` | 根工程名：`ARPet`，子模块 `:app` |
| `build.gradle` | 顶层 Gradle：AGP `7.4.2`，仓库含 Google / Maven Central / JitPack |
| `app/build.gradle` | 应用模块：`applicationId` / `namespace` 均为 `com.example.arpet`，`compileSdk` / `targetSdk` 33，`minSdk` 24 |
| `app/src/main/AndroidManifest.xml` | 相机权限、`camera.ar` 特性、ARCore `required`、`MainActivity` 为启动 Activity |
| `app/src/main/java/com/example/arpet/MainActivity.java` | **入口**：组装 `ArSessionFacade`、`PetAnchorPresenter`、界面文案与 Toast |
| `app/src/main/java/com/example/arpet/config/PetExperienceConfig.java` | 模型路径、缩放、`PlacementMode`、就绪提示文案等（默认单机参数） |
| `app/src/main/java/com/example/arpet/pet/PetAnchorPresenter.java` | GLB 加载与平面锚定放置（`ArModelNode`） |
| `app/src/main/java/com/example/arpet/ar/ArSessionFacade.java` | `ArSceneView` 的 `resume` / `pause` / 平面点击委托 |
| `app/src/main/res/layout/activity_main.xml` | 全屏 `ArSceneView` + 顶部状态 `TextView` |
| `app/src/main/res/values/strings.xml` | 应用名：`AR宠物小精灵` |
| `app/src/main/assets/` | 默认含 **`sample_pet.glb`**（Khronos「Duck」示例，占位用）；可换成自有/已授权的 GLB，并改 `PetExperienceConfig` |
| `index.html` | 独立 Web 页：落地 UI + AR 场景 + 截图相关脚本 |

扩展新能力时：优先在 **`pet`**（宠物逻辑）、**`ar`**（会话与射线）、**`config`**（可调参数）下新增类，避免把 `MainActivity` 堆胖。

**资源**：GLB 放在 `app/src/main/assets/`；仓库已附带占位模型，可直接编译运行（见上表）。

---

## Android 应用详解（当前实现）

### 依赖（摘自 `app/build.gradle`）

- AndroidX：`appcompat`、`material`、`constraintlayout`
- `com.google.ar:core:1.37.0`
- `io.github.sceneview:arsceneview:0.10.0`

### 行为摘要（分层）

- **`ArSessionFacade`**：`ArSceneView` 生命周期；将平面点击交给外部监听。
- **`PetExperienceConfig`**：默认从 assets 加载 **`sample_pet.glb`**、缩放 `0.5f`、`PlacementMode.INSTANT`；就绪文案可配置。
- **`PetAnchorPresenter`**：`loadModelGlbAsync` 加载模型；`placeFromHit` 创建 `Anchor` 并挂接 `ArModelNode`。
- **`MainActivity`**：展示 `statusText`、加载失败提示、放置成功 Toast；在 `onResume` / `onPause` 调用 `ArSessionFacade`。

### 运行前置条件

- **支持 ARCore 的设备**（清单中 `camera.ar` 为 required）。
- **Google Play Services for AR** 可用。
- 开发：**Android Studio** + 与 AGP `7.4.2` 匹配的 **Gradle/JDK**。
- **真机测试步骤（图文流程）**：见 [`docs/AR放置功能测试步骤.md`](docs/AR放置功能测试步骤.md)。

### 必须补齐的资源

- `app/src/main/assets/` 内需有与配置一致的 GLB（默认 **`sample_pet.glb`** 已入库）。若改名或删除文件会导致加载失败。

### 给 AI / 开发者的扩展锚点

- **换模型 / 默认参数**：改 `PetExperienceConfig.defaultConfig()` 或改为从 JSON / 设置读取。
- **多宠物 / 会话**：在 `pet` 包增加管理器，`PetAnchorPresenter` 可拆为「工厂 + 实例」或列表持有多个 `ArModelNode`。
- **互动加深**：在 `pet` 或新建 `interaction` 包接 `ArModelNode` / 命中测试；`MainActivity` 只订阅回调。
- **「捕捉」向功能**：在 `ar` 包扩展命中与场景理解，`pet` 包维护收集状态（后续迭代）。
- **UI**：`activity_main.xml`、`strings.xml`；入口保持薄。

---

## Web 页面（`index.html`）

### 技术栈与用途

- Tailwind、Font Awesome、**A-Frame** + **AR.js**（Web 摄像头 AR）。
- 用于演示落地页与浏览器内预览；各精灵当前可共用占位 GLB。

### 运行方式

- **HTTPS** 或 **localhost**（摄像头策略）。

### 给 AI 的锚点

- 模型列表：`pokemonModels`、`selectPokemon`；场景：`a-scene` 的 `arjs` 属性；退出 AR：`stopAR()`。

---

## 演进建议（框架化时可记入 README 的 checklist）

将「Java + AR」做成框架时，可考虑逐步引入：

- **模块边界**：AR 会话 / 资源加载 / UI /（未来的）捕捉与背包数据分层。
- **配置化**：模型路径、缩放、默认放置策略由配置或 JSON 驱动，减少硬编码。
- **测试与 CI**：至少在 Gradle 层保证 `assembleDebug`；关键逻辑辅以单元测试（不依赖真机部分mock）。
- **文档**：每增加一大功能，在本文件「愿景」或「目录结构」补一句，方便 AI 检索。

---

## 与演示 / 第三方素材相关的声明

- Web 外链图片与示例模型仅供演示；商用须自行确认**版权与许可**。
- 默认 `sample_pet.glb` 为 Khronos 官方示例模型（非宝可梦形象）；若使用「皮卡丘」等形象，**须使用自有或已获授权** 的 GLB。

---

## 常见问题（FAQ）

**Q：Android 打开后提示模型加载失败？**  
A：检查 `app/src/main/assets/` 与文件名是否与 `loadModelGlbAsync` 一致。

**Q：Web AR 黑屏或无法调起摄像头？**  
A：使用 HTTPS 或 localhost，并授予摄像头权限；更换浏览器重试。

**Q：Android 与 Web 能否共用 3D 资源？**  
A：可统一用 **GLB/glTF**；加载 API 不同（SceneView vs A-Frame）。

**Q：我的手机是荣耀 X50 GT（或其它国行荣耀），AR 应用闪退/黑屏？**  
A：本应用依赖 **Google ARCore**；该机型多数 **未在官方支持列表** 中，且国行常无法完整使用 **Google Play Services for AR**，故原生 AR 可能无法运行。可换一台 ARCore 认证机测试，或先用仓库里的 **`index.html`（WebAR）** 在浏览器里试。说明见 [`docs/AR放置功能测试步骤.md`](docs/AR放置功能测试步骤.md) 第 2 节。

**Q：Gradle Sync 报错 `Unable to find method ... DependencyHandler.module`？**  
A：多为 **Gradle 版本与 Android Gradle Plugin 7.4.2 不匹配**。本仓库已加入 **Gradle Wrapper**（固定 **Gradle 7.6.4**）。请在 Android Studio 中选用 **Gradle Wrapper**：`File → Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle projects → use Gradle from: 'wrapper'`（表述因版本略有差异），然后 **Sync**。勿让 IDE 使用「默认」过高版本的 Gradle。若命令行运行 `gradlew` 失败，检查系统 **`JAVA_HOME`** 是否指向有效 JDK（建议 JDK 11 或 17，与 Android Studio 内置 JDK 一致）。

**Q：Gradle Sync 报错 `Could not install Gradle distribution` / `SocketTimeoutException: Connect timed out`？**  
A：**下载 Gradle 压缩包时网络超时**（访问 `services.gradle.org` 在国内经常很慢或被阻断）。本项目已将 Wrapper 的 **`networkTimeout` 调到 5 分钟**，请先 **再 Sync 一次**。若仍失败：① 在 Android Studio 配置 **HTTP Proxy**（`Settings → Appearance & Behavior → System Settings → HTTP Proxy`）或换稳定网络 / 代理；② 换用国内镜像：编辑 `gradle/wrapper/gradle-wrapper.properties`，将 `distributionUrl` 改为镜像地址（例如腾讯云文档提供的 Gradle 镜像，版本路径需与 **7.6.4** 一致），保存后再 Sync；③ 浏览器手动下载同版本的 `gradle-7.6.4-bin.zip`，再按网上教程放入本机 `用户目录\.gradle\wrapper\dists\` 下对应哈希子目录（可先删除该版本未下完的半截文件夹再操作）。

---

## 文档维护约定

- **愿景或玩法变更**（例如正式做「摄像头捕捉」）：先更新本文「项目愿景」与「规划中的玩法方向」，再改代码。
- **目录或 Gradle 变更**：同步更新「目录结构」表。

---

*本文描述以仓库内源码为准；若实现追上愿景，请把「规划中」改为「已实现」并简述入口类/模块名。*
