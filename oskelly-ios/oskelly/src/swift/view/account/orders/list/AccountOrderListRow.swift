//
// Created by Виталий Хлудеев on 29.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class AccountOrderListRow : UITableViewCell {

    private let minWidth = min(UIScreen.main.bounds.width, UIScreen.main.bounds.height)

    private let orderViewLabel = UILabel()
    private let orderNumber = AccountOrderListRowPosition()
    private let orderDate = AccountOrderListRowPosition()
    private let orderCount = AccountOrderListRowPosition()
    private let orderState = AccountOrderListRowPosition()
    private let orderAmount = AccountOrderListRowPosition()
    private let firstImage = UIImageView()
    private let firstImageContainer = UIView()
    private let secondImage = UIImageView()
    private let secondImageContainer = UIView()
    private let thirdImage = UIImageView()
    private let thirdImageContainer = UIView()
    private let thirdImageMask = UIImageView()
    private let thirdImageMaskLabel = UILabel()
    private let stubImg = UIImage(named: "assets/images/publication/DraftPlaceHolder.png")
    private let insetsMake = UIEdgeInsetsMake(3, 3, 3, 3)

    var order: Order!
    var controller: UITableViewController!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addOrderViewLabel()
        addAccountOrderListRowPosition(element: orderNumber, related: orderViewLabel, name: "Номер заказа")
        addAccountOrderListRowPosition(element: orderDate, related: orderNumber, name: "Дата заказа")
        addAccountOrderListRowPosition(element: orderCount, related: orderDate, name: "Количество товаров")
        addAccountOrderListRowPosition(element: orderState, related: orderCount, name: "Статус заказа")
        addAccountOrderListRowPosition(element: orderAmount, related: orderState, name: "Сумма заказа")
        addFirstImage()
        addSecondImage()
        addThirdImage()
        addThirdImageMask()
        addThirdImageMaskLabel()
        addSeparator()
        selectionStyle = .none
    }

    private func addThirdImageMaskLabel() {
        thirdImageMask.addSubview(thirdImageMaskLabel)
        thirdImageMaskLabel.textAlignment = .center
        thirdImageMaskLabel.textColor = .white
        thirdImageMaskLabel.font = BlackFont.systemFont(ofSize: 22)
        thirdImageMaskLabel.snp.makeConstraints { m in
            m.center.equalTo(thirdImageMask)
        }
    }

    private func addThirdImageMask() {
        contentView.addSubview(thirdImageMask)
        thirdImageMask.backgroundColor = AppColors.textFieldTrans()
        thirdImageMask.layer.zPosition = 10
        thirdImageMask.snp.makeConstraints { m in
            m.edges.equalTo(thirdImageContainer)
        }
    }

    private func addThirdImage() {
        contentView.addSubview(thirdImageContainer)
        thirdImageContainer.addSubview(thirdImage)
        thirdImage.contentMode = firstImage.contentMode
        thirdImageContainer.layer.borderWidth = firstImageContainer.layer.borderWidth
        thirdImageContainer.layer.borderColor = firstImageContainer.layer.borderColor
        thirdImageContainer.snp.makeConstraints { m in
            m.top.equalTo(firstImageContainer)
            m.left.equalTo(secondImageContainer.snp.right).offset(5)
            m.width.equalTo(firstImageContainer)
            m.height.equalTo(firstImageContainer)
        }
        thirdImage.snp.makeConstraints { m in
            m.edges.equalTo(thirdImageContainer).inset(insetsMake)
        }
    }

    private func addSecondImage() {
        contentView.addSubview(secondImageContainer)
        secondImageContainer.addSubview(secondImage)
        secondImage.contentMode = firstImage.contentMode
        secondImageContainer.layer.borderWidth = firstImageContainer.layer.borderWidth
        secondImageContainer.layer.borderColor = firstImageContainer.layer.borderColor
        secondImageContainer.snp.makeConstraints { m in
            m.top.equalTo(firstImageContainer)
            m.left.equalTo(firstImageContainer.snp.right).offset(5)
            m.width.equalTo(firstImageContainer)
            m.height.equalTo(firstImageContainer)
        }
        secondImage.snp.makeConstraints { m in
            m.edges.equalTo(secondImageContainer).inset(insetsMake)
        }
    }

    private func addFirstImage() {
        contentView.addSubview(firstImageContainer)
        firstImageContainer.addSubview(firstImage)
        firstImage.contentMode = .scaleAspectFit
        firstImageContainer.layer.borderWidth = 1
        firstImageContainer.layer.borderColor = AppColors.separator().cgColor
        firstImageContainer.snp.makeConstraints { m in
            m.top.equalTo(orderAmount.snp.bottom).offset(15)
            m.left.equalTo(contentView).inset(15)
            let w = minWidth / 3.3
            let h = (w / 3) * 4
            m.width.equalTo(w)
            m.height.equalTo(h)
        }
        firstImage.snp.makeConstraints { m in
            m.edges.equalTo(firstImageContainer).inset(insetsMake)
        }
    }

    private func addSeparator() {
        let separator = UIView()
        separator.backgroundColor = AppColors.transparent()
        contentView.addSubview(separator)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(12)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }
    }

    private func addAccountOrderListRowPosition(element: AccountOrderListRowPosition, related: UIView, name: String) {
        contentView.addSubview(element)
        element.name.text = name + ":"
        element.snp.makeConstraints { m in
            m.left.equalTo(contentView)
            m.right.equalTo(contentView)
            m.top.equalTo(related.snp.bottom).offset(10)
            m.height.equalTo(20)
        }
    }

    private func addOrderViewLabel() {
        contentView.addSubview(orderViewLabel)
        orderViewLabel.font = BlackFont.systemFont(ofSize: 16)
        orderViewLabel.attributedText = NSAttributedString(string: "Просмотр заказа", attributes: [NSUnderlineStyleAttributeName: NSUnderlineStyle.styleSingle.rawValue])
        orderViewLabel.snp.makeConstraints { m in
            m.left.equalTo(contentView).inset(15)
            m.top.equalTo(contentView).inset(15)
        }
    }

    func render(order: Order, controller: UITableViewController) {
        self.order = order
        self.controller = controller
        orderNumber.value.text = String(order.id)
        if let createTime = order.createTime {
            orderDate.value.text = GlobalProvider.instance.dateTimeConverter.fromDateTimeWithTimeZone(dateString: createTime, format: "dd.MM.yyyy")
        }
        else {
            orderDate.value.text = "Дата не указана"
        }
        orderCount.value.text = String(order.items.count)
        orderState.value.text = order.state
        orderAmount.value.text = String(order.price + order.deliveryCost) + " ₽"

        renderFirstImage(order: order)
        renderSecondImage(order: order)
        renderThirdImage(order: order)
        renderThirdImageMask(order: order)
    }

    private func renderFirstImage(order: Order) {
        firstImageContainer.isHidden = !(order.items.count >= 1)
        firstImage.image = stubImg
        if(order.items.count >= 1) {
            if let im = order.items[0].imageUrl {
                let url = URL(string: ApiRequester.domain + im)!
                firstImage.af_setImage(withURL: url, placeholderImage: stubImg)
            }
        }
    }

    private func renderSecondImage(order: Order) {
        secondImageContainer.isHidden = !(order.items.count >= 2)
        secondImage.image = stubImg
        if(order.items.count >= 2) {
            if let im = order.items[1].imageUrl {
                let url = URL(string: ApiRequester.domain + im)!
                secondImage.af_setImage(withURL: url, placeholderImage: stubImg)
            }
        }
    }

    private func renderThirdImage(order: Order) {
        thirdImageContainer.isHidden = !(order.items.count >= 3)
        thirdImage.image = stubImg
        if(order.items.count >= 3) {
            if let im = order.items[1].imageUrl {
                let url = URL(string: ApiRequester.domain + im)!
                thirdImage.af_setImage(withURL: url, placeholderImage: stubImg)
            }
        }
    }

    private func renderThirdImageMask(order: Order) {
        thirdImageContainer.layer.borderWidth = order.items.count >= 4 ? 0 : 1
        thirdImageMask.isHidden = !(order.items.count >= 4)
        thirdImageMaskLabel.text = "+" + String(order.items.count - 3)
    }


    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

class AccountOrderListRowPosition : UIView {

    let name = UILabel()
    let value = UILabel()

    override init(frame: CGRect) {
        super.init(frame: frame)
        addName()
        addValue()
    }

    private func addValue() {
        addSubview(value)
        value.font = MediumFont.systemFont(ofSize: 14)
        value.text = "345678"
        value.numberOfLines = 2
        value.snp.makeConstraints { m in
            m.left.equalTo(self.snp.centerX)
            m.right.equalTo(self)
            m.centerY.equalTo(self)
        }
    }

    private func addName() {
        addSubview(name)
        name.font = BlackFont.systemFont(ofSize: 14)
        name.text = "Номер заказа"
        name.snp.makeConstraints { m in
            m.left.equalTo(self).inset(15)
            m.centerY.equalTo(self)
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}