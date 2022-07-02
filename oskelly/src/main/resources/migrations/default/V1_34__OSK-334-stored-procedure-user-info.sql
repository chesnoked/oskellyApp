CREATE OR REPLACE FUNCTION get_user_statistic(_isPro BOOLEAN, _isSeller BOOLEAN, _isNew BOOLEAN, _offset INTEGER,
                                              _limit INTEGER)
  RETURNS TABLE(userId      BIGINT, nickname TEXT, email TEXT, firstName TEXT, lastName TEXT, phone TEXT,
                ordersCount BIGINT, orderItems NUMERIC, products BIGINT, productState TEXT, isPro BOOLEAN, isVip BOOLEAN, isTrusted BOOLEAN) AS $$
SELECT
  uo.uid                        userId,
  uo.unickname                  nickname,
  uo.uemail                     email,
  uo.ufirstName                 firstName,
  uo.ufirstName                 lastName,
  uo.uphone                     phone,
  uo.uordersCount               ordersCount,
  uo.uitems                     orderItems,
  count(p.id)                   products,
  p.product_state               productState,
  uo.uproStatusTime IS NOT NULL isPro,
  uo.uvipStatusTime IS NOT NULL isVip,
  CASE WHEN uo.uisTrusted IS NULL OR uo.uisTrusted = FALSE
    THEN FALSE
  ELSE TRUE END AS              isTrusted
FROM
  (SELECT
     u.id                           AS uid,
     u.nickname                     AS unickname,
     u.email                        AS uemail,
     u.first_name                   AS ufirstName,
     u.last_name                    AS ulastName,
     u.phone                        AS uphone,
     u.pro_status_time              AS uproStatusTime,
     u.vip_status_time              AS uvipStatusTime,
     u.registration_time            AS uregistrationTime,
     u.is_trusted                   AS uisTrusted,
     count(orders.id)               AS uordersCount,
     coalesce(sum(orders.count), 0) AS uitems
   FROM "user" AS u
     LEFT JOIN (SELECT
                  o.id,
                  o.buyer_id,
                  count(op.id) AS count
                FROM "order" o LEFT JOIN order_position op ON o.id = op.order_id
                GROUP BY o.id) orders
       ON orders.buyer_id = u.id
     LEFT JOIN product _p ON _p.seller_id = u.id
   WHERE (_isPro IS NULL OR _isPro = FALSE OR u.pro_status_time IS NOT NULL)
         AND (_isNew IS NULL OR _isNew = FALSE OR u.registration_time :: DATE > (now() - INTERVAL '7 day'))
   GROUP BY uid
   HAVING (_isSeller IS NULL OR _isSeller = FALSE OR count(_p.id) > 0)
   ORDER BY uid
   LIMIT _limit
   OFFSET _offset) uo
  LEFT JOIN product p ON uo.uid = p.seller_id AND p.product_state IN ('PUBLISHED', 'NEED_MODERATION')
GROUP BY userId, nickname, email, firstName, lastName, phone, ordersCount, orderItems, productState, isPro, isVip,
  isTrusted
ORDER BY userId;
$$ LANGUAGE SQL;