# super-video-downloader 二次开发环境交接文档

> 本文件用于在 AI 对话切换时保持项目状态连续性。
> 最后更新：2026-07-06

---

## 一、项目基本信息

| 项 | 值 |
|---|---|
| 项目名 | super-video-downloader |
| 仓库地址 | https://github.com/alexch33/super-video-downloader |
| 当前版本 | v0.8.21.2 (versionCode 338) |
| 项目本地路径 | `c:\Users\Administrator\Desktop\github_Fork\super-video-downloader` |
| 技术栈 | Kotlin + Go (v2ray) + Rust (adblock) |
| 构建工具 | Gradle 8.14.3 (Kotlin DSL) + Android Gradle Plugin 8.13.2 |
| 最低 SDK | 24 (minSdk) |
| 目标 SDK | 36 (targetSdk / compileSdk) |
| NDK 版本 | 27.3.13750724 |

---

## 二、环境变量配置（全部已永久设置 via setx）

| 环境变量 | 值 | 说明 |
|---|---|---|
| `JAVA_HOME` | `C:\Program Files\Java\jdk-21.0.11` | JDK 21 (Temurin) |
| `ANDROID_HOME` | `C:\Android\Sdk` | Android SDK 根目录 |
| `ANDROID_SDK_ROOT` | `C:\Android\Sdk` | 同上（冗余但必要） |
| `ANDROID_NDK_HOME` | `C:\Android\Sdk\ndk\27.3.13750724` | NDK 路径 |
| `ANDROID_NDK_ROOT` | `C:\Android\Sdk\ndk\27.3.13750724` | 同上 |
| `GO_EXECUTABLE` | `C:\go1.25.11\go\bin\go.exe` | Go 1.25.11（项目专用） |
| `CGO_LDFLAGS_ALLOW` | `.*` | 允许所有 cgo 链接器标志 |
| `CGO_CFLAGS_ALLOW` | `.*` | 允许所有 cgo 编译器标志 |

### 用户 PATH 修改

- 已将 `C:\Program Files\CMake\bin` 加入用户 PATH（CMake 原本未在 PATH 中）

### 未设置但需要的变量

无。所有环境变量均已通过 `setx` 永久设置，新终端可直接编译。

---

## 三、已安装工具清单

| 工具 | 版本 | 安装路径 |
|---|---|---|
| JDK | 21.0.11 (Temurin) | `C:\Program Files\Java\jdk-21.0.11` |
| Go (系统) | 1.26.4 | 系统 PATH 中（不影响本项目） |
| Go (项目专用) | 1.25.11 | `C:\go1.25.11\go\bin\go.exe` |
| Rust | 1.96.1 | 默认 rustup 位置 |
| Rust Android targets | 4 个 | aarch64/armv7/x86_64/i686-linux-android |
| CMake | 4.3.4 | `C:\Program Files\CMake\bin` |
| Git | 2.53.0 | 系统默认 |
| VS Build Tools | 2022 (MSVC v143 + Win11 SDK) | 系统默认 |

### Android SDK 组件

