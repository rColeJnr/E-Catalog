package com.rick.ecatalogs.game.data_game.model

/** IGDB image sizes, https://api-docs.igdb.com/#images */
enum class IgdbImageSize(val path: String) {
    /** 264×374 */
    COVER_BIG("t_cover_big"),
    /** 889×500 */
    SCREENSHOT_BIG("t_screenshot_big"),
    /** 1280×720 */
    HD("t_720p"),
}

fun igdbImageUrl(imageId: String, size: IgdbImageSize = IgdbImageSize.COVER_BIG): String =
    "https://images.igdb.com/igdb/image/upload/${size.path}/$imageId.jpg"

fun Game.coverUrl(size: IgdbImageSize = IgdbImageSize.COVER_BIG): String? =
    coverImageId?.let { igdbImageUrl(it, size) }
