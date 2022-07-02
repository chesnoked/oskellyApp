//
// Created by Виталий Хлудеев on 24.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class OrderSummaryDiscountRow : UITableViewCell {

    private let title = UILabel()
    private let descr = UILabel()
    private let separator = UIView()

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addTitle()
        addSeparator()
        addDescr()
    }

    private func addSeparator() {
        separator.backgroundColor = AppColors.grayLabel()
        contentView.addSubview(separator)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(8)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }
    }

    private func addDescr() {
        contentView.addSubview(descr)
        descr.font = MediumFont.systemFont(ofSize: 13)
        descr.textColor = .gray
        descr.numberOfLines = 0
        descr.snp.makeConstraints({ m in
            m.left.equalTo(title)
            m.top.equalTo(title.snp.bottom).offset(10)
            m.right.equalTo(contentView).inset(10)
            m.bottom.equalTo(contentView).inset(20)
        })
    }

    private func addTitle() {
        contentView.addSubview(title)
        title.text = "Сертификат/промокод"
        title.font = MediumFont.systemFont(ofSize: 16)
        title.snp.makeConstraints({ m in
            m.left.equalTo(contentView).inset(17)
            m.top.equalTo(contentView).inset(10)
        })
    }

    func render(_ d: Discount) {

        if let code = d.code {
            let codeText = "Код: " + code + "\n"
            let discountValue = d.discountValue.map({ "Скидка на товары: " + $0 + "\n" }) ?? ""
            let savingsValue = d.savingsValue.map({ "Вы экономите: " + $0 + " ₽" }) ?? ""
            descr.text =
                    codeText +
                            discountValue +
                            savingsValue
        }
        else {
            descr.text = "Отсутствует"
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}