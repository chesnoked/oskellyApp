//
// Created by Виталий Хлудеев on 11.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProfileHeaderItem : UIButton {

    var lineView: UIView!

    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = .white
        titleLabel?.font = MediumFont.systemFont(ofSize: 16)
        setTitleColor(.gray, for: .normal)
        setTitleColor(AppColors.lato(), for: .disabled)

        lineView = UIView(frame: CGRect(x: 0, y: frame.size.height - 1, width: frame.size.width, height: 1))
        lineView.backgroundColor = AppColors.separator()
        addSubview(lineView)
    }

    func select() {
        lineView.frame = CGRect(x: 0, y: frame.size.height - 1.5, width: frame.size.width, height: 1.5)
        lineView.backgroundColor = AppColors.lato()
        self.isEnabled = false
    }

    func deSelect() {
        lineView.frame = CGRect(x: 0, y: frame.size.height - 1, width: frame.size.width, height: 1)
        lineView.backgroundColor = AppColors.separator()
        self.isEnabled = true
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}