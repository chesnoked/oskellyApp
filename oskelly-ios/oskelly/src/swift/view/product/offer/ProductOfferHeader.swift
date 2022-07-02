//
// Created by Виталий Хлудеев on 09.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProductOfferHeader : UITableViewCell {

    private let headerLabel = UILabel()
    private let img = UIImageView()
    private let brand = UILabel()
    private let productName = UILabel()
    private let startPrice = UILabel()
    private let yourPrice = UILabel()
    private let message = UILabel()

    private let stubImg = UIImage(named: "assets/images/300x400.png")

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        isUserInteractionEnabled = true
        addHeaderLabel()
        addImg()
        addBrand()
        addProductName()
        addStartPrice()
        addYourPrice()
        addMessage()
    }

    private func addMessage() {
        contentView.addSubview(message)
        message.font = yourPrice.font
        message.text = "Продавец отклонил предложение"
        message.numberOfLines = 0
        message.snp.makeConstraints({m in
            m.left.equalTo(yourPrice)
            m.right.equalTo(contentView).inset(10)
            m.top.equalTo(yourPrice.snp.bottom).offset(10)
            m.bottom.equalTo(contentView).inset(35)
        })
    }

    private func addYourPrice() {
        contentView.addSubview(yourPrice)
        yourPrice.font = productName.font
        yourPrice.text = "Ваше предложение:"
        yourPrice.snp.makeConstraints({m in
            m.left.equalTo(startPrice)
            m.right.equalTo(contentView).inset(5)
            m.top.equalTo(startPrice.snp.bottom).offset(7)
        })
    }

    private func addStartPrice() {
        contentView.addSubview(startPrice)
        startPrice.text = "Начальная цена:"
        startPrice.numberOfLines = 2
        startPrice.snp.makeConstraints({ m in
            m.left.equalTo(productName)
            m.right.equalTo(contentView).inset(5)
            m.top.equalTo(productName.snp.bottom).offset(7)
        })
    }

    private func addProductName() {
        contentView.addSubview(productName)
        productName.text = "Сапоги"
        productName.font = MediumFont.systemFont(ofSize: 14)
        productName.snp.makeConstraints({m in
            m.left.equalTo(brand)
            m.top.equalTo(brand.snp.bottom).offset(7)
        })
    }

    private func addBrand() {
        contentView.addSubview(brand)
        brand.font = BlackFont.systemFont(ofSize: 15)
        brand.numberOfLines = 2
        brand.text = "GUCCI"
        brand.snp.makeConstraints({ m in
            m.top.equalTo(img)
            m.left.equalTo(img.snp.right).offset(10)
            m.right.equalTo(contentView).inset(5)
        })
    }

    private func addImg() {
        img.image = stubImg
        img.contentMode = .scaleAspectFit
        contentView.addSubview(img)
        img.snp.makeConstraints({m in
            m.left.equalTo(contentView).inset(10)
            m.top.equalTo(headerLabel.snp.bottom).offset(20)
            m.width.equalTo(100)
            m.height.equalTo(100 * 4 / 3)
        })
    }

    private func addHeaderLabel() {
        contentView.addSubview(headerLabel)
        headerLabel.text = "Предложить продавцу снизить цену на этот товар"
        headerLabel.numberOfLines = 0
        headerLabel.textAlignment = .center
        headerLabel.font = MediumFont.systemFont(ofSize: 15)
        headerLabel.snp.makeConstraints({m in
            m.centerX.equalTo(contentView)
            m.top.equalTo(contentView).inset(12)
            m.width.equalTo(contentView).multipliedBy(0.7)
        })
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func render(product: Product) {
        brand.text = product.brand
        productName.text = product.category

        renderStartPrice(product: product)
        renderYourPrice(product: product)

        renderImage(product: product)
        renderMessage(product: product)
    }

    private func renderMessage(product: Product) {
        if let v = product.offerRelated?.offersHistory?.first?.price {
            message.text = product.offerRelated?.negotiationControlText
        }
        else {
            message.text = nil
        }

        message.snp.remakeConstraints({m in
            m.left.equalTo(yourPrice)
            m.right.equalTo(contentView).inset(10)
            m.top.equalTo(yourPrice.snp.bottom).offset(10)
            m.bottom.equalTo(contentView).inset(product.offerRelated?.offersHistory?.first?.price == nil ? 35 : 15)
        })
    }

    private func renderImage(product: Product) {
        img.image = stubImg
        if let im = product.largeImages.first {
            let url = URL(string: ApiRequester.domain + im)!
            img.af_setImage(withURL: url, placeholderImage: stubImg)
        }
    }

    func renderStartPrice(product: Product) {
        let price = (product.price ?? "0") + " ₽"
        let priceWithNbsp = price.replace(target: " ", withString: "\u{00a0}")
        startPrice.attributedText = Utils.attributedText(withString: String(format: "Начальная цена:  %@", priceWithNbsp), boldString: priceWithNbsp, font: MediumFont.systemFont(ofSize: 14), boldFont: BlackFont.systemFont(ofSize: 14))
    }

    func renderYourPrice(product: Product) {
        yourPrice.numberOfLines = 0
        if let v = product.offerRelated?.offersHistory?.first?.price {
            let s = (product.offerRelated?.offersHistory?.count ?? 0) < 2 ? "Ваше предложение" : "Повторное предложение"
            let price = v + " ₽"
            let priceWithNbsp = price.replace(target: " ", withString: "\u{00a0}")
            yourPrice.attributedText = Utils.attributedText(withString: String(format: s + ":  %@", priceWithNbsp), boldString: priceWithNbsp, font: MediumFont.systemFont(ofSize: 14), boldFont: BlackFont.systemFont(ofSize: 14))
        } else {
            yourPrice.text = product.offerRelated?.negotiationControlText
        }
    }
}

extension String
{
    func replace(target: String, withString: String) -> String
    {
        return self.replacingOccurrences(of: target, with: withString, options: NSString.CompareOptions.literal, range: nil)
    }
}