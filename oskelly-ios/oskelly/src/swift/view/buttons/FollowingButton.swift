//
// Created by Виталий Хлудеев on 16.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class FollowingButton : UIButton {

    override init(frame: CGRect) {
        super.init(frame: frame)
        setTitleColor(.gray, for: .highlighted)
        isHidden = true
        titleLabel?.font = BoldFont.systemFont(ofSize: 14)
        layer.borderWidth = 1
        layer.borderColor = UIColor.black.cgColor
        self.snp.makeConstraints { make in
            make.height.equalTo(50)
        }
    }

    func render(doIFollow: Bool?) {
        isHidden = false
        if(!(doIFollow ?? false)) {
            backgroundColor = .black
            setTitleColor(.white, for: .normal)
            setTitle("Подписаться", for: .normal)
        }
        else {
            backgroundColor = .white
            setTitleColor(.black, for: .normal)
            setTitle("Отписаться", for: .normal)
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}