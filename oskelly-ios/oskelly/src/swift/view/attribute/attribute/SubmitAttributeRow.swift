//
// Created by Виталий Хлудеев on 18.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SubmitAttributeRow: UITableViewCell {

    let attributeNameLabel = UILabel()
    let selectedValueLabel = UILabel()
    let separator = UIView()

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)

        contentView.addSubview(attributeNameLabel)
        attributeNameLabel.font = MediumFont.systemFont(ofSize: 14)
        attributeNameLabel.snp.makeConstraints { (make) -> Void in
            make.centerY.equalTo(contentView)
            make.left.equalTo(contentView).inset(10)
        }
        contentView.addSubview(selectedValueLabel)
        selectedValueLabel.font = BoldFont.systemFont(ofSize: 14)
        selectedValueLabel.textColor = AppColors.lato()
        selectedValueLabel.snp.makeConstraints { (make) -> Void in
            make.centerY.equalTo(contentView)
            make.right.equalTo(contentView).inset(10)
        }

        separator.backgroundColor = AppColors.separator()
        contentView.addSubview(separator)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)//.inset(10)
        }
    }

    func render(attributeName: String, selectedValue: String?) {
        attributeNameLabel.text = attributeName
        selectedValueLabel.text = selectedValue
    }

    func render(attributeName: String, selectedValue: String?, _ isBlackColor: Bool) {
        render(attributeName: attributeName, selectedValue: selectedValue)
        if(isBlackColor) {
            selectedValueLabel.textColor = .black
        }
        else {
            selectedValueLabel.textColor = AppColors.lato()
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}