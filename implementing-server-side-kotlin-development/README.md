# implementing-server-side-kotlin-development

このプロジェクトは、KotlinとSpring Bootを用いたサーバーサイド開発の実装例です。

## 主な技術スタック
- Kotlin
- Spring Boot
- Gradle (Kotlin DSL)
- Dokka (KDocドキュメント生成)
- Jackson (Kotlin用JSONシリアライズ)

## セットアップ

### 前提条件
- JDK 17 以上
- Gradle 8 以上（`./gradlew` で自動ダウンロード可）

### ビルドと起動

```sh
./gradlew build
./gradlew bootRun
```

### ドキュメント生成

```sh
./gradlew dokkaHtml
```
生成されたドキュメントは `build/dokka/html/index.html` をブラウザで開くことで閲覧できます。

macOSやLinuxの場合、以下のコマンドで直接ブラウザで開くこともできます。

```sh
open build/dokka/html/index.html
```

## ディレクトリ構成
- `src/main/kotlin` : アプリケーション本体
- `src/test/kotlin` : テストコード
- `build.gradle.kts` : ビルド設定
- `docker/` : Docker関連ファイル
- `config/` : 静的解析等の設定

## 依存ライブラリ・プラグインの導入方法

### プラグインの追加

`build.gradle.kts` の `plugins` ブロックに追加します。

例：
```kotlin
plugins {
    kotlin("jvm") version "1.9.22"
    id("org.springframework.boot") version "3.5.3"
    // 必要なプラグインをここに追加
}
```

### 依存ライブラリの追加

`build.gradle.kts` の `dependencies` ブロックに追加します。

例：
```kotlin
dependencies {
    implementation("io.arrow-kt:arrow-core:1.2.1")
    testImplementation("org.assertj:assertj-core:3.25.2")
    testImplementation("net.jqwik:jqwik:1.8.2")
    testImplementation("net.jqwik:jqwik-kotlin:1.8.2")
}
```

追加後、以下のコマンドで依存関係やプラグインをダウンロード・反映します。

```sh
./gradlew build
```

## ライセンス
このリポジトリはMITライセンスです。
