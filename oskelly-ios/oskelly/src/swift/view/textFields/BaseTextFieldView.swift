//
// Created by Виталий Хлудеев on 31.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class BaseTextFieldView : UITableViewCell, UITextFieldDelegate {

    static let height: CGFloat = 66.0

    var label = UILabel()

    var textField = UITextField()

    var textFieldContainer = UIView()

    var numbersOnly: Bool = false

    let separator = UIView()

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)

        self.addSubview(label)
        label.font = BoldFont.systemFont(ofSize: 13)
        label.text = "Модель"
        label.snp.makeConstraints { m in
            m.top.equalTo(self).inset(10)
            m.left.equalTo(self).inset(10)
        }

        self.addSubview(textFieldContainer)
//        textFieldContainer.layer.borderWidth = 1
//        textFieldContainer.layer.borderColor = AppColors.separator().cgColor
        textFieldContainer.snp.makeConstraints { m in
            m.top.equalTo(label.snp.bottom).offset(5)
            m.left.equalTo(self).inset(10)
            m.right.equalTo(self).inset(10)
            m.height.equalTo(35)
        }

        textFieldContainer.addSubview(textField)
        textField.tintColor = AppColors.lato()
        textField.font = MediumFont.systemFont(ofSize: 14)
        textField.textColor = AppColors.textField()
        textField.delegate = self
        textField.snp.makeConstraints { m in
            m.left.equalTo(textFieldContainer)
            m.right.equalTo(textFieldContainer).inset(4)
            m.top.equalTo(textFieldContainer).inset(4)
            m.bottom.equalTo(textFieldContainer).inset(4)
        }

        self.addSubview(separator)
        separator.backgroundColor = AppColors.separator()
        separator.snp.makeConstraints { m in
            m.height.equalTo(1)
            m.bottom.equalTo(self)
            m.left.equalTo(self)
            m.right.equalTo(self)
        }
        textField.addTarget(self, action: #selector(self.onChange), for: .editingChanged)
    }

    func render(name: String, value: String?, numbersOnly: Bool) {
        label.text = name
        let attributes = [
            NSForegroundColorAttributeName: UIColor(red: 216/255, green: 216/255, blue: 216/255, alpha: 1),
            NSFontAttributeName : MediumFont.systemFont(ofSize: 12)
        ]
        textField.attributedPlaceholder = NSAttributedString(string: name + "...", attributes:attributes)
        self.numbersOnly = numbersOnly
        if(numbersOnly) {
            textField.keyboardType = .numberPad
        }
        else {
            textField.keyboardType = .default
        }
        textField.text = value
        textField.reloadInputViews()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    public func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        if(numbersOnly) {
            let allowedCharacters = CharacterSet.decimalDigits
            let characterSet = CharacterSet(charactersIn: string)
            return allowedCharacters.isSuperset(of: characterSet)
        }
        return true
    }

    public func getValue() -> String? {
        return textField.text
    }

    func onChange() {

    }
}