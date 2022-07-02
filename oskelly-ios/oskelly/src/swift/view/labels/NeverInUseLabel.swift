//
// Created by Виталий Хлудеев on 16.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class NeverInUseLabel : UILabel {

    init() {
        super.init(frame: CGRect())
        self.text = "С биркой"
        self.textColor = .white
        self.textAlignment = .center
        self.font = BoldFont.systemFont(ofSize: 6.0)
        self.layer.backgroundColor = UIColor.darkGray.cgColor
    }

    func setRadius(radius: CGFloat) {
        self.layer.cornerRadius = radius
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
