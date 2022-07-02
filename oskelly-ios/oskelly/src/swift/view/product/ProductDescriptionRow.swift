//
// Created by Виталий Хлудеев on 24.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProductDescriptionRow : UITableViewCell {

    let brandNameLabel = UILabel()
    let productDescriptionLabel = UILabel()
    let productNameLabel = UILabel()
    let priceLabel = UILabel()
    let startPriceLabel = UILabel()
    let stripeView = UIView()


    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addBrandName(container: contentView)
        addProductName(container: contentView)
        addProductDescription(container: contentView)
        addStartPrice(container: contentView)
        addPrice(container: contentView)
        addStripe(container: contentView)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func addBrandName(container: UIView) {
        brandNameLabel.font = BlackFont.systemFont(ofSize: 16)
        brandNameLabel.text = "GOYARD"
        brandNameLabel.textAlignment = .center
        container.addSubview(brandNameLabel)
        brandNameLabel.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(container.snp.width)
            make.top.equalTo(container.snp.top).offset(10)
            make.centerX.equalTo(container.snp.centerX)
        }
    }

    func addProductName(container: UIView) {
        productNameLabel.font = BoldFont.systemFont(ofSize: 12)
        productNameLabel.textColor = .lightGray
        productNameLabel.text = "Тканевая сумка"
        productNameLabel.textAlignment = .center
        container.addSubview(productNameLabel)
        productNameLabel.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(container.snp.width)
            make.top.equalTo(brandNameLabel.snp.bottom).offset(7)
            make.centerX.equalTo(container.snp.centerX)
        }
    }

    func addProductDescription(container: UIView) {
        productDescriptionLabel.font = productNameLabel.font
        productDescriptionLabel.textColor = .lightGray
        productDescriptionLabel.text = "Размер: S (UK) | Очень хорошее состояние"
        productDescriptionLabel.textAlignment = .center
        container.addSubview(productDescriptionLabel)
        productDescriptionLabel.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(container.snp.width)
            make.top.equalTo(productNameLabel.snp.bottom).offset(3)
            make.centerX.equalTo(container.snp.centerX)
        }
    }

    func addStartPrice(container: UIView) {
        startPriceLabel.font = BoldFont.systemFont(ofSize: 13)
        startPriceLabel.text = "14408 Р"
        startPriceLabel.textAlignment = .center
        container.addSubview(startPriceLabel)
        startPriceLabel.snp.makeConstraints { (make) -> Void in
            make.top.equalTo(productDescriptionLabel.snp.bottom).offset(12)
            make.right.equalTo(container.snp.centerX)
        }
    }

    func addPrice(container: UIView) {
        priceLabel.font = BoldFont.systemFont(ofSize: 13)
        priceLabel.text = "14408 Р"
        priceLabel.textAlignment = .center
        container.addSubview(priceLabel)
        priceLabel.snp.makeConstraints { (make) -> Void in
            make.top.equalTo(startPriceLabel)
            make.left.equalTo(startPriceLabel.snp.right).offset(10)
        }
    }

    func addStripe(container: UIView) {
        stripeView.backgroundColor = .lightGray
        container.addSubview(stripeView)
        stripeView.snp.makeConstraints { (make) -> Void in
            make.centerX.equalTo(container.snp.centerX)
            make.height.equalTo(1)
            make.width.equalTo(container.snp.width).dividedBy(2.5)
            make.top.equalTo(priceLabel.snp.bottom).offset(12)
            make.bottom.equalTo(container.snp.bottom)
        }
    }

    func render(product: Product) {
        brandNameLabel.text = product.brand
        productNameLabel.text = product.category

        if(product.size?.type != "Без размера") {
            let sizeValuesString = product.size?.values?.map({ $0.value! }).joined(separator: "; ")
            productDescriptionLabel.text = "Размер: "
            if let t = product.size?.type {
                productDescriptionLabel.text = productDescriptionLabel.text! + t
            }
            productDescriptionLabel.text = productDescriptionLabel.text! + " " + sizeValuesString!
            productDescriptionLabel.text = productDescriptionLabel.text! + " | " + product.condition!
        }
        else {
            productDescriptionLabel.text = product.condition!
        }

        if let price = product.currentPrice {
            priceLabel.text = String(price) + " ₽"
        }
        else {
            priceLabel.text = nil
        }
        priceLabel.textColor = (product.hasDiscount ?? false) ? .red : .black
        priceLabel.snp.remakeConstraints { (m) -> Void in
            m.top.equalTo(startPriceLabel)
            if(product.hasDiscount ?? false) {
                m.left.equalTo(startPriceLabel.snp.right).offset(10)
            }
            else {
                m.centerX.equalTo(contentView)
            }
        }


        if let price = product.startPrice {
            startPriceLabel.attributedText = Utils.getCrossedText(text: String(price) + " ₽")
        }
        else {
            startPriceLabel.text = nil
        }
        startPriceLabel.isHidden = !(product.hasDiscount ?? false)
    }
}