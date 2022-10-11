package com.rick.data_book.model

fun GutenbergResponseDto.toGutenBergResponse(): GutenbergResponse = GutenbergResponse(
    count = count,
    next = next ?: "",
    previous = previous,
    books = results.map { it.toBook() }
)

fun BookDto.toBook(): Book = Book(
    id = id,
    title = title,
    authors = authors,
    bookshelves = bookshelves,
    copyright = copyright,
    downloads = downloadCount,
    formats = formats.toFormats(),
    languages = languages,
    mediaType = mediaType,
    subjects = subjects
)

fun FormatsDto.toFormats(): Formats = Formats(
    image = imagejpeg,
    textHtml = texthtml,
    textPlain = textplain,
    textHtmCharsetUtf8 = texthtmlCharsetutf8,
    textHtmlCharsetIso88591 = texthtmlCharsetiso88591,
    textHtmlCharsetUsAscii = texthtmlAscii,
    textPlainCharsetUsAscii = textplainCharsetusAscii,
    textPlainCharsetUtf8 = textplainCharsetutf8

)

fun TranslatorDto.toTranslators(): Translator = Translator(
    name = name
)