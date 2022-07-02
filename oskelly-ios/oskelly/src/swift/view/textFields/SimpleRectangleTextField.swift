//
// Created by Виталий Хлудеев on 23.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SimpleRectangleTextField : UIView {

    let textField = UITextField()

    override init(frame: CGRect) {
        super.init(frame: frame)
        layer.borderWidth = 1
        layer.borderColor = UIColor.lightGray.cgColor

        addSubview(textField)
        textField.snp.makeConstraints({ m in
            m.centerX.equalTo(self)
            m.centerY.equalTo(self)
            m.left.equalTo(self).inset(10)
            m.right.equalTo(self).inset(10)
        })

        textField.tintColor = AppColors.lato()
        textField.font = MediumFont.systemFont(ofSize: 14)
        textField.textColor = AppColors.textField()
    }

    func setPlaceHolder(_ placeholder: String) {
        let attributes = [
            NSForegroundColorAttributeName: UIColor(red: 216/255, green: 216/255, blue: 216/255, alpha: 1),
            NSFontAttributeName : MediumFont.systemFont(ofSize: 14)
        ]
        textField.attributedPlaceholder = NSAttributedString(string: placeholder, attributes:attributes)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}