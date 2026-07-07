# Video Downloader

基于原项目 [alexch33/super-video-downloader](https://github.com/alexch33/super-video-downloader) 精简优化的视频下载应用。

Based on the original project [alexch33/super-video-downloader](https://github.com/alexch33/super-video-downloader), this is a streamlined and optimized video download application.

## Original Project / 原项目

- **原项目**: [super-video-downloader](https://github.com/alexch33/super-video-downloader)
- **原作者**: alexch33

- **Original Project**: [super-video-downloader](https://github.com/alexch33/super-video-downloader)
- **Original Author**: alexch33

本项目保留了原项目的核心功能（浏览器、视频嗅探、下载管理），并进行了精简优化：
- 移除了代理、广告拦截、本地视频播放等非核心功能
- 精简了 FFmpeg 库（从 full-gpl 改为 min-gpl）
- 仅保留中文和英文语言支持
- 优化了构建配置，仅编译 arm64-v8a 架构

This project retains the core features of the original (browser, video sniffing, download management) and has been streamlined:
- Removed non-core features such as proxy, ad-blocking, and local video playback
- Streamlined FFmpeg library (from full-gpl to min-gpl)
- Only Chinese and English language support retained
- Optimized build configuration to compile only arm64-v8a architecture

## Disclaimer / 免责声明

This project was created for research and educational purposes to explore the downloading of a wide variety of video formats and stream types. The developer does not take any responsibility for illegal actions performed by users of this application.

本项目仅供研究和教育目的，用于探索各种视频格式和流类型的下载。开发者不对用户使用本应用进行的非法行为承担任何责任。

## Features / 功能特性

- **Video Sniffing**: Auto-detect videos on web pages
  - **视频嗅探**: 自动检测网页上的视频
- **Advanced Stream Download**: Support for HLS (`.m3u8`) and DASH (`.mpd`) streams, in addition to standard `.mp4` video streams
  - **高级流下载**: 支持 HLS (`.m3u8`) 和 DASH (`.mpd`) 流，以及标准 `.mp4` 视频流
- **Built-in Browser**: A lightweight browser to find and download videos
  - **内置浏览器**: 轻量级浏览器，用于查找和下载视频
- **Download Manager**: Manage all download tasks with real-time progress tracking
  - **下载管理**: 管理所有下载任务，实时跟踪进度
- **Lossless Remuxing**: Convert downloaded streams to MP4 format without re-encoding
  - **无损重封装**: 将下载的流转换为 MP4 格式，无需重新编码

## Major Technologies / 主要技术

- **Language**: Kotlin
  - **语言**: Kotlin
- **Architecture**: MVVM (ViewModel, LiveData) with Repository Pattern
  - **架构**: MVVM (ViewModel, LiveData) + Repository 模式
- **UI**: Android Views with DataBinding
  - **界面**: Android Views + DataBinding
- **Dependency Injection**: Dagger 2
  - **依赖注入**: Dagger 2
- **Concurrency**: Coroutines & RxJava
  - **并发**: Coroutines & RxJava
- **Database**: Room
  - **数据库**: Room
- **Networking**: OkHttp
  - **网络**: OkHttp
- **Video Processing**: FFmpeg (min-gpl)
  - **视频处理**: FFmpeg (min-gpl)

## How to Build / 构建方式

### Prerequisites / 前提条件

1. **JDK 21**
2. **Android SDK & NDK** (r27d recommended)

### Build Commands / 构建命令

- Build debug APK (macOS / Linux):
  - 构建调试版 APK (macOS / Linux):
```bash
./gradlew :app:assembleDebug
```

- Build debug APK (Windows PowerShell):
  - 构建调试版 APK (Windows PowerShell):
```powershell
.\gradlew.bat :app:assembleDebug
```

- Build release APK:
  - 构建发布版 APK:
```bash
./gradlew :app:assembleRelease
```

- Clean build artifacts:
  - 清理构建产物:
```bash
./gradlew clean
```

## License / 许可证

This package is licensed under the [LICENSE](./LICENSE) for details.

详见 [LICENSE](./LICENSE) 文件。