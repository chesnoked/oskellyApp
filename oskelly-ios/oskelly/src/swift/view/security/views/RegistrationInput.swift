//
//  RegistrationInput.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 12.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class RegistrationInput: UIView {
    
    let textField = UITextField()
    let icon = UIImageView()
    let separatop = UIView()
    let iconContainer = UIView()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        addSubview(textField)
        addSubview(iconContainer)
        iconContainer.addSubview(icon)
        addSubview(separatop)
        self.snp.makeConstraints { make in
            make.height.equalTo(48.0)
        }
        iconContainer.snp.makeConstraints { make in
            make.top.equalTo(self)
            make.bottom.equalTo(self)
            make.left.equalTo(self).inset(5)
            make.width.equalTo(50)
        }
        icon.snp.makeConstraints { make in
            make.center.equalTo(iconContainer)
        }
        textField.tintColor = AppColors.lato()
        textField.font = MediumFont.systemFont(ofSize: 14)
        textField.textColor = AppColors.textField()
        textField.autocorrectionType = .no
        textField.snp.makeConstraints {make in
            make.centerY.equalTo(self)
            make.left.equalTo(iconContainer.snp.right)
            make.right.equalTo(self).inset(20)
        }
        separatop.backgroundColor = UIColor(red: 236/255, green: 236/255, blue: 236/255, alpha: 1)
        separatop.snp.makeConstraints {make in
            make.bottom.equalTo(self)
            make.height.equalTo(1)
            make.left.equalTo(self).inset(5)
            make.right.equalTo(self).inset(5)
        }
    }
    
    func setup(imageName: String, placeHolder: String, isSecure: Bool, needCapitalization: Bool) {
        setup(imageName: imageName, placeHolder: placeHolder, isSecure: isSecure, needCapitalization: needCapitalization, keyboardType: nil)
    }

    func setup(imageName: String, placeHolder: String, isSecure: Bool, needCapitalization: Bool, keyboardType: UIKeyboardType?) {
        let image = UIImage(named: imageName)
        icon.image = image

        let attributes = [
            NSForegroundColorAttributeName: UIColor(red: 216/255, green: 216/255, blue: 216/255, alpha: 1),
            NSFontAttributeName : MediumFont.systemFont(ofSize: 14)
        ]
        textField.attributedPlaceholder = NSAttributedString(string: placeHolder, attributes:attributes)
        textField.isSecureTextEntry = isSecure
        if !needCapitalization {
            textField.autocapitalizationType = .none
        }
        else {
            textField.autocapitalizationType = .words
        }
        if let value = keyboardType { textField.keyboardType = value }
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
