//
// Created by Виталий Хлудеев on 18.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SaleBaseRow : UITableViewCell {

    let elementsOffset: Int = 12
    let labelHeight: Int = 16

    let order = UILabel()
    let brand = UILabel()
    let productName = UILabel()
    let size = UILabel()
    let productId = UILabel()
    let buyPrice = UILabel()
    let buyPriceWithoutCommission = UILabel()
    let img = UIImageView()
    let separator = UIView()
    let stubImg = UIImage(named: "assets/images/publication/DraftPlaceHolder.png")

    var sale: Sale!
    var controller: UITableViewController!


    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addOrder()
        addImg()
        addBrand()
        addProductName()
        addSize()
        addProductId()
        addBuyPrice()
        addBuyPriceWithoutCommission()
        addSeparator()
    }

    private func addSeparator() {
        contentView.addSubview(separator)
        separator.backgroundColor = AppColors.separator()
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }
    }

    private func addBuyPriceWithoutCommission() {
        contentView.addSubview(buyPriceWithoutCommission)
        buyPriceWithoutCommission.font = brand.font
        buyPriceWithoutCommission.snp.makeConstraints({ m in
            m.left.equalTo(productId)
            m.height.equalTo(labelHeight)
            makeTopOffset(m, buyPrice)
        })
    }

    private func addBuyPrice() {
        contentView.addSubview(buyPrice)
        buyPrice.font = brand.font
        buyPrice.snp.makeConstraints({ m in
            m.left.equalTo(productId)
            m.height.equalTo(labelHeight)
            makeTopOffset(m, productId)
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

    private func addSize() {
        contentView.addSubview(size)
        size.font = productName.font
        size.snp.makeConstraints({ m in
            m.left.equalTo(productName)
            makeTopOffset(m, productName)
            m.height.equalTo(labelHeight)
        })
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

    func makeTopOffset(_ m: ConstraintMaker, _ element: UILabel) -> ConstraintMakerEditable {
        return m.top.equalTo(element.snp.bottom).offset(elementsOffset)
    }

    private func addImg() {
        contentView.addSubview(img)
        img.contentMode = .scaleAspectFit
        img.image = stubImg
        img.snp.makeConstraints({ m in
            makeTopOffset(m, order)
            m.left.equalTo(order)
            m.width.equalTo(110)
            m.height.equalTo(110).multipliedBy(4).dividedBy(3)
            makeTopOffset(m, order)
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

    private func addOrder() {
        contentView.addSubview(order)
        order.font = BlackFont.systemFont(ofSize: 16)
        order.snp.makeConstraints({m in
            m.top.equalTo(contentView).inset(elementsOffset)
            m.left.equalTo(contentView).inset(elementsOffset)
            m.height.equalTo(labelHeight)
        })
    }

    func render(sale : Sale, controller: UITableViewController) {
        self.sale = sale
        self.controller = controller
        order.text = "Продажа по заказу " + sale.orderId!
        brand.text = sale.brandName
        productName.text = sale.productName
        if let s = sale.size {
            size.text = sale.size
            size.isHidden = false
        }
        else {
            size.isHidden = true
        }
        self.remakeProductIdConstraints(sale.size != nil)
        productId.text = "ID товара: " + String(sale.productId!)
        buyPrice.text = "Цена: " + String(sale.buyPrice!)
        buyPriceWithoutCommission.text = "Без комиссии: " + String(sale.buyPriceWithoutCommission!)

        if let imageUrl = sale.image {
            let url = URL(string: ApiRequester.domain + imageUrl)!
            img.af_setImage(withURL: url, placeholderImage: stubImg)
        }
        else {
            img.image = stubImg
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}