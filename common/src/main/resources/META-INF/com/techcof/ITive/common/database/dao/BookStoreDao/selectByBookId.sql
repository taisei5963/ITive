SELECT
    amazon_url,
    rakuten_url,
    kindle_url
FROM
    book_store
WHERE
    book_id = /* bookId */1
  AND
    deleted_at IS NULL