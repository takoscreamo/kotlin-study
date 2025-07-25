package com.example.implementingserversidekotlindevelopment.infra

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.implementingserversidekotlindevelopment.domain.ArticleRepository
import com.example.implementingserversidekotlindevelopment.domain.Body
import com.example.implementingserversidekotlindevelopment.domain.CreatedArticle
import com.example.implementingserversidekotlindevelopment.domain.Description
import com.example.implementingserversidekotlindevelopment.domain.Slug
import com.example.implementingserversidekotlindevelopment.domain.Title
//import com.example.implementingserversidekotlindevelopment.domain.UpdatableCreatedArticle
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

/**
 * 作成済記事リポジトリの具象クラス
 *
 * @property namedParameterJdbcTemplate
 */
@Repository
class ArticleRepositoryImpl(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : ArticleRepository {
    override fun findBySlug(slug: Slug): Either<ArticleRepository.FindBySlugError, CreatedArticle> {
        val sql = """
            SELECT
                articles.slug
                , articles.title
                , articles.body
                , articles.description
            FROM
                articles
            WHERE
                slug = :slug
        """.trimIndent()
        val articleMapList = namedParameterJdbcTemplate.queryForList(sql, MapSqlParameterSource().addValue("slug", slug.value))

        /**
         * DB から作成済記事が見つからなかった場合、早期 return
         */
        if (articleMapList.isEmpty()) {
            return ArticleRepository.FindBySlugError.NotFound(slug = slug).left()
        }

        val articleMap = articleMapList.first()

        return CreatedArticle.newWithoutValidation(
            slug = Slug.newWithoutValidation(articleMap["slug"].toString()),
            title = Title.newWithoutValidation(articleMap["title"].toString()),
            description = Description.newWithoutValidation(articleMap["description"].toString()),
            body = Body.newWithoutValidation(articleMap["body"].toString()),
        ).right()
    }

//    override fun create(createdArticle: CreatedArticle): Either<ArticleRepository.CreateArticleError, CreatedArticle> {
//        val sql = """
//            INSERT INTO articles (
//                slug
//                , title
//                , body
//                , description
//            )
//            VALUES (
//                :slug
//                , :title
//                , :body
//                , :description
//            )
//        """.trimIndent()
//        namedParameterJdbcTemplate.update(
//            sql,
//            MapSqlParameterSource()
//                .addValue("slug", createdArticle.slug.value)
//                .addValue("title", createdArticle.title.value)
//                .addValue("body", createdArticle.body.value)
//                .addValue("description", createdArticle.description.value)
//        )
//        return createdArticle.right()
//    }
//
//    override fun all(): Either<ArticleRepository.FindError, List<CreatedArticle>> {
//        val sql = """
//            SELECT
//                articles.slug
//                , articles.title
//                , articles.body
//                , articles.description
//            FROM
//                articles
//            ;
//        """.trimIndent()
//        val articleMap = namedParameterJdbcTemplate.queryForList(sql, MapSqlParameterSource())
//        return articleMap.map {
//            CreatedArticle.newWithoutValidation(
//                Slug.newWithoutValidation(it["slug"].toString()),
//                Title.newWithoutValidation(it["title"].toString()),
//                Description.newWithoutValidation(it["description"].toString()),
//                Body.newWithoutValidation(it["body"].toString())
//            )
//        }.right()
//    }
//
//    override fun update(
//        slug: Slug,
//        updatableCreatedArticle: UpdatableCreatedArticle,
//    ): Either<ArticleRepository.UpdateError, CreatedArticle> {
//        /**
//         * slug に該当する作成済記事を調べる
//         */
//        val findArticleSql = """
//            SELECT
//                articles.slug
//                , articles.title
//                , articles.body
//                , articles.description
//            FROM
//                articles
//            WHERE
//                slug = :slug
//        """.trimIndent()
//        val articleMapList =
//            namedParameterJdbcTemplate.queryForList(
//                findArticleSql,
//                MapSqlParameterSource().addValue("slug", slug.value)
//            )
//
//        /**
//         * DB から作成済記事が見つからなかった場合、早期 return
//         */
//        if (articleMapList.isEmpty()) {
//            return ArticleRepository.UpdateError.NotFound(slug = slug).left()
//        }
//
//        /**
//         * 記事を更新
//         */
//        val sql = """
//            UPDATE
//                articles
//            SET
//                title = :title
//                , body = :body
//                , description = :description
//            WHERE
//                slug = :slug
//        """.trimIndent()
//        namedParameterJdbcTemplate.update(
//            sql,
//            MapSqlParameterSource()
//                .addValue("slug", slug.value)
//                .addValue("title", updatableCreatedArticle.title.value)
//                .addValue("body", updatableCreatedArticle.body.value)
//                .addValue("description", updatableCreatedArticle.description.value)
//        )
//        return CreatedArticle.newWithoutValidation(
//            slug = slug,
//            title = updatableCreatedArticle.title,
//            body = updatableCreatedArticle.body,
//            description = updatableCreatedArticle.description
//        ).right()
//    }
//
//    override fun delete(slug: Slug): Either<ArticleRepository.DeleteError, Unit> {
//        /**
//         * slug に該当する作成済記事を調べる
//         */
//        val findArticleSql = """
//            SELECT
//                articles.slug
//                , articles.title
//                , articles.body
//                , articles.description
//            FROM
//                articles
//            WHERE
//                slug = :slug
//            ;
//        """.trimIndent()
//        val articleMapList =
//            namedParameterJdbcTemplate.queryForList(
//                findArticleSql,
//                MapSqlParameterSource().addValue("slug", slug.value)
//            )
//
//        /**
//         * DB から作成済記事が見つからなかった場合、早期 return
//         */
//        if (articleMapList.isEmpty()) {
//            return ArticleRepository.DeleteError.NotFound(slug = slug).left()
//        }
//
//        /**
//         * 記事を削除
//         */
//        val sql = """
//            DELETE FROM
//                articles
//            WHERE
//                slug = :slug
//            ;
//        """.trimIndent()
//        namedParameterJdbcTemplate.update(
//            sql,
//            MapSqlParameterSource()
//                .addValue("slug", slug.value)
//        )
//
//        return Unit.right()
//    }
}