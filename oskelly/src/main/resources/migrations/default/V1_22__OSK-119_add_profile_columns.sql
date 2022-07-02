ALTER TABLE "user"
	ADD COLUMN name TEXT,
	ADD COLUMN city TEXT,
	ADD COLUMN birth_date TIMESTAMPTZ,
	ADD COLUMN is_trusted BOOLEAN,
	ADD COLUMN delivery_address TEXT,
	ADD COLUMN payment_details TEXT,
	ADD COLUMN sex TEXT,
	ADD COLUMN first_name TEXT,
	ADD COLUMN last_name TEXT,
	ADD COLUMN postcode TEXT,
	ADD COLUMN seller_address TEXT,
	ADD COLUMN phone TEXT;