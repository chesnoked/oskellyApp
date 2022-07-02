//
// Created by Виталий Хлудеев on 14.03.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class SimpleRectangleSearchTextField : SimpleRectangleTextField {

    override init(frame: CGRect) {
        super.init(frame: frame)
        layer.borderWidth = 0
        layer.cornerRadius = 7
        backgroundColor = UIColor(red: 210/255, green: 213/255, blue: 215/255, alpha: 1)
        setPlaceHolder("Поиск")

        let imageView = UIImageView();
        let image = UIImage(named: "assets/images/Search Icon.png");
        imageView.image = image;
        textField.addSubview(imageView)
        imageView.snp.makeConstraints({ m in
            m.left.equalTo(textField)
            m.centerY.equalTo(textField)
        })
        let leftView = UIView.init(frame: CGRect(x: 10, y: 0, width: 21, height: 30))
        textField.leftView = leftView;
        textField.leftViewMode = UITextFieldViewMode.always
    }

    override func setPlaceHolder(_ placeholder: String) {
        let attributes = [
            NSForegroundColorAttributeName: UIColor(red: 122/255, green: 122/255, blue: 122/255, alpha: 1),
            NSFontAttributeName : MediumFont.systemFont(ofSize: 14)
        ]
        textField.attributedPlaceholder = NSAttributedString(string: placeholder, attributes:attributes)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}