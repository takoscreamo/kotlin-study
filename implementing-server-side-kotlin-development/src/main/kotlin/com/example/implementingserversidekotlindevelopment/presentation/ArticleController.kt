package com.example.implementingserversidekotlindevelopment.presentation

import arrow.core.getOrElse
import com.example.implementingserversidekotlindevelopment.presentation.model.Article
import com.example.implementingserversidekotlindevelopment.presentation.model.GenericErrorModel
import com.example.implementingserversidekotlindevelopment.presentation.model.GenericErrorModelErrors
//import com.example.implementingserversidekotlindevelopment.presentation.model.MultipleArticleResponse
//import com.example.implementingserversidekotlindevelopment.presentation.model.NewArticleRequest
import com.example.implementingserversidekotlindevelopment.presentation.model.SingleArticleResponse
//import com.example.implementingserversidekotlindevelopment.presentation.model.UpdateArticleRequest
//import com.example.implementingserversidekotlindevelopment.usecase.CreateArticleUseCase
//import com.example.implementingserversidekotlindevelopment.usecase.DeleteCreatedArticleUseCase
//import com.example.implementingserversidekotlindevelopment.usecase.FeedArticleUseCase
import com.example.implementingserversidekotlindevelopment.usecase.ShowArticleUseCase
//import com.example.implementingserversidekotlindevelopment.usecase.UpdateArticleUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.hibernate.validator.constraints.Length
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
//import java.lang.UnsupportedOperationException

/**
 * 作成済記事記事のコントローラー
 *
 * @property showArticleUseCase 単一記事取得ユースケース
 * @property createArticleUseCase 記事作成ユースケース
 * @property feedArticleUseCase 記事一覧取得ユースケース
 * @property updateArticleUseCase 記事更新ユースーケース
 * @property deleteCreatedArticleUseCase 記事削除ユースケース
 */