| 组件 | 版本 | 路径 |
|---|---|---|
| cmdline-tools | 20.0 | `C:\Android\Sdk\cmdline-tools\latest` |
| platform-tools | 最新 | `C:\Android\Sdk\platform-tools` |
| platforms | android-36 | `C:\Android\Sdk\platforms\android-36` |
| build-tools | 36.0.0 + 35.0.0 (自动装的) | `C:\Android\Sdk\build-tools\` |
| NDK | 27.3.13750724 | `C:\Android\Sdk\ndk\27.3.13750724` |

---

## 四、对项目源码的修改（重要！）

### 4.1 `gradle.properties` — 新增 1 行

**文件**：`super-video-downloader/gradle.properties`

**修改内容**：在文件末尾添加了：
```properties
android.overridePathCheck=true
```

**原因**：项目路径 `c:\Users\Administrator\Desktop\github二次开发\` 包含中文字符，Android Gradle Plugin 会拒绝构建。此选项绕过该检查。

**风险**：此选项是实验性的。未来某些 Gradle 插件或工具链可能无法正确处理中文路径，导致构建失败。详见第七节。

### 4.2 `app/build.gradle.kts` — 修改 1 行

**文件**：`super-video-downloader/app/build.gradle.kts`

**修改位置**：第 406 行

**原始内容**：
```kotlin
val executableSuffix = if (isWindows) ".exe" else ""
```

**修改为**：
```kotlin
val executableSuffix = if (isWindows) ".cmd" else ""
```

**原因**：NDK 27.x 在 Windows 上的 clang wrapper 是 `.cmd` 文件而非 `.exe`。代码原来硬编码 `.exe`，导致找不到 Rust 交叉编译的链接器。

**影响范围**：仅影响 `buildRustAdblock` 任务中的链接器查找。

### 4.3 `local.properties` — 新建文件

**文件**：`super-video-downloader/local.properties`

**内容**：
```properties
sdk.dir=C:/Android/Sdk
```

**说明**：标准 Gradle 配置文件，指向 SDK 路径。已在 `.gitignore` 中（不会被提交）。

### 4.4 Rust vendor 目录行尾修复

**受影响目录**：`super-video-downloader/rust_adblock/vendor/`

**问题**：Git 在 Windows 上默认 `core.autocrlf=true`，检出文件时把 LF 换行符转成 CRLF。但 vendored 的 Rust crate 有 `.cargo-checksum.json` 记录源文件 SHA-256 校验和，行尾改变后校验和不匹配，Cargo 拒绝编译。

**修复操作**：
1. 将 vendor 目录下 2686 个文件的 CRLF 转回 LF
2. 重新生成 11 个 crate 的 `.cargo-checksum.json` 校验和：
   - psl, seahash, unicode-ident
   - windows_aarch64_gnullvm, windows_aarch64_msvc
   - windows_i686_gnu, windows_i686_msvc
   - windows_x86_64_gnu, windows_x86_64_gnullvm, windows_x86_64_msvc

**git 配置**：已在项目目录执行 `git config core.autocrlf false`，防止后续操作再次转换行尾。

**注意**：`git status` 会显示这些文件被修改，这是正常的。

---

## 五、Go 版本说明

### 为什么有两个 Go？

| 版本 | 路径 | 用途 |
|---|---|---|
| 1.26.4 | 系统 PATH | 其他项目使用（未修改） |
| 1.25.11 | `C:\go1.25.11\` | 专供本项目使用 |

### 为什么 1.26.4 不行？

项目 `libs.versions.toml` 指定 `goVersion = "1.25.0"`。Go 1.26 引入了更严格的 cgo 链接器标志安全校验，会拒绝 `--sysroot` 和 `-Wl,-z,max-page-size=16384` 这类标志。Go 1.25.x 没有这个限制。

项目通过 `GO_EXECUTABLE` 环境变量指定使用 Go 1.25.11，不影响系统全局 Go 版本。

### 为什么需要 CGO_LDFLAGS_ALLOW？

即使降级到 Go 1.25.11，Go 的 cgo 仍然对 `--sysroot` 标志有安全校验。设置 `CGO_LDFLAGS_ALLOW=.*` 和 `CGO_CFLAGS_ALLOW=.*` 绕过该校验，允许 NDK 的 sysroot 标志通过。

**安全提示**：`.*` 放行所有 cgo 标志。仅用于本地可信项目编译。如需编译来路不明的 Go cgo 代码，请临时删除此环境变量。

---

## 六、已验证的编译流程

### 完整编译命令

```powershell
# 在项目根目录执行
cd "c:\Users\Administrator\Desktop\github二次开发\super-video-downloader"
.\gradlew.bat :app:assembleDebug --console=plain
```

所有环境变量已永久设置，无需额外配置。

### 编译产物

| APK | 大小 | 路径 |
|---|---|---|
| app-arm64-v8a-debug.apk | 103 MB | `app\build\outputs\apk\debug\` |
| app-armeabi-v7a-debug.apk | 117 MB | 同上 |
| app-x86-debug.apk | 105 MB | 同上 |
| app-x86_64-debug.apk | 109 MB | 同上 |
| app-universal-debug.apk | 372 MB | 同上 |

### 编译耗时

- 首次构建：约 6-8 分钟（含 Rust 4 架构 + Go 4 架构 + Kotlin）
- 增量构建：约 1-2 分钟（原生库已缓存）

### 其他常用命令

```powershell
# 清理构建产物
.\gradlew.bat clean

