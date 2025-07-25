package com.example.implementingserversidekotlindevelopment.domain

import arrow.core.Either
import arrow.core.leftNel
import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.ArbitrarySupplier
import net.jqwik.api.ForAll
import net.jqwik.api.From
import net.jqwik.api.Property
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SlugTest {
    class New {
        @Property
        fun `正常系-文字列が有効な場合、検証済の Slug が戻り値`(
            @ForAll @From(supplier = SlugValidRange::class) validString: String,
        ) {
            /**
             * given:
             */

            /**
             * when:
             */
            val actual = Slug.new(validString)

            /**
             * then:
             */
            val expectedSlug = Slug.newWithoutValidation(validString)
            when (actual) {
                is Either.Left -> assert(false) { "原因: ${actual.value}" }
                is Either.Right -> assertThat(actual.value.value).isEqualTo(expectedSlug.value)
            }
        }

        @Property
        fun `異常系-文字列が有効でない場合、バリデーションエラーが戻り値`(
            @ForAll @From(supplier = SlugInvalidRange::class) invalidString: String,
        ) {
            /**
             * given:
             */

            /**
             * when:
             */
            val actual = Slug.new(invalidString)

            /**
             * then:
             */
            val expected = Slug.CreationError.ValidFormat(invalidString).leftNel()
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun `正常系-new() で生成される文字列は 32 文字の英数字`() {
            /**
             * given:
             */

            /**
             * when:
             */
            val actual = Slug.new().value

            /**
             * then:
             */
            val expectedPattern = "^[a-z0-9]{32}$"
            assertThat(actual).matches(expectedPattern)
        }
    }
    /**
     * Slug の有効な範囲の String プロパティ
     */
    class SlugValidRange : ArbitrarySupplier<String> {
        override fun get(): Arbitrary<String> =
            Arbitraries.strings()
                .numeric()
                .withCharRange('a', 'z')
                .ofLength(32)
    }

    /**
     * Slug の無効な範囲の String プロパティ
     */
    class SlugInvalidRange : ArbitrarySupplier<String> {
        override fun get(): Arbitrary<String> =
            Arbitraries.strings()
                .filter { !it.matches(Regex("^[a-z0-9]{32}$")) }
    }
}