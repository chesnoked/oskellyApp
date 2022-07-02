//
// Created by Виталий Хлудеев on 04.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountOrderRow : UITableViewCell {

    let brand = UILabel()
    let productName = UILabel()
    let size = UILabel()
    let productId = UILabel()
    let price = UILabel()
    let delivery = UILabel()
    let img = UIImageView()
    let separator = UIView()
    let stubImg = UIImage(named: "assets/images/publication/DraftPlaceHolder.png")

    let elementsOffset: Int = 12
    let labelHeight: Int = 16

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none
        addImg()
        addBrand()
        addProductName()
        addSize()
        addProductId()
        addPrice()
        addDelivery()
        addSeparator()
    }

    private func addImg() {
        contentView.addSubview(img)
        img.contentMode = .scaleAspectFit
        img.image = stubImg
        img.snp.makeConstraints({ m in
            m.left.equalTo(contentView).inset(15)
            m.width.equalTo(110)
            m.height.equalTo(110 * 4 / 3)
            m.top.equalTo(contentView).inset(15)
        })
    }


    private func remakeProductIdConstraints(_ sizeExist: Bool) {
        if(sizeExist) {
            productId.snp.remakeConstraints({ m in
                m.left.equalTo(size)
                makeTopOffset(m, size)
                m.height.equalTo(labelHeight)
            })
        }
        else {
            productId.snp.makeConstraints({ m in
                m.left.equalTo(size)
                makeTopOffset(m, productName)
                m.height.equalTo(labelHeight)
            })
        }
    }

    private func addProductName() {
        contentView.addSubview(productName)
        productName.font = MediumFont.systemFont(ofSize: 14)
        productName.snp.makeConstraints({ m in
            m.left.equalTo(brand)
            makeTopOffset(m, brand)
            m.height.equalTo(labelHeight)
        })
    }

    private func addSize() {
        contentView.addSubview(size)
        size.font = productName.font
        size.snp.makeConstraints({ m in
            m.left.equalTo(productName)
            makeTopOffset(m, productName)
            m.height.equalTo(labelHeight)
        })
    }

    private func addBrand() {
        contentView.addSubview(brand)
        brand.font = BoldFont.systemFont(ofSize: 15)
        brand.snp.makeConstraints({m in
            m.top.equalTo(img)
            m.left.equalTo(img.snp.right).offset(elementsOffset)
            m.height.equalTo(labelHeight)
        })
    }

    private func addProductId() {
        contentView.addSubview(productId)
        productId.font = size.font
        productId.snp.makeConstraints({ m in
            m.left.equalTo(size)
            makeTopOffset(m, size)
            m.height.equalTo(labelHeight)
        })
    }

    private func addPrice() {
        contentView.addSubview(price)
        price.font = brand.font
        price.snp.makeConstraints({ m in
            m.left.equalTo(productId)
            m.height.equalTo(labelHeight)
            makeTopOffset(m, productId)
        })
    }

    private func addDelivery() {
        contentView.addSubview(delivery)
        delivery.font = productId.font
        delivery.snp.makeConstraints({ m in
            m.left.equalTo(productId)
            m.height.equalTo(labelHeight)
            makeTopOffset(m, price)
            m.bottom.equalTo(contentView).inset(12)
        })
    }

    private func addSeparator() {
        let separator = UIView()
        separator.backgroundColor = AppColors.grayLabel()
        contentView.addSubview(separator)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }
    }

    private func makeTopOffset(_ m: ConstraintMaker, _ element: UIView) -> ConstraintMakerEditable {
        return m.top.equalTo(element.snp.bottom).offset(elementsOffset)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func render(orderPosition: OrderPosition) {

        img.image = stubImg
        if let imageUrl = orderPosition.imageUrl {
            let url = URL(string: ApiRequester.domain + imageUrl)!
            img.af_setImage(withURL: url, placeholderImage: stubImg)
        }

        brand.text = orderPosition.brandName
        productName.text = orderPosition.productName
        size.text = orderPosition.productSize
        productId.text = "ID: " + String(orderPosition.productItemId)
        price.text = String(orderPosition.productPrice)  + " ₽"
        delivery.text = "Доставка: " + (orderPosition.deliveryCost != nil ? String(orderPosition.deliveryCost!) : "0") + " ₽"

        remakeProductIdConstraints(orderPosition.productSize != nil)
    }
}