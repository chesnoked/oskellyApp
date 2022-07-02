//
// Created by Виталий Хлудеев on 09.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountOrderFooter : UITableViewCell {

    let countLabel = UILabel()
    let deliveryLabel = UILabel()
    let totalLabel = UILabel()
    let productPriceLabel = UILabel()
    let productPriceDiscountLabel = UILabel()
    let deliveryPriceLabel = UILabel()
    let totalPriceLabel = UILabel()
    let totalPriceDiscountLabel = UILabel()
    let topSeparator = UIView()
    let separator = UIView()

    let minWidth = min(UIScreen.main.bounds.width, UIScreen.main.bounds.height)

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)

        contentView.addSubview(countLabel)
        countLabel.text = "Общая сумма заказа:"
        countLabel.textColor = AppColors.lightGray()
        countLabel.font = BoldFont.systemFont(ofSize: 13)
        countLabel.snp.makeConstraints { make in
            make.left.equalTo(contentView).inset(15)
            make.top.equalTo(contentView).inset(15)
        }

        contentView.addSubview(deliveryLabel)
        deliveryLabel.text = "Стоимость доставки:"
        deliveryLabel.font = countLabel.font
        deliveryLabel.textColor = countLabel.textColor
        deliveryLabel.snp.makeConstraints { make in
            make.left.equalTo(countLabel)
            make.top.equalTo(countLabel.snp.bottom).offset(5)
        }

        contentView.addSubview(totalLabel)
        totalLabel.font = countLabel.font
        totalLabel.text = "Итого: "
        totalLabel.snp.makeConstraints { make in
            make.left.equalTo(countLabel)
            make.top.equalTo(deliveryLabel.snp.bottom).offset(5)
        }

        contentView.addSubview(productPriceLabel)
        productPriceLabel.font = countLabel.font
        productPriceLabel.snp.makeConstraints { make in
            make.centerY.equalTo(countLabel)
            make.right.equalTo(contentView.snp.left).inset(minWidth - 15)
        }

        contentView.addSubview(deliveryPriceLabel)
        deliveryPriceLabel.font = countLabel.font
        deliveryPriceLabel.snp.makeConstraints { make in
            make.centerY.equalTo(deliveryLabel)
            make.right.equalTo(productPriceLabel)
        }

        contentView.addSubview(totalPriceLabel)
        totalPriceLabel.font = totalLabel.font
        totalPriceLabel.snp.makeConstraints { make in
            make.centerY.equalTo(totalLabel)
            make.right.equalTo(productPriceLabel)
            make.bottom.equalTo(contentView).inset(10)
        }

        addSeparator()
        addSeparatorTop()

        addProductPriceDiscountLabel()
        addTotalPriceDiscountLabel()
    }

    private func addTotalPriceDiscountLabel() {
        contentView.addSubview(totalPriceDiscountLabel)
        totalPriceDiscountLabel.font = productPriceDiscountLabel.font
        totalPriceDiscountLabel.snp.makeConstraints({ m in
            m.right.equalTo(productPriceDiscountLabel)
            m.top.equalTo(totalPriceLabel)
        })

    }

    private func addProductPriceDiscountLabel() {
        let buffer = UIView()
        contentView.addSubview(buffer)
        buffer.snp.makeConstraints({m in
            m.width.equalTo(15)
            m.right.equalTo(productPriceLabel.snp.left)
        })

        contentView.addSubview(productPriceDiscountLabel)
        productPriceDiscountLabel.font = MediumFont.systemFont(ofSize: 13)
        productPriceDiscountLabel.snp.makeConstraints({ m in
            m.right.equalTo(buffer.snp.left)
            m.top.equalTo(productPriceLabel)
        })

    }

    private func addSeparator() {
        separator.backgroundColor = AppColors.separator()
        contentView.addSubview(separator)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }
    }

    private func addSeparatorTop() {
        topSeparator.backgroundColor = AppColors.separator()
        contentView.addSubview(topSeparator)
        topSeparator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(2)
            make.top.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }
    }

    func render(order: Order) {
        render(order: order, addSeparators: true)
    }

    func render(order: Order, addSeparators: Bool) {
        productPriceLabel.text = order.appliedDiscount?.updatedOrderAmount.map({$0 + " ₽"}) ?? (String(order.price) + " ₽")
        productPriceDiscountLabel.attributedText = order.appliedDiscount?.code != nil ? Utils.getCrossedText(text: String(order.price) + " ₽") : nil

        deliveryPriceLabel.text = String(order.deliveryCost) + " ₽"

        totalPriceLabel.text = order.appliedDiscount?.finalOrderAmount.map({$0 + " ₽"}) ?? (String(order.price + order.deliveryCost) + " ₽")
        totalPriceDiscountLabel.attributedText = order.appliedDiscount?.code != nil ? Utils.getCrossedText(text: String(order.price + order.deliveryCost) + " ₽") : nil

        separator.snp.remakeConstraints { (make) -> Void in
            make.height.equalTo(addSeparators ? 1 : 0)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }

        topSeparator.snp.remakeConstraints { (make) -> Void in
            make.height.equalTo(addSeparators ? 2 : 0)
            make.top.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}