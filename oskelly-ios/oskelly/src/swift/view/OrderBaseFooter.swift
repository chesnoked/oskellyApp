//
// Created by Виталий Хлудеев on 01.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class OrderBaseFooter : UIView {

    static let height: CGFloat = 170.0

    let separator = UIView()
    let countLabel = UILabel()
    let deliveryLabel = UILabel()
    let totalLabel = UILabel()
    let payButton = DarkButton()
    let productPriceLabel = UILabel()
    let deliveryPriceLabel = UILabel()
    let totalPriceLabel = UILabel()
    var controller: UIViewController!

    override init(frame: CGRect) {
        super.init(frame: frame)
        addSeparator()
        addSubview(countLabel)
        countLabel.textColor = AppColors.lightGray()
        countLabel.font = BoldFont.systemFont(ofSize: 13)
        countLabel.snp.makeConstraints { make in
            make.left.equalTo(separator).inset(15)
            make.top.equalTo(separator).inset(25)
        }

        addSubview(deliveryLabel)
        deliveryLabel.text = "Доставка:"
        deliveryLabel.font = countLabel.font
        deliveryLabel.textColor = countLabel.textColor
        deliveryLabel.snp.makeConstraints { make in
            make.left.equalTo(countLabel)
            make.top.equalTo(countLabel.snp.bottom).offset(5)
        }

        addSubview(totalLabel)
        totalLabel.font = BlackFont.systemFont(ofSize: 13)
        totalLabel.text = "Всего к оплате: "
        totalLabel.snp.makeConstraints { make in
            make.left.equalTo(countLabel)
            make.top.equalTo(deliveryLabel.snp.bottom).offset(5)
        }

        addSubview(payButton)
        payButton.setTitle("Перейти к оплате", for: .normal)
        payButton.snp.makeConstraints { make in
            make.top.equalTo(totalLabel.snp.bottom).offset(15)
            make.left.equalTo(self).inset(20)
            make.right.equalTo(self).inset(20)
        }

        addSubview(productPriceLabel)
        productPriceLabel.font = countLabel.font
        productPriceLabel.snp.makeConstraints { make in
            make.centerY.equalTo(countLabel)
            make.right.equalTo(separator).inset(15)
        }

        addSubview(deliveryPriceLabel)
        deliveryPriceLabel.font = countLabel.font
        deliveryPriceLabel.snp.makeConstraints { make in
            make.centerY.equalTo(deliveryLabel)
            make.right.equalTo(productPriceLabel)
        }

        addSubview(totalPriceLabel)
        totalPriceLabel.font = totalLabel.font
        totalPriceLabel.snp.makeConstraints { make in
            make.centerY.equalTo(totalLabel)
            make.right.equalTo(productPriceLabel)
        }
    }

    func addSeparator() {
        addSubview(separator)
        separator.backgroundColor = AppColors.lightGray()
        separator.snp.makeConstraints { make in
            make.height.equalTo(2)
            make.top.equalTo(self)
            make.left.equalTo(self)
            make.right.equalTo(self)
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}