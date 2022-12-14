package com.hyoseok.service.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.hyoseok.sns.entity.Sns
import com.hyoseok.sns.entity.SnsCache
import com.hyoseok.sns.entity.SnsImage
import com.hyoseok.sns.entity.SnsTag
import java.time.LocalDateTime

data class SnsCreateDto(
    val memberId: Long,
    val title: String,
    val contents: String,
    val writer: String,
    val images: List<SnsImageDto>,
    val tag: SnsTagDto,
    val productIds: List<Long>,
) {
    fun toEntity() =
        Sns(
            memberId = memberId,
            title = title,
            contents = contents,
            writer = writer,
            productIds = productIds,
            snsImages = SnsImageDto.toEntities(images = images),
            snsTag = tag.toEntity(),
        )
}

data class SnsCreateResultDto(
    val snsId: Long,
) {
    companion object {
        operator fun invoke(sns: Sns) = SnsCreateResultDto(snsId = sns.id!!)
    }
}

data class SnsEditDto(
    val id: Long,
    val memberId: Long,
    val title: String,
    val contents: String,
    val writer: String,
    val images: List<SnsImageDto>,
    val tag: SnsTagDto,
    val productIds: List<Long>,
)

data class SnsFindResultDto(
    val id: Long,
    val memberId: Long,
    val title: String,
    val contents: String,
    val writer: String,
    val isDisplay: Boolean,
    val images: List<SnsImageDto>,
    val tag: SnsTagDto,
    val products: List<ProductFindResultDto>,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val updatedAt: LocalDateTime,
) {
    companion object {
        operator fun invoke(snsCache: SnsCache) =
            with(receiver = snsCache) {
                SnsFindResultDto(
                    id = id,
                    memberId = memberId,
                    title = title,
                    contents = contents,
                    writer = writer,
                    isDisplay = isDisplay,
                    images = snsImages.map { SnsImageDto(url = "https://test/${it.url}", sortOrder = it.sortOrder) },
                    tag = with(receiver = snsTag) { SnsTagDto(type = type.name.lowercase(), values = values) },
                    products = products.map { ProductFindResultDto(externalProduct = it) },
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                )
            }
    }
}

data class SnsImageDto(
    val url: String,
    val sortOrder: Int,
) {
    companion object {
        fun toEntities(images: List<SnsImageDto>) =
            SnsImage.createSnsImages(snsImages = images.map { Pair(first = it.url, second = it.sortOrder) })
    }
}

data class SnsTagDto(
    val type: String,
    val values: List<String>,
) {
    fun toEntity() = SnsTag(type = type, values = values)
}
