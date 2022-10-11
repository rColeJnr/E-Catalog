package com.rick.data_book.model

fun GutenbergResponseDto.toGutenBergResponse(): GutenbergResponse = GutenbergResponse(
    count = count,
    next = next,
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
    subjects = subjects,
    translators = translators.map { it.toTranslators() }
)

fun FormatsDto.toFormats(): Formats = Formats(
    image = imagejpeg,
    bookLink = texthtml
)

fun TranslatorDto.toTranslators(): Translator = Translator(
    name = name
)