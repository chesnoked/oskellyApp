//
// Created by Виталий Хлудеев on 10.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProductOfferHistoryRow : UITableViewCell {

    private let img = ProfilePhotoView()
    private let container = UIView()
    private let baseContainer = UIView()
    private let date = UILabel()
    private let message = UILabel()

    private let img2 = ProfilePhotoView()
    private let baseContainer2 = UIView()
    private let container2 = UIView()
    private let date2 = UILabel()
    private let message2 = UILabel()
    private let price = UILabel()

    private let imgWidth : CGFloat = 54.0
    private let rowHeight: CGFloat = 54.0 + 40.0

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addBaseContainer()
        addImg()
        addContainer()
        addDate()
        addMessage()

        addBaseContainer2()
        addImg2()
        addContainer2()
        addDate2()
        addMessage2()
        addPrice()
    }

    private func addBaseContainer() {
        contentView.addSubview(baseContainer)
        baseContainer.snp.makeConstraints({ m in
            m.height.equalTo(rowHeight)
            m.top.equalTo(contentView)
            m.left.equalTo(contentView)
            m.right.equalTo(contentView)
        })
    }

    private func addImg() {
        baseContainer.addSubview(img)
        img.snp.makeConstraints({m in
            m.left.equalTo(baseContainer).inset(10)
            m.centerY.equalTo(baseContainer)
            m.height.equalTo(imgWidth)
            m.width.equalTo(imgWidth)
        })
        img.layer.cornerRadius = imgWidth / 2
    }

    private func addContainer() {
        baseContainer.addSubview(container)
        container.backgroundColor = AppColors.grayLabel()
        container.snp.makeConstraints({ m in
            m.height.equalTo(80)
            m.right.equalTo(baseContainer).inset(10)
            m.left.equalTo(img.snp.right).offset(10)
            m.centerY.equalTo(img)
        })
    }

    private func addPrice() {
        container2.addSubview(price)
        price.font = message2.font
        price.textColor = message2.textColor
        price.text = "35 000 ₽"
        price.snp.makeConstraints({m in
            m.right.equalTo(message2)
            m.bottom.equalTo(container2).inset(10)
        })
    }

    private func addMessage2() {
        container2.addSubview(message2)
        message2.text = "Ваше предложение:"
        message2.font = date.font
        message2.textColor = date.textColor
        message2.snp.makeConstraints({m in
            m.right.equalTo(date2)
            m.centerY.equalTo(container2)
        })
    }

    private func addDate2() {
        container2.addSubview(date2)
        date2.text = "2018-01-09 21:00"
        date2.font = date.font
        date2.textColor = date.textColor
        date2.snp.makeConstraints({m in
            m.top.equalTo(container2).inset(10)
            m.right.equalTo(container2).inset(10)
        })
    }

    private func addBaseContainer2() {
        contentView.addSubview(baseContainer2)
        baseContainer2.snp.makeConstraints({ m in
            m.height.equalTo(rowHeight)
            m.top.equalTo(baseContainer.snp.bottom)
            m.left.equalTo(contentView)
            m.right.equalTo(contentView)
            m.bottom.equalTo(contentView)
        })
    }

    private func addImg2() {
        baseContainer2.addSubview(img2)
        img2.snp.makeConstraints({m in
            m.right.equalTo(baseContainer2).inset(10)
            m.centerY.equalTo(baseContainer2)
            m.height.equalTo(imgWidth)
            m.width.equalTo(imgWidth)
        })
        img2.layer.cornerRadius = img.layer.cornerRadius
    }

    private func addContainer2() {

        let v = UIView() // здесь нужна прослойка
        baseContainer2.addSubview(v)
        v.snp.makeConstraints({m in
            m.width.equalTo(10)
            m.right.equalTo(img2.snp.left)
        })

        baseContainer2.addSubview(container2)
        container2.backgroundColor = container.backgroundColor
        container2.snp.makeConstraints({ m in
            m.height.equalTo(container)
            m.left.equalTo(baseContainer2).inset(10)
            m.right.equalTo(v.snp.left)
            m.centerY.equalTo(img2)
        })
    }

    private func addMessage() {
        container.addSubview(message)
        message.text = "Продавец отклонил ваше предложение"
        message.font = date.font
        message.textColor = date.textColor
        message.snp.makeConstraints({m in
            m.left.equalTo(date)
            m.centerY.equalTo(container)
        })
    }

    private func addDate() {
        container.addSubview(date)
        date.text = "2018-01-09 21:00"
        date.font = MediumFont.systemFont(ofSize: 12)
        date.textColor = AppColors.grayLabelText()
        date.snp.makeConstraints({m in
            m.top.equalTo(container).inset(10)
            m.left.equalTo(container).inset(10)
        })
    }

    func render(product: Product, offerHistory: OfferHistory?) {
        img.setDefaultImage()
        if let im = product.avatar {
            let url = URL(string: ApiRequester.domain + im)!
            img.af_setImage(withURL: url, placeholderImage: nil)
        }
        GlobalProvider.instance.accountProvider.getCurrent(completionHandler: {a in
            if let im = a.avatar {
                let url = URL(string: ApiRequester.domain + im)!
                self.img2.af_setImage(withURL: url, placeholderImage: nil)
            }
        }, needSynchronize: false)

        if let v = offerHistory?.offeredAt {
            date.text = GlobalProvider.instance.dateTimeConverter.fromDateTimeWithTimeZone(dateString: v, format: "dd.MM.yyyy HH:mm")
        }
        else {
            date.text = ""
        }
        date2.text = date.text

        message.text = offerHistory?.acceptedMessage

        price.text = (offerHistory?.price ?? "0") + " ₽"

        message2.text = product.offerRelated?.offersHistory?.last?.offeredAt == offerHistory?.offeredAt ? "Ваше предложение:" : "Повторное предложение:"

        baseContainer.snp.remakeConstraints({ m in
            m.height.equalTo(offerHistory?.accepted != nil ? rowHeight : 0)
            m.top.equalTo(contentView)
            m.left.equalTo(contentView)
            m.right.equalTo(contentView)
        })
        baseContainer.isHidden = offerHistory?.accepted == nil
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}