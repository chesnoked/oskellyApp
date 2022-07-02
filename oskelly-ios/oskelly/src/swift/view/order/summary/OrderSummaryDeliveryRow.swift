//
// Created by Виталий Хлудеев on 23.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class OrderSummaryDeliveryRow : UITableViewCell {

    private let title = UILabel()
    private let address = UILabel()
    private let topSeparator = UIView()
    private let separator = UIView()

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addTopSeparator()
        addTitle()
        addSeparator()
        addAddress()
    }

    private func addSeparator() {
        separator.backgroundColor = AppColors.grayLabel()
        contentView.addSubview(separator)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(topSeparator)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }
    }

    private func addTopSeparator() {
        topSeparator.backgroundColor = AppColors.grayLabel()
        contentView.addSubview(topSeparator)
        topSeparator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(8)
            make.top.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }
    }

    private func addAddress() {
        contentView.addSubview(address)
        address.font = MediumFont.systemFont(ofSize: 13)
        address.textColor = .gray
        address.numberOfLines = 0
        address.snp.makeConstraints({ m in
            m.left.equalTo(title)
            m.top.equalTo(title.snp.bottom).offset(10)
            m.right.equalTo(contentView).inset(10)
            m.bottom.equalTo(contentView).inset(20)
        })
    }

    private func addTitle() {
        contentView.addSubview(title)
        title.text = "Адрес доставки"
        title.font = MediumFont.systemFont(ofSize: 16)
        title.snp.makeConstraints({ m in
            m.left.equalTo(contentView).inset(17)
            m.top.equalTo(topSeparator.snp.bottom).offset(10)
        })
    }

    func render(_ d : DeliveryRequisite) {
        let deliveryName = d.deliveryName.map({ $0 + ",\n" }) ?? ""
        let deliveryZipCode = d.deliveryZipCode.map({ $0 + ", " }) ?? ""
        let deliveryCountry = d.deliveryCountry.map({ $0 + ", " }) ?? ""
        let deliveryCity = d.deliveryCity.map({ $0 + ", \n" }) ?? ""
        let deliveryAddress = d.deliveryAddress.map({ $0 + ", \n" }) ?? ""
        let deliveryPhone = d.deliveryPhone ?? ""
        address.text =
                deliveryName +
                        deliveryZipCode +
                        deliveryCountry +
                        deliveryCity +
                        deliveryAddress +
                        deliveryPhone

    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}