# 只构建 Go 原生库
.\gradlew.bat :app:buildAllGoLibraries

# 只构建 Rust 广告拦截库
.\gradlew.bat :app:buildRustAdblock
```

---

## 七、已知风险与注意事项

### 7.1 中文路径风险（高风险）

**当前状态**：项目位于 `c:\Users\Administrator\Desktop\github二次开发\super-video-downloader\`，路径含中文。通过 `android.overridePathCheck=true` 绕过 AGP 检查，当前能正常编译。

**潜在问题**：以下工具链环节可能因中文路径出错：

| 环节 | 可能的错误表现 |
|---|---|
| Go 编译 | `go build` 报路径找不到或编码错误 |
| NDK 工具链 | clang 找不到头文件路径 |
| aapt2 资源处理 | 资源合并失败，`aapt2.exe` 异常退出 |
| R8/ProGuard | 代码混淆时找不到类文件路径 |
| Windows 路径长度 | 中文占 3 字节，更易触发 260 字符限制 |

**迁移建议**：将项目移至纯英文路径（如 `C:\Projects\super-video-downloader\`）。迁移后：
1. 删除 `gradle.properties` 中的 `android.overridePathCheck=true`（不再需要）
2. 更新 `local.properties` 中的 sdk.dir（如 SDK 路径不变则无需改）
3. 重新执行 `git config core.autocrlf false`
4. 首次编译前清理：`.\gradlew.bat clean`

### 7.2 源码修改的 git 追踪

以下修改已被 git 追踪，`git status` 会显示为已修改：

| 文件 | 修改类型 |
|---|---|
| `gradle.properties` | 新增 1 行 |
| `app/build.gradle.kts` | 修改 1 行（第 406 行） |
| `rust_adblock/vendor/**` | 行尾修复 + 校验和重生成（2686 个文件） |
| `local.properties` | 新建（在 .gitignore 中，不会被追踪） |

**注意**：如果未来执行 `git pull` 拉取上游更新，`app/build.gradle.kts` 和 `rust_adblock/vendor/` 会产生冲突。用户已确认不会执行 git pull，所以无需担心。

### 7.3 签名配置

当前只构建 debug 版本（使用 Android 默认 debug keystore 自动签名）。如需构建 release 版本：
1. 生成签名 keystore
2. 设置环境变量：`KEYSTORE_PATH`、`KEYSTORE_PASSWORD`、`KEY_ALIAS`、`KEY_PASSWORD`
3. 执行 `.\gradlew.bat :app:assembleRelease`

### 7.4 环境变量全局影响

`CGO_LDFLAGS_ALLOW=.*` 和 `CGO_CFLAGS_ALLOW=.*` 是全局环境变量，会影响系统上所有 Go cgo 编译。如其他 Go 项目遇到安全问题，临时删除这两个变量即可。

---

## 八、编译前环境检查清单

如果遇到编译失败，按以下顺序检查：

```powershell
# 1. 检查环境变量
echo $env:JAVA_HOME          # 应为 C:\Program Files\Java\jdk-21.0.11
echo $env:ANDROID_HOME       # 应为 C:\Android\Sdk
echo $env:ANDROID_NDK_HOME   # 应为 C:\Android\Sdk\ndk\27.3.13750724
echo $env:GO_EXECUTABLE      # 应为 C:\go1.25.11\go\bin\go.exe
echo $env:CGO_LDFLAGS_ALLOW  # 应为 .*

# 2. 检查工具可用性
java -version                # 应显示 21.0.11
& $env:GO_EXECUTABLE version # 应显示 go1.25.11
rustc --version               # 应显示 1.96.1
cmake --version               # 应显示 4.3.4

# 3. 检查 SDK 组件
Test-Path "C:\Android\Sdk\platforms\android-36\android.jar"  # True
Test-Path "C:\Android\Sdk\ndk\27.3.13750724\source.properties" # True

# 4. 检查项目文件
Test-Path "super-video-downloader\local.properties"  # True
Test-Path "super-video-downloader\gradlew.bat"        # True
```

---

## 九、项目架构速览

```
super-video-downloader/
├── app/                          # Android 主应用模块
│   ├── build.gradle.kts          # 应用构建配置（已修改第406行）
│   ├── src/main/                 # Kotlin 源码 + 资源
│   │   ├── java/com/myAllVideoBrowser/  # 主包
│   │   ├── jniLibs/              # 编译生成的 .so 原生库
│   │   └── AndroidManifest.xml
│   └── proguard-rules.pro        # 代码混淆规则
├── rust_adblock/                 # Rust 广告拦截引擎
│   ├── src/lib.rs                # Rust JNI 入口
│   ├── vendor/                   # Vendored Rust 依赖（已修复行尾）
│   ├── Cargo.toml
│   └── rust-toolchain.toml       # 指定 Rust 1.96.0
├── v2ray-src/                    # Go v2ray 网络引擎（git 子模块）
├── gradle/
│   └── libs.versions.toml        # 所有依赖版本声明
├── build.gradle.kts              # 根构建配置
├── settings.gradle.kts           # 项目设置
├── gradle.properties             # Gradle 属性
├── local.properties              # SDK 路径（新建）
├── gradlew.bat                   # Gradle 包装器（Windows）
└── .gitmodules                   # 子模块配置
```

### 三种原生库

| 库 | 语言 | 构建任务 | 产物 |
|---|---|---|---|
| libgojni.so | Go | `buildGoSharedLib_{abi}` | v2ray 网络引擎 |
| libadblock_rust_jni.so | Rust | `buildRustAdblock` | 广告拦截引擎 |
| (Kotlin/Java) | Kotlin | `assembleDebug` | 主应用逻辑 |

---

## 十、对话历史摘要

1. 用户要求二次开发编译运行 `super-video-downloader` 项目
2. 审查并修正了用户的初始规划（缺少 CMake、SDK 版本不匹配等）
3. 搭建完整环境：解压 cmdline-tools、安装 SDK/NDK、安装 Rust targets
4. 克隆项目 + 初始化 v2ray-src 子模块
5. 创建 local.properties
6. 首次编译失败 → 发现并修复 6 个问题：
   - JAVA_HOME 未设置
   - 中文路径被 AGP 拒绝
   - ANDROID_NDK_HOME 未设置
   - NDK clang 后缀 .exe → .cmd
   - Rust vendor CRLF 校验和不匹配
   - Go cgo_ldflag 安全检查拒绝 --sysroot
7. 编译成功：BUILD SUCCESSFUL，生成 5 个 APK
8. 用户确认不会 git pull，要求永久设置 CGO_LDFLAGS_ALLOW
9. 永久设置 CGO_LDFLAGS_ALLOW 和 CGO_CFLAGS_ALLOW（via setx）
10. 创建本交接文档
11. 尝试通过文件分享服务上传 APK（file.io / transfer.sh / 0x0.st 均失败）
12. 启动局域网 HTTP 服务器（端口 8080）供手机下载 APK → 用户不在同一 WiFi
13. 最终通过 TRAE 文件链接（computer:// 协议）让用户直接下载 APK 成功

### 遗留事项

- **HTTP 服务器可能仍在后台运行**（端口 8080，PID 可能已释放）。如需关闭，执行：`Stop-Process -Name python -ErrorAction SilentlyContinue` 或 `netstat -ano | findstr :8080` 查找并 kill。
- **Go 1.25.11 ZIP 安装包**仍在 `C:\go1.25.11.zip`，可删除节省空间。
- **cmdline-tools ZIP** 仍在工作目录 `commandlinetools-win-14742923_latest.zip`，可删除。
