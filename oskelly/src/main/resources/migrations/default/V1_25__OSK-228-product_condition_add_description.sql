alter table product_condition
  add COLUMN description TEXT;

update product_condition set description = 'Товар с биркой. Вещь ни разу не надевали.' WHERE id = 1;
update product_condition set description = 'Надевали пару раз, вещь в отличном состоянии.' WHERE id = 2;
update product_condition set description = 'Хорошее состояние товара. Незначительные следы носки.' WHERE id = 3;
update product_condition set description = 'Удовлетворительное состояние товара. Заметны следы носки.' WHERE id = 4;
