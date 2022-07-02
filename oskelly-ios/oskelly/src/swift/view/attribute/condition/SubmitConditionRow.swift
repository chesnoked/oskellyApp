//
// Created by Виталий Хлудеев on 27.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SubmitConditionRow: UITableViewCell {

    let name = UILabel()

    let descr = UILabel()

    let separator = UIView()

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.addSubview(name)
        name.text = "С биркой"
        name.font = BoldFont.systemFont(ofSize: 16)
        name.snp.makeConstraints { m in
            m.left.equalTo(contentView).inset(10)
            m.right.equalTo(contentView).inset(10)
            m.top.equalTo(contentView).inset(10)
        }

        contentView.addSubview(descr)
        descr.text = "Вещь ни разу не носили"
        descr.numberOfLines = 3
        descr.font = MediumFont.systemFont(ofSize: 13)
        descr.snp.makeConstraints { m in
            m.left.equalTo(name)
            m.right.equalTo(name)
            m.top.equalTo(name.snp.bottom).offset(10)
        }

        contentView.addSubview(separator)
        separator.backgroundColor = AppColors.separator()
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1.0)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }
    }

    func render(condition: ProductCondition) {
        name.text = condition.name
        descr.text = condition.descr
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}