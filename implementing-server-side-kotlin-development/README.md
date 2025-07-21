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

### 静的解析（Detekt）

Kotlinコードの静的解析にはDetektを利用しています。

#### メインソースの解析
```sh
./gradlew detektMain
```
レポートは `build/reports/detekt/` 以下に出力されます。
自動修正を行いたい場合は、以下のコマンドを利用できます。
```sh
./gradlew detektMain --auto-correct
```

#### テストコードの解析
```sh
./gradlew detektTest
```
自動修正を行いたい場合は、以下のコマンドを利用できます。
```sh
./gradlew detektTest --auto-correct
```

## ディレクトリ構成
- `src/main/kotlin` : アプリケーション本体
- `src/test/kotlin` : テストコード
- `build.gradle.kts` : ビルド設定
- `docker/` : Docker関連ファイル
- `config/` : 静的解析等の設定

## ライセンス
このリポジトリはMITライセンスです。
