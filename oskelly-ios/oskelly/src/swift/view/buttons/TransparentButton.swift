//
// Created by Виталий Хлудеев on 09.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class TransparentButton : UIButton {

    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = nil
        setTitleColor(.black, for: .normal)
        setTitleColor(.gray, for: .highlighted)

        titleLabel?.font = MediumFont.systemFont(ofSize: 11)
        layer.borderWidth = 1
        layer.borderColor = UIColor.black.cgColor
        self.snp.makeConstraints { make in
            make.height.equalTo(30)
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}