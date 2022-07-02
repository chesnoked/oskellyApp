DROP FUNCTION IF EXISTS get_user_statistic(BOOLEAN,BOOLEAN,BOOLEAN,INTEGER, INTEGER);

CREATE OR REPLACE FUNCTION get_user_statistic(_isPro BOOLEAN, _isSeller BOOLEAN, _isNew BOOLEAN, _offset INTEGER,
																							_limit INTEGER)
	RETURNS TABLE(userId      BIGINT, nickname TEXT, email TEXT, firstName TEXT, lastName TEXT, phone TEXT,
								isPro       BOOLEAN, isVip BOOLEAN, isTrusted BOOLEAN,
								ordersCount BIGINT, orderItems BIGINT, products BIGINT, productState TEXT) AS $$
SELECT
	u.id                               AS userId,
	u.nickname                         AS nickname,
	u.email                            AS email,
	u.first_name                       AS firstName,
	u.last_name                        AS lastName,
	u.phone                            AS phone,
	u.pro_status_time IS NOT NULL      AS isPro,
	u.vip_status_time IS NOT NULL      AS isVip,
	CASE WHEN u.is_trusted IS NULL OR u.is_trusted = FALSE
		THEN FALSE
	ELSE TRUE END                      AS isTrusted,
	coalesce(count(DISTINCT o.id), 0)  AS ordersCount,
	coalesce(count(DISTINCT op.id), 0) AS orderItems,
	count(DISTINCT p.id)               AS products,
	p.product_state                    AS productState
FROM "user" u LEFT JOIN "order" o ON u.id = o.buyer_id
	LEFT JOIN order_position op ON o.id = op.order_id
	LEFT JOIN product p ON p.seller_id = u.id AND p.product_state IN ('PUBLISHED', 'NEED_MODERATION')
	JOIN (SELECT u1.id sellerId
				FROM "user" u1 LEFT JOIN product p1 ON u1.id = p1.seller_id
				WHERE (_isPro IS NULL OR _isPro = FALSE OR u1.pro_status_time IS NOT NULL)
							AND (_isNew IS NULL OR _isNew = FALSE OR u1.registration_time :: DATE > (now() - INTERVAL '7 day'))
				GROUP BY u1.id
				HAVING (_isSeller IS NULL OR _isSeller = FALSE OR count(p1.id) > 0)
				ORDER BY u1.id
				LIMIT _limit
				OFFSET _offset) sellers ON sellers.sellerId = u.id
GROUP BY u.id, p.product_state
HAVING (_isSeller IS NULL OR _isSeller = FALSE OR count(p.id) > 0)
ORDER BY userId
$$ LANGUAGE SQL;