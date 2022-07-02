//
//  DarkButton.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 06.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class DarkButton: UIButton {
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = AppColors.darkButton()
        titleLabel?.font = BlackFont.systemFont(ofSize: 14)
        setTitleColor(.gray, for: .highlighted)
        setTitleColor(.white, for: .normal)
        self.snp.makeConstraints { make in
            make.height.equalTo(50)
        }
    }

    func disable() {
        isEnabled = false
        backgroundColor = AppColors.separator()
    }

    func enable() {
        isEnabled = true
        backgroundColor = AppColors.darkButton()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
