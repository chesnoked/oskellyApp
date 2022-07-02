//
// Created by Виталий Хлудеев on 17.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class StepNumberLabel : UILabel {

    init() {
        super.init(frame: CGRect())
        self.text = "1"
        self.textColor = AppColors.lato()
        self.textAlignment = .center
        self.font = MediumFont.systemFont(ofSize: 16)
        self.layer.backgroundColor = UIColor.white.cgColor
        self.layer.borderWidth = 1.5
        self.layer.borderColor = AppColors.lato().cgColor
    }

    func setRadius(radius: CGFloat) {
        self.layer.cornerRadius = radius
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}