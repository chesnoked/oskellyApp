//
// Created by Виталий Хлудеев on 18.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SaleSubmitAddressRow : UITableViewCell, UITextFieldDelegate {

    var textField = UITextField()
    var textFieldContainer = UIView()
    let separator = UIView()
    var keyboardType : UIKeyboardType!
    let attributeNameLabel = UILabel()
    let maskX = UIView()

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {

        super.init(style: style, reuseIdentifier: reuseIdentifier)

        contentView.addSubview(attributeNameLabel)
        attributeNameLabel.font = MediumFont.systemFont(ofSize: 14)
        attributeNameLabel.snp.makeConstraints { (make) -> Void in
            make.centerY.equalTo(contentView)
            make.left.equalTo(contentView).inset(10)
        }

        self.addSubview(textFieldContainer)
        textFieldContainer.snp.makeConstraints { m in
            m.top.equalTo(contentView).inset(3)
            m.bottom.equalTo(contentView).inset(3)
            m.left.equalTo(contentView).inset(10)
            m.right.equalTo(contentView).inset(10)
        }

        textFieldContainer.addSubview(textField)
        textField.tintColor = AppColors.lato()
        textField.font = MediumFont.systemFont(ofSize: 14)
        textField.delegate = self
        textField.textAlignment = .right
        textField.snp.makeConstraints { m in
            m.top.equalTo(textFieldContainer)
            m.bottom.equalTo(textFieldContainer)
            m.right.equalTo(textFieldContainer)
            m.left.equalTo(attributeNameLabel.snp.right)
        }

        self.addSubview(separator)
        separator.backgroundColor = AppColors.separator()
        separator.snp.makeConstraints { m in
            m.height.equalTo(1)
            m.bottom.equalTo(self)
            m.left.equalTo(self)
            m.right.equalTo(self)
        }

        contentView.addSubview(maskX)
        maskX.snp.makeConstraints({ m in
            m.edges.equalTo(contentView)
        })
        maskX.backgroundColor = UIColor(red: 1, green: 1, blue: 1, alpha: 0.6)
        maskX.layer.zPosition = 10
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func getValue() -> String? {
        return textField.text
    }

    func render(name: String, value: String?, keyboardType: UIKeyboardType, enable: Bool) {
        self.keyboardType = keyboardType
        let attributes = [
            NSForegroundColorAttributeName: UIColor(red: 216/255, green: 216/255, blue: 216/255, alpha: 1),
            NSFontAttributeName : MediumFont.systemFont(ofSize: 12)
        ]
        textField.attributedPlaceholder = NSAttributedString(string: name, attributes:attributes)
        textField.keyboardType = keyboardType
        textField.text = value
        textField.textColor = enable ? AppColors.lato() : AppColors.latoTrans()
        textField.font = enable ? BoldFont.systemFont(ofSize: 14) : MediumFont.systemFont(ofSize: 14)
        attributeNameLabel.text = name
        textField.reloadInputViews()
        textField.isEnabled = enable
        maskX.isHidden = enable
    }
}