@RestController
@Validated
class ArticleController(
    val showArticleUseCase: ShowArticleUseCase,
//    val createArticleUseCase: CreateArticleUseCase,
//    val feedArticleUseCase: FeedArticleUseCase,
//    val updateArticleUseCase: UpdateArticleUseCase,
//    val deleteCreatedArticleUseCase: DeleteCreatedArticleUseCase,
) {
    /**
     * 単一の作成済記事取得
     *
     * @return
     */
    @Operation(
        summary = "単一記事取得",
        operationId = "getArticle",
        description = "slug に一致する記事を取得します。",
        tags = ["articles"],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        schema = Schema(implementation = SingleArticleResponse::class),
                        examples = [
                            ExampleObject(
                                name = "OK",
                                value = """
                                    {
                                        "article": {
                                            "slug": "283e60096c26aa3a39cf04712cdd1ff7",
                                            "title": "title",
                                            "description": "description",
                                            "body": "body"
                                        }
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not Found",
                content = [
                    Content(
                        schema = Schema(implementation = GenericErrorModel::class),
                        examples = [
                            ExampleObject(
                                name = "Not Found",
                                value = """
                                    {
                                        "errors": {
                                            "body": [
                                                "slug に該当する記事は見つかりませんでした"
                                            ]
                                        }
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/api/articles/{slug}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getArticle(
        @Parameter(description = "記事の slug", required = true, schema = Schema(minLength = 32, maxLength = 32)) @Valid @PathVariable("slug") @Length(min = 32, max = 32) slug: String,
    ): ResponseEntity<SingleArticleResponse> {
        /**
         * 作成済記事の取得
         */
        val createdArticle = showArticleUseCase.execute(slug).getOrElse { throw ShowArticleUseCaseErrorException(it) }

        return ResponseEntity(
            SingleArticleResponse(
                article = Article(
                    slug = createdArticle.slug.value,
                    title = createdArticle.title.value,
                    description = createdArticle.description.value,
                    body = createdArticle.body.value,
                ),
            ),
            HttpStatus.OK
        )
    }

    /**
     * 単一記事取得ユースケースがエラーを戻したときの Exception
     *
     * このクラスの例外が発生したときに、@ExceptionHandler で例外処理をおこなう
     *
     * @property error
     */
    data class ShowArticleUseCaseErrorException(val error: ShowArticleUseCase.Error) : Exception()

    /**
     * ShowArticleUseCaseErrorException をハンドリングする関数
     *
     * ShowArticleUseCase.Error の型に合わせてレスポンスを分岐させる
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = [ShowArticleUseCaseErrorException::class])
    fun onShowArticleUseCaseErrorException(e: ShowArticleUseCaseErrorException): ResponseEntity<GenericErrorModel> =
        when (val error = e.error) {
            /**
             * 原因: slug に該当する記事が見つからなかった
             */
            is ShowArticleUseCase.Error.NotFoundArticleBySlug -> ResponseEntity<GenericErrorModel>(
                GenericErrorModel(
                    errors = GenericErrorModelErrors(
                        body = listOf("${error.slug.value} に該当する記事は見つかりませんでした")
                    )
                ),
                HttpStatus.NOT_FOUND
            )

            /**
             * 原因: バリデーションエラー
             */
            is ShowArticleUseCase.Error.ValidationErrors -> ResponseEntity<GenericErrorModel>(
                GenericErrorModel(
                    errors = GenericErrorModelErrors(
                        body = error.errors.map { it.message }
                    )
                ),
                HttpStatus.BAD_REQUEST
            )
        }
//
//    /**
//     * 新規記事作成
//     *
//     * @return
//     */
//    @Operation(
//        summary = "新規記事作成",
//        operationId = "CreateArticle",
//        description = "新規に記事を作成します",
//        tags = ["articles"],
//        responses = [
//            ApiResponse(
//                responseCode = "201",
//                description = "OK",
//                content = [
//                    Content(
//                        schema = Schema(implementation = SingleArticleResponse::class),
//                        examples = [
//                            ExampleObject(
//                                name = "OK",
//                                value = """
//                                    {
//                                        "article": {
//                                            "slug": "283e60096c26aa3a39cf04712cdd1ff7",
//                                            "title": "new-article-title",
//                                            "description": "new-article-description",
//                                            "body": "new-article-body"
//                                        }
//                                    }
//                                """
//                            )
//                        ]
//                    )
//                ]
//            ),
//            ApiResponse(
//                responseCode = "400",
//                description = "Validation Error",
//                content = [
//                    Content(
//                        schema = Schema(implementation = GenericErrorModel::class),
//                        examples = [
//                            ExampleObject(
//                                name = "Validation Error",
//                                value = """
//                                    {
//                                        "errors": {
//                                            "body": [
//                                                "article.titleは必須です",
//                                                "article.bodyは0文字以上64文字以下です",
//                                                "article.descriptionは0文字以上1024文字以下です"
//                                            ]
//                                        }
//                                    }
//                                """
//                            )
//                        ]
//                    )
//                ]
//            )
//        ]
//    )
//    @PostMapping("/api/articles", produces = [MediaType.APPLICATION_JSON_VALUE])
//    fun createArticle(@Valid @RequestBody newArticleRequest: NewArticleRequest): ResponseEntity<SingleArticleResponse> {
//        val createdArticle = createArticleUseCase.execute(
//            title = newArticleRequest.validatedArticle.validatedTitle,
//            description = newArticleRequest.validatedArticle.validatedDescription,
//            body = newArticleRequest.validatedArticle.validatedBody,
//        ).getOrElse { throw CreateArticleUseCaseErrorException(it) }
//
//        return ResponseEntity(
//            SingleArticleResponse(
//                article = Article(
//                    slug = createdArticle.slug.value,
//                    title = createdArticle.title.value,
//                    description = createdArticle.description.value,
//                    body = createdArticle.body.value,
//                ),
//            ),
//            HttpStatus.CREATED
//        )
//    }
//
//    /**
//     * 記事作成ユースケースがエラーを戻したときの Exception
//     *
//     * このクラスの例外が発生したときに、@ExceptionHandler で例外をおこなう
//     *
//     * @property error
//     */
//    data class CreateArticleUseCaseErrorException(val error: CreateArticleUseCase.Error) : Throwable()
//
//    /**
//     * CreateArticleUseCaseErrorException をハンドリングする関数
//     *
//     * CreateArticleUseCase.Error の型に合わせてレスポンスを分岐する
//     *
//     * @param e
//     * @return
//     */
//    @ExceptionHandler(value = [CreateArticleUseCaseErrorException::class])
//    fun onCreateArticleUseCaseErrorException(e: CreateArticleUseCaseErrorException): ResponseEntity<GenericErrorModel> =
//        when (val error = e.error) {
//            /**
//             * 原因: 記事のリクエストが不正
//             */
//            is CreateArticleUseCase.Error.InvalidArticle -> ResponseEntity<GenericErrorModel>(
//                GenericErrorModel(
//                    errors = GenericErrorModelErrors(
//                        body = error.errors.map { it.message }
//                    )
//                ),
//                HttpStatus.FORBIDDEN
//            )
//        }
//
//    /**
//     * 新規記事作成
//     *
//     * @return
//     */
//    @Operation(
//        summary = "記事一覧取得",
//        operationId = "GetArticles",
//        description = "記事を一覧取得します",
//        tags = ["articles"],
//        responses = [
//            ApiResponse(
//                responseCode = "200",
//                description = "OK",
//                content = [
//                    Content(
//                        schema = Schema(implementation = MultipleArticleResponse::class),
//                        examples = [
//                            ExampleObject(
//                                name = "OK",
//                                value = """
//                                    {
//                                        "articleCount": 2,
//                                        "articles": [
//                                            {
//                                                "slug": "slug0000000000000000000000000001",
//                                                "title": "dummy-title-01",
//                                                "description": "dummy-description-01",
//                                                "body": "dummy-body-01"
//                                            },
//                                            {
//                                                "slug": "slug0000000000000000000000000002",
//                                                "title": "dummy-title-02",
//                                                "description": "dummy-description-02",
//                                                "body": "dummy-body-02"
//                                            }
//                                        ]
//                                    }
//                                """
//                            )
//                        ]
//                    )
//                ]
//            ),
//        ]
//    )
//    @GetMapping("/api/articles", produces = [MediaType.APPLICATION_JSON_VALUE])
//    fun getArticles(): ResponseEntity<MultipleArticleResponse> {
//        val createdArticles = feedArticleUseCase.execute().getOrElse { throw UnsupportedOperationException("想定外のエラー") }
//
//        return ResponseEntity(
//            MultipleArticleResponse(
//                articleCount = createdArticles.articlesCount,
//                articles = createdArticles.articles.map {
//                    Article(
//                        slug = it.slug.value,
//                        title = it.title.value,
//                        description = it.description.value,
//                        body = it.body.value,
//                    )
//                }
//            ),
//            HttpStatus.OK
//        )
//    }
//
//    /**
//     * 作成済記事更新
//     *
//     * @return
//     */
//    @Operation(
//        summary = "作成済記事更新",
//        operationId = "UpdateArticle",
//        description = "作成済記事を更新します",
//        tags = ["articles"],
//        responses = [
//            ApiResponse(
//                responseCode = "200",
//                description = "OK",
//                content = [
//                    Content(
//                        schema = Schema(implementation = SingleArticleResponse::class),
//                        examples = [
//                            ExampleObject(
//                                name = "OK",
//                                value = """
//                                    {
//                                        "article": {
//                                            "slug": "283e60096c26aa3a39cf04712cdd1ff7",
//                                            "title": "updated-article-title",
//                                            "description": "updated-article-description",
//                                            "body": "updated-article-body"
//                                        }
//                                    }
//                                """
//                            )
//                        ]
//                    )
//                ]
//            ),
//            ApiResponse(
//                responseCode = "400",
//                description = "Validation Error",
//                content = [
//                    Content(
//                        schema = Schema(implementation = GenericErrorModel::class),
//                        examples = [
//                            ExampleObject(
//                                name = "Validation Error",
//                                value = """
//                                    {
//                                        "errors": {
//                                            "body": [
//                                                "article.titleは必須です",
//                                                "article.bodyは0文字以上64文字以下です",
//                                                "article.descriptionは0文字以上1024文字以下です"
//                                            ]
//                                        }
//                                    }
//                                """
//                            )
//                        ]
//                    )
//                ]
//            ),
//            ApiResponse(
//                responseCode = "404",
//                description = "Not Found",
//                content = [
//                    Content(
//                        schema = Schema(implementation = GenericErrorModel::class),
//                        examples = [
//                            ExampleObject(
//                                name = "Not Found",
//                                value = """
//                                    {
//                                        "errors": {
//                                            "body": [
//                                                "slug に該当する記事は見つかりませんでした"
//                                            ]
//                                        }
//                                    }
//                                """
//                            )
//                        ]
//                    )
//                ]
//            )
//        ]
//    )
//    @PutMapping("/api/articles/{slug}", produces = [MediaType.APPLICATION_JSON_VALUE])
//    fun updateArticle(
//        @Parameter(description = "記事の slug", required = true, schema = Schema(minLength = 32, maxLength = 32)) @Valid @PathVariable("slug") @Length(min = 32, max = 32) slug: String,
//        @Valid @RequestBody updateArticleRequest: UpdateArticleRequest,
//    ): ResponseEntity<SingleArticleResponse> {
//        val updatedArticle = updateArticleUseCase.execute(
//            slug = slug,
//            title = updateArticleRequest.validatedArticle.validatedTitle,
//            description = updateArticleRequest.validatedArticle.validatedDescription,
//            body = updateArticleRequest.validatedArticle.validatedBody,
//        ).getOrElse { throw UpdateArticleUseCaseErrorException(it) }
//
//        return ResponseEntity(
//            SingleArticleResponse(
//                article = Article(
//                    slug = updatedArticle.slug.value,
//                    title = updatedArticle.title.value,
//                    description = updatedArticle.description.value,
//                    body = updatedArticle.body.value,
//                ),
//            ),
//            HttpStatus.OK
//        )
//    }
//
//    /**
//     * 記事更新ユースケースがエラーを戻したときの Exception
//     *
//     * このクラスの例外が発生したときに、@ExceptionHandler で例外をおこなう
//     *
//     * @property error
//     */
//    data class UpdateArticleUseCaseErrorException(val error: UpdateArticleUseCase.Error) : Throwable()
//
//    /**
//     * UpdateArticleUseCaseErrorException をハンドリングする関数
//     *
//     * UpdateArticleUseCase.Error の型に合わせてレスポンスを分岐する
//     *
//     * @param e
//     * @return
//     */
//    @ExceptionHandler(value = [UpdateArticleUseCaseErrorException::class])
//    fun onUpdateArticleUseCaseErrorException(e: UpdateArticleUseCaseErrorException): ResponseEntity<GenericErrorModel> =
//        when (val error = e.error) {
//            is UpdateArticleUseCase.Error.NotFoundArticleBySlug -> ResponseEntity(
//                GenericErrorModel(
//                    errors = GenericErrorModelErrors(
//                        body = listOf("${error.slug.value} に該当する記事は見つかりませんでした")
//                    )
//                ),
//                HttpStatus.NOT_FOUND
//            )
//
//            is UpdateArticleUseCase.Error.InvalidArticle -> ResponseEntity(
//                GenericErrorModel(
//                    errors = GenericErrorModelErrors(
//                        body = error.errors.map { it.message }
//                    )
//                ),
//                HttpStatus.FORBIDDEN
//            )
//
//            is UpdateArticleUseCase.Error.ValidationErrors -> ResponseEntity(
//                GenericErrorModel(
//                    errors = GenericErrorModelErrors(
//                        body = error.errors.map { it.message }
//                    )
//                ),
//                HttpStatus.FORBIDDEN
//            )
//        }
//
//    /**
//     * 作成済記事削除
//     *
//     * @return
//     */
//    @Operation(
//        summary = "作成済記事削除",
//        operationId = "DeleteArticle",
//        description = "作成済記事を削除します",
//        tags = ["articles"],
//        responses = [
//            ApiResponse(
//                responseCode = "200",
//                description = "OK",
//                content = [
//                    Content(
//                        examples = [
//                            ExampleObject(
//                                name = "OK",
//                                value = "{}"
//                            )
//                        ]
//                    )
//                ]
//            ),
//            ApiResponse(
//                responseCode = "400",
//                description = "Validation Error",
//                content = [
//                    Content(
//                        schema = Schema(implementation = GenericErrorModel::class),
//                        examples = [
//                            ExampleObject(
//                                name = "Validation Error",
//                                value = """
//                                    {
//                                        "errors": {
//                                            "body": [
//                                                "slugは32文字以上32文字以下にしてください"
//                                            ]
//                                        }
//                                    }
//                                """
//                            )
//                        ]
//                    )
//                ]
//            ),
//            ApiResponse(
//                responseCode = "404",
//                description = "Not Found",
//                content = [
//                    Content(
//                        schema = Schema(implementation = GenericErrorModel::class),
//                        examples = [
//                            ExampleObject(
//                                name = "Not Found",
//                                value = """
//                                    {
//                                        "errors": {
//                                            "body": [
//                                                "slug に該当する記事は見つかりませんでした"
//                                            ]
//                                        }
//                                    }
//                                """
//                            )
//                        ]
//                    )
//                ]
//            )
//        ]
//    )
//    @DeleteMapping("/api/articles/{slug}")
//    fun deleteArticle(
//        @Parameter(description = "記事の slug", required = true, schema = Schema(minLength = 32, maxLength = 32)) @Valid @PathVariable("slug") @Length(min = 32, max = 32) slug: String,
//    ): ResponseEntity<Unit> {
//        deleteCreatedArticleUseCase.execute(slug).getOrElse { throw DeleteCreatedArticleUseCaseErrorException(it) }
//
//        return ResponseEntity(Unit, HttpStatus.OK)
//    }
//
//    /**
//     * 記事削除ユースケースがエラーを戻したときの Exception
//     *
//     * このクラスの例外が発生したときに、@ExceptionHandler で例外をおこなう
//     *
//     * @property error
//     */
//    data class DeleteCreatedArticleUseCaseErrorException(val error: DeleteCreatedArticleUseCase.Error) : Exception()
//
//    /**
//     * DeleteArticleUseCaseErrorException をハンドリングする関数
//     *
//     * DeleteArticleUseCase.Error の型に合わせてレスポンスを分岐する
//     *
//     * @param e
//     * @return
//     */
//    @ExceptionHandler(value = [DeleteCreatedArticleUseCaseErrorException::class])
//    fun onDeleteCreatedArticleUseCaseErrorException(e: DeleteCreatedArticleUseCaseErrorException): ResponseEntity<GenericErrorModel> =
//        when (val error = e.error) {
//            is DeleteCreatedArticleUseCase.Error.NotFoundArticleBySlug -> ResponseEntity(
//                GenericErrorModel(
//                    errors = GenericErrorModelErrors(
//                        body = listOf("${error.slug.value} に該当する記事は見つかりませんでした")
//                    )
//                ),
//                HttpStatus.NOT_FOUND
//            )
//
//            is DeleteCreatedArticleUseCase.Error.ValidationErrors -> ResponseEntity(
//                GenericErrorModel(
//                    errors = GenericErrorModelErrors(
//                        body = error.errors.map { it.message }
//                    )
//                ),
//                HttpStatus.FORBIDDEN
//            )
//        }
}