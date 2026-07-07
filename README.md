# Video Downloader

基于原项目 [alexch33/super-video-downloader](https://github.com/alexch33/super-video-downloader) 精简优化的视频下载应用。

## Original Project

- **原项目**: [super-video-downloader](https://github.com/alexch33/super-video-downloader)
- **原作者**: alexch33

本项目保留了原项目的核心功能（浏览器、视频嗅探、下载管理），并进行了精简优化：
- 移除了代理、广告拦截、本地视频播放等非核心功能
- 精简了 FFmpeg 库（从 full-gpl 改为 min-gpl）
- 仅保留中文和英文语言支持
- 优化了构建配置，仅编译 arm64-v8a 架构

## Disclaimer

This project was created for research and educational purposes to explore the downloading of a wide variety of video formats and stream types. The developer does not take any responsibility for illegal actions performed by users of this application.

## Features

- **Video Sniffing**: Auto-detect videos on web pages
- **Advanced Stream Download**: Support for HLS (`.m3u8`) and DASH (`.mpd`) streams, in addition to standard `.mp4` video streams
- **Built-in Browser**: A lightweight browser to find and download videos
- **Download Manager**: Manage all download tasks with real-time progress tracking
- **Lossless Remuxing**: Convert downloaded streams to MP4 format without re-encoding

## Major Technologies

- **Language**: Kotlin
- **Architecture**: MVVM (ViewModel, LiveData) with Repository Pattern
- **UI**: Android Views with DataBinding
- **Dependency Injection**: Dagger 2
- **Concurrency**: Coroutines & RxJava
- **Database**: Room
- **Networking**: OkHttp
- **Video Processing**: FFmpeg (min-gpl)

## How to Build

### Prerequisites

1. **JDK 21**
2. **Android SDK & NDK** (r27d recommended)

### Build Commands

- Build debug APK (macOS / Linux):
```bash
./gradlew :app:assembleDebug
```

- Build debug APK (Windows PowerShell):
```powershell
.\gradlew.bat :app:assembleDebug
```

- Build release APK:
```bash
./gradlew :app:assembleRelease
```

- Clean build artifacts:
```bash
./gradlew clean
```

## License

This package is licensed under the [LICENSE](./LICENSE) for details.